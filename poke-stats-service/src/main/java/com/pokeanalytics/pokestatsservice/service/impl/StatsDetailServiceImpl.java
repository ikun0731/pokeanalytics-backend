package com.pokeanalytics.pokestatsservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pokeanalytics.pokestatsservice.dto.PokemonStatsDetailDto;
import com.pokeanalytics.pokestatsservice.dto.StatDetailItemDto;
import com.pokeanalytics.pokestatsservice.entity.PokemonInfo;
import com.pokeanalytics.pokestatsservice.entity.PokemonTier;
import com.pokeanalytics.pokestatsservice.entity.PokemonUsageStats;
import com.pokeanalytics.pokestatsservice.entity.StatsSnapshot;
import com.pokeanalytics.pokestatsservice.mapper.PokemonUsageStatsMapper;
import com.pokeanalytics.pokestatsservice.mapper.StatsDetailMapper;
import com.pokeanalytics.pokestatsservice.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 宝可梦对战数据详情服务实现类
 * 提供获取宝可梦在特定对战格式和天梯分数线下的详细数据功能
 */
@Service
@Slf4j
public class StatsDetailServiceImpl implements StatsDetailService {

    /**
     * 统计快照服务
     */
    private final StatsSnapshotService statsSnapshotService;

    
    /**
     * 宝可梦基础信息服务
     */
    private final PokemonInfoService pokemonInfoService;
    
    /**
     * 宝可梦竞技分级服务
     */
    private final PokemonTierService pokemonTierService;
    
    /**
     * 统计详情数据库操作接口
     */
    private final StatsDetailMapper statsDetailMapper;
    
    /**
     * 宝可梦使用率数据库操作接口
     */
    private final PokemonUsageStatsMapper pokemonUsageStatsMapper;

    /**
     * 构造函数，通过依赖注入初始化所需服务
     *
     * @param statsSnapshotService 统计快照服务
     * @param pokemonUsageStatsService 宝可梦使用率统计服务
     * @param pokemonInfoService 宝可梦基础信息服务
     * @param pokemonTierService 宝可梦竞技分级服务
     * @param statsDetailMapper 统计详情数据库操作接口
     * @param pokemonUsageStatsMapper 宝可梦使用率数据库操作接口
     */
    public StatsDetailServiceImpl(StatsSnapshotService statsSnapshotService, 
                                 PokemonInfoService pokemonInfoService, 
                                 PokemonTierService pokemonTierService, 
                                 StatsDetailMapper statsDetailMapper,
                                 PokemonUsageStatsMapper pokemonUsageStatsMapper) {
        this.statsSnapshotService = statsSnapshotService;
        this.pokemonInfoService = pokemonInfoService;
        this.pokemonTierService = pokemonTierService;
        this.statsDetailMapper = statsDetailMapper;
        this.pokemonUsageStatsMapper = pokemonUsageStatsMapper;
    }

    /**
     * 太晶属性英文名到中文名的映射表
     * 用于翻译太晶属性名称
     */
    private static final Map<String, String> TERA_TYPE_MAP = Map.ofEntries(
            Map.entry("normal", "一般"), Map.entry("fighting", "格斗"), Map.entry("flying", "飞行"),
            Map.entry("poison", "毒"), Map.entry("ground", "地面"), Map.entry("rock", "岩石"),
            Map.entry("bug", "虫"), Map.entry("ghost", "幽灵"), Map.entry("steel", "钢"),
            Map.entry("fire", "火"), Map.entry("water", "水"), Map.entry("grass", "草"),
            Map.entry("electric", "电"), Map.entry("psychic", "超能力"), Map.entry("ice", "冰"),
            Map.entry("dragon", "龙"), Map.entry("dark", "恶"), Map.entry("fairy", "妖精")
    );

