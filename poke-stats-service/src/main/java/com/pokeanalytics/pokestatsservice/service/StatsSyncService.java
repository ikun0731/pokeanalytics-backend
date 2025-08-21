package com.pokeanalytics.pokestatsservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokeanalytics.pokestatsservice.entity.PokemonUsageStats;
import com.pokeanalytics.pokestatsservice.entity.StatsSnapshot;
import com.pokeanalytics.pokestatsservice.entity.UsageDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 对战数据同步服务
 * 负责从本地JSON文件同步对战数据统计到数据库
 */
@Service
@Slf4j
public class StatsSyncService {

    /**
     * 支持的对战格式和天梯分数线配置
     * key: 对战格式
     * value: 支持的天梯分数线列表
     */
    private static final Map<String, List<Integer>> TARGETS = Map.of(
            "gen9ou", List.of(1500, 1695, 1825),
            "gen9ubers", List.of(1500, 1630, 1760),
            "gen9nationaldex", List.of(1500, 1630, 1760),
            "gen9nationaldexubers", List.of(1500, 1630, 1760)
    );

    /**
     * 资源加载器
     * 用于读取本地JSON文件
     */
    private final ResourceLoader resourceLoader;
    
    /**
     * JSON对象映射器
     * 用于解析JSON文件
     */
    private final ObjectMapper objectMapper;
    
    /**
     * 统计快照服务
     * 用于管理统计快照记录
     */
    private final StatsSnapshotService statsSnapshotService;
    
    /**
     * 宝可梦使用率统计服务
     * 用于管理宝可梦使用率数据
     */
    private final PokemonUsageStatsService pokemonUsageStatsService;
    
    /**
     * 使用详情服务
     * 用于管理宝可梦特性、道具、技能、队友、太晶属性等使用详情
     */
    private final UsageDetailsService usageDetailsService;

