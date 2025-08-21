package com.pokeanalytics.pokestatsservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 统计详情项DTO
 * 用于表示各种统计数据项（特性、道具、技能、队友、太晶属性等）的使用情况
 */
@Data
public class StatDetailItemDto implements Serializable {
    /**
     * 名称
     * 通用名称字段，可以是特性名、道具名、技能名、宝可梦名或太晶属性名
     */
    private String name;
    
    /**
     * 使用率
     * 百分比形式，如0.1234表示12.34%
     */
    private BigDecimal percentage;
    
    /**
     * 中文属性
     * 主要用于技能和太晶属性的类型
     */
    private String typeCn;
    
    /**
     * 宝可梦ID
     * 主要用于队友数据，表示队友宝可梦的ID
     */
    private Integer pokemonId;
}