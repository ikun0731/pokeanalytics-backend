package com.pokeanalytics.pokedexdataservice.dto.response;

import lombok.Data;

/**
 * 宝可梦学习者数据传输对象
 * 用于表示拥有特定特性或招式的宝可梦信息
 */
@Data
public class PokemonLearnerDto {
    /**
     * 宝可梦ID
     */
    private Integer pokemonId;
    
    /**
     * 宝可梦中文名称
     */
    private String pokemonNameCn;
    
    /**
     * 宝可梦像素图标路径
     */
    private String spritePixel;
    
    /**
     * 学习该特性或招式的等级
     */
    private Integer levelLearnedAt;
    
    /**
     * 最后一次学习的游戏版本/世代
     */
    private String lastLearnGeneration;
}