    /**
     * 构造函数，通过依赖注入初始化所需服务
     *
     * @param resourceLoader 资源加载器
     * @param statsSnapshotService 统计快照服务
     * @param pokemonUsageStatsService 宝可梦使用率统计服务
     * @param usageDetailsService 使用详情服务
     */
    public StatsSyncService(ResourceLoader resourceLoader, 
                           StatsSnapshotService statsSnapshotService, 
                           PokemonUsageStatsService pokemonUsageStatsService, 
                           UsageDetailsService usageDetailsService) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = new ObjectMapper();
        this.statsSnapshotService = statsSnapshotService;
        this.pokemonUsageStatsService = pokemonUsageStatsService;
        this.usageDetailsService = usageDetailsService;
    }

    /**
     * 同步月度对战数据
     * 定时任务，每月8日凌晨4点自动执行，同步上上个月的对战数据
     * 从本地JSON文件同步到数据库
     */
    @Async
    @Scheduled(cron = "0 0 4 8 * ?")
    public void syncMonthlyStats() {
        log.info("【定时任务】开始执行每月Smogon数据同步...");
        String lastMonth = YearMonth.now().minusMonths(2).format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // 遍历所有支持的对战格式和分数线组合
        TARGETS.forEach((format, cutoffs) -> cutoffs.forEach(cutoff -> {
            try {
                // 检查数据是否已存在，避免重复同步
                QueryWrapper<StatsSnapshot> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("format", format)
                        .eq("stats_month", lastMonth)
                        .eq("rating_cutoff", cutoff);
                if (statsSnapshotService.count(queryWrapper) > 0) {
                    log.info("数据已存在，跳过: format={}, cutoff={}, month={}", format, cutoff, lastMonth);
                    return;
                }
                
                // 处理统计文件
                processStatsFile(format, cutoff, lastMonth);
            } catch (Exception e) {
                log.error("处理格式 [{}], 分数线 [{}] 数据时发生错误", format, cutoff, e);
            }
        }));
        log.info("【定时任务】本月数据同步完成。");
    }

    /**
     * 处理单个统计文件
     * 读取JSON文件，解析数据并存入数据库
     * 
     * @param format 对战格式
     * @param cutoff 天梯分数线
     * @param month 统计月份（格式：YYYY-MM）
     */
    @Transactional
    public void processStatsFile(String format, int cutoff, String month) {
        // 构建文件路径
        String filePath = String.format("classpath:stats-data/%s/%s-%d.json", month, format, cutoff);
        log.info("-> 开始处理本地文件: {}", filePath);

        // 读取JSON文件内容
        String jsonString;
        try {
            log.info("--> 1. 正在读取...");
            Resource resource = resourceLoader.getResource(filePath);
            if (!resource.exists()) {
                log.warn("--> 本地文件未找到, 跳过: {}", filePath);
                return;
            }
            try (InputStream inputStream = resource.getInputStream()) {
                jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.error("--> 读取本地文件失败: {}", filePath, e);
            throw new RuntimeException("读取本地文件失败: " + filePath, e);
        }

        try {
            log.info("--> 2. 读取完成, 正在解析JSON...");
            JsonNode root = objectMapper.readTree(jsonString);
            JsonNode dataNode = root.path("data");

            log.info("--> 3. 解析完成, 准备写入数据库...");

            // 创建统计快照
            StatsSnapshot snapshot = new StatsSnapshot();
            snapshot.setFormat(format);
            snapshot.setStatsMonth(month);
            snapshot.setRatingCutoff(cutoff);
            statsSnapshotService.save(snapshot);
            Integer snapshotId = snapshot.getId();

            // 处理每个宝可梦的数据
            int rank = 1;

            for (Iterator<Map.Entry<String, JsonNode>> it = dataNode.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                String pokemonNameWithCase = entry.getKey();
                JsonNode pokemonData = entry.getValue();

                // 创建宝可梦使用率记录
                PokemonUsageStats stats = new PokemonUsageStats();
                stats.setSnapshotId(snapshotId);
                // 规范化宝可梦名称：小写并将空格替换为连字符
                stats.setPokemonNameEn(pokemonNameWithCase.toLowerCase().replace(" ", "-"));
                stats.setUsageRate(pokemonData.path("usage").decimalValue());
                stats.setRank(rank++);
                pokemonUsageStatsService.save(stats);
                Integer statsId = stats.getId();

                // 创建宝可梦使用详情列表
                List<UsageDetails> detailsForThisPokemon = new ArrayList<>();
                
                // 添加各类使用详情：特性、道具、技能、队友、太晶属性
                addUsageDetailsToList(statsId, "ability", pokemonData.path("Abilities"), detailsForThisPokemon);
                addUsageDetailsToList(statsId, "item", pokemonData.path("Items"), detailsForThisPokemon);
                addUsageDetailsToList(statsId, "move", pokemonData.path("Moves"), detailsForThisPokemon);
                addUsageDetailsToList(statsId, "teammate", pokemonData.path("Teammates"), detailsForThisPokemon);
                addUsageDetailsToList(statsId, "tera", pokemonData.path("Tera Types"), detailsForThisPokemon);

                // 批量保存使用详情
                if (!detailsForThisPokemon.isEmpty()) {
                    usageDetailsService.saveBatch(detailsForThisPokemon);
                }
            }
            log.info("--> 4. 文件处理和数据插入成功: {}", filePath);

        } catch (Exception e) {
            log.error("--> 解析或存储数据时失败: {}", filePath, e);
            throw new RuntimeException("处理统计文件失败，事务已回滚。", e);
        }
    }

    /**
     * 将使用详情数据添加到列表中
     * 
     * @param statsId 宝可梦使用率统计ID
     * @param type 详情类型（ability、item、move、teammate、tera）
     * @param detailsNode 详情数据JSON节点
     * @param detailsList 要添加到的详情列表
     */
    private void addUsageDetailsToList(Integer statsId, String type, JsonNode detailsNode, List<UsageDetails> detailsList) {
        // 如果节点不存在或不是对象，直接返回
        if (detailsNode.isMissingNode() || !detailsNode.isObject()) return;
        
        // 遍历所有字段
        detailsNode.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            // 跳过"RAW COUNT"字段
            if ("RAW COUNT".equalsIgnoreCase(key)) {
                return;
            }
            
            // 创建使用详情记录
            JsonNode valueNode = entry.getValue();
            double percentageValue = valueNode.asDouble();
            UsageDetails detail = new UsageDetails();
            detail.setStatsId(statsId);
            detail.setDetailType(type);
            detail.setDetailName(key);
            detail.setUsagePercentage(BigDecimal.valueOf(percentageValue));
            detailsList.add(detail);
        });
    }
    
    /**
     * 补充缺失的太晶数据
     * 临时补丁方法：只补充缺失的太晶数据，不修改其他记录
     */
    @Async
    @Transactional
    public void patchMissingTeraData() {
        log.info("【临时脚本】开始执行补充太晶数据任务...");
        String lastMonth = YearMonth.now().minusMonths(2).format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // 遍历所有支持的对战格式和分数线组合
        TARGETS.forEach((format, cutoffs) -> cutoffs.forEach(cutoff -> {
            log.info("-> 正在检查: format={}, cutoff={}", format, cutoff);
            try {
                // 1. 查找现有的快照，如果快照不存在，直接跳过
                StatsSnapshot snapshot = statsSnapshotService.getOne(new QueryWrapper<StatsSnapshot>()
                        .eq("format", format)
                        .eq("stats_month", lastMonth)
                        .eq("rating_cutoff", cutoff));

                if (snapshot == null) {
                    log.info("--> 快照不存在，跳过文件处理。");
                    return;
                }

                // 2. 读取对应的JSON文件
                String filePath = String.format("classpath:stats-data/%s/%s-%d.json", lastMonth, format, cutoff);
                Resource resource = resourceLoader.getResource(filePath);
                if (!resource.exists()) return;

                // 解析JSON文件
                JsonNode root = objectMapper.readTree(resource.getInputStream());
                JsonNode dataNode = root.path("data");

                // 3. 遍历JSON中的每一个宝可梦
                dataNode.fields().forEachRemaining(entry -> {
                    // 获取宝可梦名称和数据
                    String pokemonNameWithCase = entry.getKey();
                    String normalizedPokemonName = pokemonNameWithCase.toLowerCase().replace(" ", "-");
                    JsonNode pokemonData = entry.getValue();

                    // 4. 查找该宝可梦在数据库中对应的使用率记录
                    PokemonUsageStats stats = pokemonUsageStatsService.getOne(new QueryWrapper<PokemonUsageStats>()
                            .eq("snapshot_id", snapshot.getId())
                            .eq("pokemon_name_en", normalizedPokemonName));

                    if (stats != null) {
                        Integer statsId = stats.getId();

                        // 5. 检查是否已存在太晶数据，如果存在则跳过
                        long existingTeraCount = usageDetailsService.count(new QueryWrapper<UsageDetails>()
                                .eq("stats_id", statsId).eq("detail_type", "tera"));

                        if (existingTeraCount > 0) {
                            return; // 已存在，跳到下一个宝可梦
                        }

                        // 6. 如果不存在，则补充太晶数据
                        JsonNode terasNode = pokemonData.path("Tera Types");
                        if (!terasNode.isMissingNode()) {
                            // 创建太晶详情列表并保存
                            List<UsageDetails> teraDetails = new ArrayList<>();
                            addUsageDetailsToList(statsId, "tera", terasNode, teraDetails);
                            if (!teraDetails.isEmpty()) {
                                usageDetailsService.saveBatch(teraDetails);
                                log.info("--> 已为宝可梦 [{}] 成功补充 {} 条太晶数据", normalizedPokemonName, teraDetails.size());
                            }
                        }
                    }
                });
            } catch (Exception e) {
                log.error("处理格式 [{}], 分数线 [{}] 数据时发生错误", format, cutoff, e);
            }
        }));
        log.info("【临时脚本】补充太晶数据任务完成。");
    }
}