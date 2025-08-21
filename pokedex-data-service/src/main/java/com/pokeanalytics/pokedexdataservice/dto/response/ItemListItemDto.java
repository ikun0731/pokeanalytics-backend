package com.pokeanalytics.pokedexdataservice.dto.response;

import lombok.Data;

/**
 * 道具列表项数据传输对象
 * 用于在列表页面展示道具基本信息
 */
@Data
public class ItemListItemDto {
    /**
     * 道具ID
     */
    private Integer id;
    
    /**
     * 道具中文名称
     */
    private String nameCn;
    
    /**
     * 道具英文名称
     */
    private String nameEn;
    
    /**
     * 道具分类中文名称
     */
    private String categoryCn;
    
    /**
     * 道具图片URL
     */
    private String spriteUrl;
    
    /**
     * 道具风味文本描述
     */
    private String flavorText;
}