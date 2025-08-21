package com.pokeanalytics.pokedexdataservice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 宝可梦技能实体类
 * 对应数据库中的move表
 */
@Data
@TableName("move")
public class Move {
    /**
     * 技能ID
     */
    @TableId
    private Integer id;
    
    /**
     * 技能英文名称
     */
    @TableField("name_en")
    private String nameEn;
    
    /**
     * 技能中文名称
     */
    @TableField("name_cn")
    private String nameCn;
    
    /**
     * 技能属性英文
     */
    private String type;
    
    /**
     * 技能属性中文
     */
    @TableField("type_cn")
    private String typeCn;
    
    /**
     * 技能威力
     */
    private Integer power;
    
    /**
     * 技能PP值
     */
    private Integer pp;
    
    /**
     * 技能命中率
     */
    private Integer accuracy;
    
    /**
     * 技能风味文本描述
     */
    @TableField("flavor_text")
    private String flavorText;
    
    /**
     * 技能伤害类别英文
     */
    @TableField("damage_class")
    private String damageClass;
    
    /**
     * 技能伤害类别中文
     */
    @TableField("damage_class_cn")
    private String damageClassCn;
}