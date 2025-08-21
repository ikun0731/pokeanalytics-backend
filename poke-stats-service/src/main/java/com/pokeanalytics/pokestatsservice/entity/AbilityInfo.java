package com.pokeanalytics.pokestatsservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 特性基础信息实体类
 * 对应数据库表ability_info，存储宝可梦特性的基本信息
 */
@Data
@TableName("ability_info")
public class AbilityInfo {
    /**
     * 特性ID，主键
     * 主键值来自于pokedex-data-service服务，非自增
     */
    @TableId(type = IdType.INPUT)
    private Integer id;
    
    /**
     * 特性英文名
     */
    private String nameEn;
    
    /**
     * 特性中文名
     */
    private String nameCn;
}