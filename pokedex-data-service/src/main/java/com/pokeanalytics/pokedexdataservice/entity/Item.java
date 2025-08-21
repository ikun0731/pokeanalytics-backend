package com.pokeanalytics.pokedexdataservice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 宝可梦道具实体类
 * 对应数据库中的item表
 */
@Data
@TableName("item")
public class Item {
    /**
     * 道具ID
     */
    @TableId
    private Integer id;
    
    /**
     * 道具英文名称
     */
    @TableField("name_en")
    private String nameEn;
    
    /**
     * 道具中文名称
     */
    @TableField("name_cn")
    private String nameCn;
    
    /**
     * 道具分类英文
     */
    private String category;
    
    /**
     * 道具分类中文
     */
    @TableField("category_cn")
    private String categoryCn;
    
    /**
     * 道具效果描述
     */
    private String effect;
    
    /**
     * 道具风味文本描述
     */
    @TableField("flavor_text")
    private String flavorText;
    
    /**
     * 道具图片URL
     */
    @TableField("sprite_url")
    private String spriteUrl;
}