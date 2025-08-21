package com.pokeanalytics.pokedexdataservice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 宝可梦特性实体类
 * 对应数据库中的ability表
 */
@Data
@TableName("ability")
public class Ability {
    /**
     * 特性ID
     */
    @TableId
    private Integer id;
    
    /**
     * 特性英文名称
     */
    @TableField("name_en")
    private String nameEn;
    
    /**
     * 特性中文名称
     */
    @TableField("name_cn")
    private String nameCn;
    
    /**
     * 特性效果描述
     */
    private String effect;
}