    /**
     * 获取宝可梦在特定对战格式和天梯分数线下的详细数据
     * 包括使用率、常见特性、常见道具、常见技能、常见队友、常见太晶属性等
     *
     * @param format 对战格式，如"gen9ou"（第9代常规单打）
     * @param cutoff 天梯分数线，如1500、1630、1760等
     * @param pokemonNameEn 宝可梦英文名，如"great-tusk"
     * @return 宝可梦的详细对战数据，如果未找到则返回null
     */
    @Override
    @Cacheable(value = "statsDetails", key = "#format + '::' + #cutoff + '::' + #pokemonNameEn")
    public PokemonStatsDetailDto getStatsDetail(String format, int cutoff, String pokemonNameEn) {
        // 1. 查询指定对战格式的最新统计月份
        QueryWrapper<StatsSnapshot> monthQuery = new QueryWrapper<StatsSnapshot>()
                .eq("format", format)
                .select("MAX(stats_month) as stats_month");
        StatsSnapshot latestMonthSnapshot = statsSnapshotService.getOne(monthQuery);

        // 如果没有找到相关快照，返回null
        if (latestMonthSnapshot == null || latestMonthSnapshot.getStatsMonth() == null) {
            return null;
        }
        String latestMonth = latestMonthSnapshot.getStatsMonth();

        // 2. 基于最新月份和指定分数线查询统计快照
        StatsSnapshot snapshot = statsSnapshotService.getOne(new QueryWrapper<StatsSnapshot>()
                .eq("format", format)
                .eq("rating_cutoff", cutoff)
                .eq("stats_month", latestMonth));

        // 如果没有找到符合条件的快照，返回null
        if (snapshot == null) return null;

        Integer snapshotId = snapshot.getId();

        // 3. 查找宝可梦使用率数据
        // 优先使用精确的形态名进行查找（支持不同形态宝可梦）
        PokemonUsageStats usageStats = pokemonUsageStatsMapper.findBestMatch(snapshotId, pokemonNameEn);

        // 如果没有找到使用率数据，记录日志并返回null
        if (usageStats == null) {
            log.warn("智能匹配未能为 [{} - {}] 找到任何使用率数据", format, pokemonNameEn);
            return null;
        }
        Integer statsId = usageStats.getId();

        // 4. 查找宝可梦基础信息
        PokemonInfo pokemonInfo = pokemonInfoService.getOne(new QueryWrapper<PokemonInfo>()
                .eq("name_en", pokemonNameEn));
        if (pokemonInfo == null) return null;

        // 5. 查找宝可梦竞技分级信息
        PokemonTier pokemonTier = pokemonTierService.getOne(new QueryWrapper<PokemonTier>()
                .eq("format", format).eq("pokemon_name_en", pokemonNameEn));

        // 6. 组装基础DTO信息
        PokemonStatsDetailDto dto = createBasicDto(pokemonInfo, pokemonTier, usageStats);

        // 7. 查询各类使用详情数据
        List<StatDetailItemDto> rawAbilities = statsDetailMapper.findUsageDetailsWithInfo(statsId, "ability");
        List<StatDetailItemDto> rawItems = statsDetailMapper.findUsageDetailsWithInfo(statsId, "item");
        List<StatDetailItemDto> rawMoves = statsDetailMapper.findUsageDetailsWithInfo(statsId, "move");
        List<StatDetailItemDto> rawTeammates = statsDetailMapper.findUsageDetailsWithInfo(statsId, "teammate");
        List<StatDetailItemDto> rawTeras = statsDetailMapper.findUsageDetailsWithInfo(statsId, "tera");

        // 8. 计算宝可梦总出场加权次数（以道具总和为基准）
        BigDecimal totalPokemonWeight = calculateTotalWeight(rawItems);

        // 9. 根据不同类别，处理并设置各类使用详情
        // 单选类别（特性、道具、太晶属性）归一化为100%
        dto.setAbilities(normalizeTo100(rawAbilities));
        dto.setItems(normalizeTo100(rawItems));

        // 对太晶属性进行中文名转换后再计算
        translateTeraTypes(rawTeras);
        dto.setTeras(normalizeTo100(rawTeras));

        // 多选类别（技能、队友）计算相对于总出场次数的使用率
        dto.setMoves(calculateUsageRate(rawMoves, totalPokemonWeight));
        dto.setTeammates(calculateUsageRate(rawTeammates, totalPokemonWeight));

        return dto;
    }
    
