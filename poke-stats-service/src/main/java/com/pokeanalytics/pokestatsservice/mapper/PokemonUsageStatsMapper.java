package com.pokeanalytics.pokestatsservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pokeanalytics.pokestatsservice.entity.PokemonUsageStats;
import org.apache.ibatis.annotations.Param;

/**
 * 宝可梦使用率统计数据库操作接口
 * 提供对pokemon_usage_stats表的基础操作和自定义查询
 */
public interface PokemonUsageStatsMapper extends BaseMapper<PokemonUsageStats> {
    /**
     * 根据完整的形态名，智能查找最匹配的使用率统计记录
     * 支持精确匹配和模糊匹配，优先返回精确匹配结果
     * 用于处理宝可梦有多种形态且数据源可能使用不同命名格式的情况
     *
     * @param snapshotId 统计快照ID
     * @param fullFormName 宝可梦完整英文名，来自pokemon_info表
     * @return 最匹配的使用率统计记录，如果未找到则返回null
     */
    PokemonUsageStats findBestMatch(@Param("snapshotId") Integer snapshotId, @Param("fullFormName") String fullFormName);
}
