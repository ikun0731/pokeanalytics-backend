package com.pokeanalytics.pokestatsservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 道具基础信息实体类
 * 对应数据库表item_info，存储宝可梦道具的基本信息
 */
@Data
@TableName("item_info")
public class ItemInfo {
    /**
     * 道具ID，主键
     * 主键值来自于pokedex-data-service服务，非自增
     */
    @TableId(type = IdType.INPUT)
    private Integer id;
    
    /**
     * 道具英文名
     */
    private String nameEn;
    
    /**
     * 道具中文名
     */
    private String nameCn;
    
    /**
     * 道具类别中文名
     * 如：持有物、树果、训练器等
     */
    private String categoryCn;
    
    /**
     * 道具图片URL
     */
    private String spriteUrl;
}