package com.pokeanalytics.pokestatsservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pokeanalytics.pokestatsservice.dto.projection.LeaderboardProjection;
import org.apache.ibatis.annotations.Param;

/**
 * 排行榜数据库操作接口
 * 提供排行榜数据查询方法
 * 由于是复杂多表查询，不对应单一实体，所以BaseMapper类型参数为Object
 */
public interface LeaderboardMapper extends BaseMapper<Object> {
    
    /**
     * 获取宝可梦使用率排行榜
     * 关联查询宝可梦使用率、基础信息和竞技分级数据，并计算常用道具
     *
     * @param page 分页对象
     * @param snapshotId 统计快照ID
     * @param format 对战格式
     * @return 分页的排行榜数据
     */
    IPage<LeaderboardProjection> getLeaderboard(
            IPage<LeaderboardProjection> page, 
            @Param("snapshotId") Integer snapshotId, 
            @Param("format") String format
    );
}