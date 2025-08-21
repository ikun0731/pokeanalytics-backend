package com.pokeanalytics.pokedexdataservice.dto.response;

import lombok.Data;
import java.util.List;

/**
 * 宝可梦列表项DTO
 * 用于宝可梦列表展示的精简信息
 */
@Data
public class PokemonListItemDto {
    /**
     * 宝可梦ID
     */
    private Integer id;
    
    /**
     * 中文名称
     */
    private String nameCn;
    
    /**
     * 英文名称
     */
    private String nameEn;
    
    /**
     * 中文属性列表
     */
    private List<String> types;
    
    /**
     * 英文属性列表
     */
    private List<String> typesEn;
    
    /**
     * 像素风格图片URL
     * 列表页使用像素图，相比高清图更节省流量
     */
    private String spritePixel;
}