    /**
     * 创建基础DTO对象
     *
     * @param pokemonInfo 宝可梦基础信息
     * @param pokemonTier 宝可梦竞技分级信息
     * @param usageStats 宝可梦使用率统计
     * @return 填充了基础信息的DTO对象
     */
    private PokemonStatsDetailDto createBasicDto(PokemonInfo pokemonInfo, PokemonTier pokemonTier, PokemonUsageStats usageStats) {
        PokemonStatsDetailDto dto = new PokemonStatsDetailDto();
        dto.setPokemonId(pokemonInfo.getId());
        dto.setNameCn(pokemonInfo.getNameCn());
        dto.setSpritePixel(pokemonInfo.getSpritePixel());
        dto.setTier(pokemonTier != null ? pokemonTier.getTier() : "N/A");
        dto.setRank(usageStats.getRank());
        dto.setUsageRate(usageStats.getUsageRate());
        
        // 处理属性列表，将逗号分隔的字符串转换为列表
        if (StringUtils.hasText(pokemonInfo.getTypesCn())) {
            dto.setTypesCn(Arrays.asList(pokemonInfo.getTypesCn().split(",")));
        } else {
            dto.setTypesCn(Collections.emptyList());
        }
        
        return dto;
    }
    
    /**
     * 计算道具使用总权重，作为宝可梦总出场次数的基准
     *
     * @param items 道具使用详情列表
     * @return 总权重
     */
    private BigDecimal calculateTotalWeight(List<StatDetailItemDto> items) {
        return items.stream()
                .map(StatDetailItemDto::getPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * 将太晶属性英文名翻译为中文名
     *
     * @param teras 太晶属性使用详情列表
     */
    private void translateTeraTypes(List<StatDetailItemDto> teras) {
        teras.forEach(tera -> 
            tera.setName(TERA_TYPE_MAP.getOrDefault(tera.getName().toLowerCase(), tera.getName()))
        );
    }

    /**
     * 处理单选类别（如道具、特性），使其总和为100%
     * 归一化统计数据，使总和为100%
     *
     * @param details 使用详情列表
     * @return 归一化后的使用详情列表
     */
    private List<StatDetailItemDto> normalizeTo100(List<StatDetailItemDto> details) {
        if (details == null || details.isEmpty()) return new ArrayList<>();

        // 计算总权重
        BigDecimal totalWeight = details.stream()
                .map(StatDetailItemDto::getPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 防止除以零
        if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
            details.forEach(item -> item.setPercentage(BigDecimal.ZERO));
            return details;
        }

        // 计算每项所占百分比
        BigDecimal hundred = new BigDecimal("100");
        details.forEach(item -> {
            BigDecimal realPercentage = item.getPercentage()
                    .divide(totalWeight, 6, java.math.RoundingMode.HALF_UP)
                    .multiply(hundred);
            item.setPercentage(realPercentage);
        });
        return details;
    }

    /**
     * 处理多选类别（如技能、队友），计算它们相对于宝可梦总出场次数的使用率
     * 此方法考虑了一个宝可梦可以同时携带多个技能或与多个队友搭配的情况
     *
     * @param details 使用详情列表
     * @param totalPokemonWeight 宝可梦总出场权重
     * @return 计算了使用率的使用详情列表
     */
    private List<StatDetailItemDto> calculateUsageRate(List<StatDetailItemDto> details, BigDecimal totalPokemonWeight) {
        if (details == null || details.isEmpty() || totalPokemonWeight.compareTo(BigDecimal.ZERO) == 0) {
            return new ArrayList<>();
        }

        // 计算每项相对于总出场次数的使用率
        BigDecimal hundred = new BigDecimal("100");
        details.forEach(item -> {
            BigDecimal usageRate = item.getPercentage()
                    .divide(totalPokemonWeight, 6, java.math.RoundingMode.HALF_UP)
                    .multiply(hundred);
            item.setPercentage(usageRate);
        });
        return details;
    }
}