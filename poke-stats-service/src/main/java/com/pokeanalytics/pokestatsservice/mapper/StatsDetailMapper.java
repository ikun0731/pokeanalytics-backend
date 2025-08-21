package com.pokeanalytics.pokestatsservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pokeanalytics.pokestatsservice.dto.StatDetailItemDto;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 对战数据详情数据库操作接口
 * 提供宝可梦使用详情数据查询方法
 */
public interface StatsDetailMapper extends BaseMapper<Object> {
    
    /**
     * 查询宝可梦的使用详情数据，并关联基础信息
     * 根据不同的详情类型，关联不同的基础信息表（特性、道具、技能等）
     *
     * @param statsId 宝可梦使用率统计ID
     * @param detailType 详情类型（ability、item、move、teammate、tera）
     * @return 使用详情列表，包含基础信息
     */
    List<StatDetailItemDto> findUsageDetailsWithInfo(
            @Param("statsId") Integer statsId,
            @Param("detailType") String detailType
    );
}