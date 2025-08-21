package com.pokeanalytics.pokestatsservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pokeanalytics.pokestatsservice.dto.projection.LeaderboardProjection;
import com.pokeanalytics.pokestatsservice.dto.LeaderboardItemDto;
import com.pokeanalytics.pokestatsservice.dto.PageResultDto;
import com.pokeanalytics.pokestatsservice.entity.StatsSnapshot;
import com.pokeanalytics.pokestatsservice.mapper.LeaderboardMapper;
import com.pokeanalytics.pokestatsservice.service.LeaderboardService;
import com.pokeanalytics.pokestatsservice.service.StatsSnapshotService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 宝可梦排行榜服务实现类
 * 提供获取宝可梦使用率排行榜数据的具体实现
 */
@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    /**
     * 排行榜数据库操作接口
     */
    private final LeaderboardMapper leaderboardMapper;
    
    /**
     * 统计快照服务
     */
    private final StatsSnapshotService statsSnapshotService;

    /**
     * 构造函数，通过依赖注入初始化所需服务
     *
     * @param leaderboardMapper 排行榜数据库操作接口
     * @param statsSnapshotService 统计快照服务
     */
    public LeaderboardServiceImpl(LeaderboardMapper leaderboardMapper, StatsSnapshotService statsSnapshotService) {
        this.leaderboardMapper = leaderboardMapper;
        this.statsSnapshotService = statsSnapshotService;
    }

    /**
     * 获取宝可梦使用率排行榜
     * 先查询最新的统计快照，然后基于该快照查询排行榜数据
     *
     * @param format 对战格式，如"gen9ou"（第9代常规单打）
     * @param cutoff 天梯分数线，如1500、1630、1760等
     * @param pageNum 页码，从1开始
     * @param pageSize 每页数量
     * @return 封装好的排行榜分页结果
     */
    @Override
    @Cacheable(value = "leaderboards", key = "#format + '::' + #cutoff + '::' + #pageNum + '::' + #pageSize")
    public PageResultDto<LeaderboardItemDto> getLeaderboard(String format, int cutoff, long pageNum, long pageSize) {
        // 1. 查询指定对战格式的最新统计月份
        QueryWrapper<StatsSnapshot> monthQuery = new QueryWrapper<StatsSnapshot>()
                .eq("format", format)
                .select("MAX(stats_month) as stats_month");
        StatsSnapshot latestMonthSnapshot = statsSnapshotService.getOne(monthQuery);

        // 如果没有找到相关快照，返回空结果
        if (latestMonthSnapshot == null || latestMonthSnapshot.getStatsMonth() == null) {
            return createEmptyResult();
        }
        String latestMonth = latestMonthSnapshot.getStatsMonth();

        // 2. 基于最新月份和指定分数线查询统计快照
        StatsSnapshot snapshot = statsSnapshotService.getOne(new QueryWrapper<StatsSnapshot>()
                .eq("format", format)
                .eq("rating_cutoff", cutoff)
                .eq("stats_month", latestMonth));

        // 如果没有找到符合条件的快照，返回空结果
        if (snapshot == null) {
            return createEmptyResult();
        }
        Integer snapshotId = snapshot.getId();

        // 3. 使用MyBatis-Plus进行数据库分页查询
        IPage<LeaderboardProjection> pageRequest = new Page<>(pageNum, pageSize);
        IPage<LeaderboardProjection> resultPage = leaderboardMapper.getLeaderboard(pageRequest, snapshotId, format);

        // 4. 将查询结果转换为DTO对象
        List<LeaderboardItemDto> currentPageItems = resultPage.getRecords().stream().map(this::convertToDto).collect(Collectors.toList());

        // 5. 封装分页结果
        PageResultDto<LeaderboardItemDto> pageResult = new PageResultDto<>();
        pageResult.setTotal(resultPage.getTotal());
        pageResult.setTotalPages(resultPage.getPages());
        pageResult.setItems(currentPageItems);

        return pageResult;
    }
    
    /**
     * 创建空的分页结果
     *
     * @return 空的分页结果对象
     */
    private PageResultDto<LeaderboardItemDto> createEmptyResult() {
        PageResultDto<LeaderboardItemDto> emptyResult = new PageResultDto<>();
        emptyResult.setTotal(0);
        emptyResult.setTotalPages(0);
        emptyResult.setItems(Collections.emptyList());
        return emptyResult;
    }
    
    /**
     * 将数据库查询结果转换为DTO对象
     *
     * @param proj 数据库查询投影结果
     * @return 转换后的DTO对象
     */
    private LeaderboardItemDto convertToDto(LeaderboardProjection proj) {
        LeaderboardItemDto dto = new LeaderboardItemDto();
        dto.setRank(proj.getRank());
        dto.setPokemonId(proj.getPokemonId());
        dto.setNameCn(proj.getNameCn() != null ? proj.getNameCn() : proj.getNameEn());
        dto.setNameEn(proj.getNameEn());
        dto.setSpritePixel(proj.getSpritePixel());
        dto.setTier(proj.getTier() != null ? proj.getTier() : "N/A");
        dto.setUsageRate(proj.getUsageRate());
        dto.setTopItem(proj.getTopItem());

        // 处理属性列表，将逗号分隔的字符串转换为列表
        if (StringUtils.hasText(proj.getTypesCn())) {
            dto.setTypesCn(Arrays.asList(proj.getTypesCn().split(",")));
        } else {
            dto.setTypesCn(Collections.emptyList());
        }
        return dto;
    }
}