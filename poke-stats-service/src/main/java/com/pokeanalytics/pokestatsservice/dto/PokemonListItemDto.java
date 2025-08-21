package com.pokeanalytics.pokestatsservice.dto;

import lombok.Data;

import java.util.List;

/**
 * 宝可梦列表项DTO
 * 用于接收Feign客户端从pokedex-data-service获取的宝可梦列表数据
 */
@Data
public class PokemonListItemDto {
    /**
     * 宝可梦ID
     */
    private Integer id;
    
    /**
     * 宝可梦中文名
     */
    private String nameCn;
    
    /**
     * 宝可梦英文名
     */
    private String nameEn;
    
    /**
     * 宝可梦属性列表
     */
    private List<String> types;
    
    /**
     * 宝可梦像素图URL
     * 列表页使用像素图以节省流量
     */
    private String spritePixel;
}