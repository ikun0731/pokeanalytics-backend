package com.pokeanalytics.pokedexdataservice.dto.response;

import lombok.Data;

/**
 * 宝可梦技能DTO
 * 包含技能本身的信息和宝可梦学习该技能的方式
 */
@Data
public class PokemonMoveDto {
    /**
     * 技能ID
     */
    private Integer moveId;
    
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
     * 伤害类型中文名
     * 物理攻击/特殊攻击/变化技能
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
     * 技能PP值（可使用次数）
     */
    private Integer pp;
    
    /**
     * 技能描述文本
     */
    private String flavorText;

    /**
     * 学习方式
     * 例如：level-up(升级)、machine(技能机)、tutor(教授)等
     */
    private String learnMethod;
    
    /**
     * 通过升级学习时的等级
     * 只有learnMethod为level-up时该字段才有值
     */
    private Integer levelLearnedAt;
    
    /**
     * 最后可学习该技能的游戏世代
     */
    private String lastLearnGeneration;
}