package com.pokeanalytics.pokestatsservice.dto;

import lombok.Data;

/**
 * 道具列表项DTO
 * 用于接收Feign客户端从pokedex-data-service获取的道具列表数据
 */
@Data
public class ItemListItemDto {
    /**
     * 道具ID
     */
    private Integer id;
    
    /**
     * 道具中文名
     */
    private String nameCn;
    
    /**
     * 道具英文名
     */
    private String nameEn;
    
    /**
     * 道具分类中文名
     */
    private String categoryCn;
    
    /**
     * 道具图片URL
     */
    private String spriteUrl;
}