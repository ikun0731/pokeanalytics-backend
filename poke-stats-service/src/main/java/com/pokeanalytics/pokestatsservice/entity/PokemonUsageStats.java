package com.pokeanalytics.pokestatsservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 宝可梦使用率统计实体类
 * 对应数据库表pokemon_usage_stats，存储宝可梦在特定统计快照中的使用率数据
 */
@Data
@TableName("pokemon_usage_stats")
public class PokemonUsageStats {
    /**
     * 记录ID，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 统计快照ID
     * 关联stats_snapshot表的id字段
     */
    private Integer snapshotId;
    
    /**
     * 宝可梦英文名
     */
    private String pokemonNameEn;
    
    /**
     * 使用率
     * 百分比形式，如0.1234表示12.34%
     */
    private BigDecimal usageRate;
    
    /**
     * 排名
     * 使用了反引号，因为rank是MySQL关键字
     */
    @TableField("`rank`")
    private Integer rank;
}