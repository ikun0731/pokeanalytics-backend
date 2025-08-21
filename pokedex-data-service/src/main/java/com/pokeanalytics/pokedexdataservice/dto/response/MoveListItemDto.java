package com.pokeanalytics.pokedexdataservice.dto.response;

import lombok.Data;

/**
 * 技能列表项数据传输对象
 * 用于在列表页面展示技能基本信息
 */
@Data
public class MoveListItemDto {
    /**
     * 技能ID
     */
    private Integer id;
    
    /**
     * 技能中文名称
     */
    private String nameCn;
    
    /**
     * 技能英文名称
     */
    private String nameEn;
    
    /**
     * 技能属性中文
     */
    private String typeCn;
    
    /**
     * 技能伤害类别中文（物攻/特攻/变化）
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