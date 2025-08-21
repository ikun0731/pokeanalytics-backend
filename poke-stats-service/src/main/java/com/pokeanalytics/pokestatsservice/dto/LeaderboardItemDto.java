package com.pokeanalytics.pokestatsservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 宝可梦排行榜项DTO
 * 用于表示排行榜中的单个宝可梦数据
 */
@Data
public class LeaderboardItemDto implements Serializable {
    /**
     * 排名
     */
    private Integer rank;
    
    /**
     * 宝可梦ID
     */
    private Integer pokemonId;
    
    /**
     * 宝可梦中文名
     */
    private String nameCn;
    
    /**
     * 宝可梦英文名
     */
    private String nameEn;
    
    /**
     * 宝可梦中文属性列表
     */
    private List<String> typesCn;
    
    /**
     * 宝可梦像素图URL
     */
    private String spritePixel;
    
    /**
     * 官方竞技分级
     * 如：OU(常规)、UU(次级)、Ubers(无限制)等
     */
    private String tier;
    
    /**
     * 使用率
     * 百分比形式，如0.1234表示12.34%
     */
    private BigDecimal usageRate;
    
    /**
     * 最常用道具的中文名
     */
    private String topItem;
}