package com.pokeanalytics.pokestatsservice.dto;

import lombok.Data;

/**
 * 技能列表项DTO
 * 用于接收Feign客户端从pokedex-data-service获取的技能列表数据
 */
@Data
public class MoveListItemDto {
    /**
     * 技能ID
     */
    private Integer id;
    
    /**
     * 技能中文名
     */
    private String nameCn;
    
    /**
     * 技能英文名
     */
    private String nameEn;
    
    /**
     * 技能属性中文名
     */
    private String typeCn;
    
    /**
     * 伤害类别中文名
     * 如：物理、特殊、变化
     */
    private String damageClassCn;
    
    /**
     * 技能威力
     */
    private Integer power;
    
    /**
     * 技能命中率
     */
    private Integer accuracy;
    
    /**
     * 技能PP值
     */
    private Integer pp;
}