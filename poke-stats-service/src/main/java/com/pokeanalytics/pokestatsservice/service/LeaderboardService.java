package com.pokeanalytics.pokestatsservice.service;

import com.pokeanalytics.pokestatsservice.dto.LeaderboardItemDto;
import com.pokeanalytics.pokestatsservice.dto.PageResultDto;

/**
 * 宝可梦排行榜服务接口
 * 提供获取宝可梦使用率排行榜数据的相关功能
 */
public interface LeaderboardService {
    /**
     * 获取宝可梦使用率排行榜
     *
     * @param format 对战格式，如"gen9ou"（第9代常规单打）、"gen9ubers"（第9代无限制单打）等
     * @param cutoff 天梯分数线，如1500、1630、1760等，表示统计该分数以上的对战数据
     * @param pageNum 页码，从1开始
     * @param pageSize 每页数量
     * @return 封装好的排行榜分页结果，包含排名、宝可梦基本信息和使用率
     */
    PageResultDto<LeaderboardItemDto> getLeaderboard(String format, int cutoff, long pageNum, long pageSize);
}
