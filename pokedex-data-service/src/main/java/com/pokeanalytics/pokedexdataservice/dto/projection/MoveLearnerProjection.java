package com.pokeanalytics.pokedexdataservice.dto.projection;

import lombok.Data;

/**
 * 技能学习者投影类
 * 用于接收SQL查询结果，表示学习特定技能的宝可梦及学习方式
 */
@Data
public class MoveLearnerProjection {
    /**
     * 宝可梦ID
     */
    private Integer pokemonId;
    
    /**
     * 宝可梦中文名称
     */
    private String pokemonNameCn;
    
    /**
     * 宝可梦像素图片URL
     */
    private String spritePixel;
    
    /**
     * 学习方法
     */
    private String learnMethod;
    
    /**
     * 学习所需等级
     */
    private Integer levelLearnedAt;
    
    /**
     * 最后可学习的游戏版本
     */
    private String lastLearnGeneration;
}