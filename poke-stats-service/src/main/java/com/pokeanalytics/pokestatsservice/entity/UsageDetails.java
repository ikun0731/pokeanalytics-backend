package com.pokeanalytics.pokestatsservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 使用详情实体类
 * 对应数据库表usage_details，存储宝可梦的特性、道具、技能、队友、太晶属性等使用详情
 */
@Data
@TableName("usage_details")
public class UsageDetails {
    /**
     * 记录ID，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 对战统计ID
     * 关联pokemon_usage_stats表的id字段
     */
    private Integer statsId;
    
    /**
     * 详情类型
     * 可能的值：ability(特性)、item(道具)、move(技能)、teammate(队友)、tera(太晶属性)
     */
    private String detailType;
    
    /**
     * 详情名称
     * 根据detailType不同，可能是特性名、道具名、技能名、队友名或太晶属性名
     */
    private String detailName;
    
    /**
     * 使用率
     * 百分比形式，如0.1234表示12.34%
     */
    private BigDecimal usagePercentage;
}