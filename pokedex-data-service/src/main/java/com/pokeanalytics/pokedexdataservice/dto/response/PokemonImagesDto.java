package com.pokeanalytics.pokedexdataservice.dto.response;

import lombok.Data;

/**
 * 宝可梦图片数据传输对象
 * 包含不同类型的宝可梦图片URL
 */
@Data
public class PokemonImagesDto {
    /**
     * 高清图片URL
     */
    private String hd;
    
    /**
     * 高清闪光图片URL
     */
    private String hdShiny;
    
    /**
     * 像素图片URL
     */
    private String pixel;
    
    /**
     * 像素闪光图片URL
     */
    private String pixelShiny;
}