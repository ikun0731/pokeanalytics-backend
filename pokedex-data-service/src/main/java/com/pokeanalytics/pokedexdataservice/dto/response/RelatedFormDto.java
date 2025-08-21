package com.pokeanalytics.pokedexdataservice.dto.response;

import lombok.Data;

/**
 * 宝可梦相关形态DTO
 * 用于表示宝可梦的不同形态信息
 */
@Data
public class RelatedFormDto {
    /**
     * 形态ID
     */
    private Integer id;
    
    /**
     * 形态中文名称
     */
    private String nameCn;
    
    /**
     * 形态名称
     * 如：alola(阿罗拉)、galar(伽勒尔)、gmax(极巨化)等
     */
    private String formName;
    
    /**
     * 高清图片URL
     */
    private String spriteHd;
    
    /**
     * 像素图片URL
     */
    private String spritePixel;
}