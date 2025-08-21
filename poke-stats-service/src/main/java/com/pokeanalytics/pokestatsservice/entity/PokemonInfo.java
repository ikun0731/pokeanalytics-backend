package com.pokeanalytics.pokestatsservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 宝可梦基础信息实体类
 * 对应数据库表pokemon_info，存储宝可梦的基本信息
 */
@Data
@TableName("pokemon_info")
public class PokemonInfo {
    /**
     * 宝可梦ID，主键
     * 主键值来自于pokedex-data-service服务，非自增
     */
    @TableId(type = IdType.INPUT)
    private Integer id;
    
    /**
     * 宝可梦英文名
     */
    private String nameEn;
    
    /**
     * 宝可梦中文名
     */
    private String nameCn;
    
    /**
     * 宝可梦像素图URL
     */
    private String spritePixel;
    
    /**
     * 宝可梦中文属性
     * 格式：多个属性用逗号分隔，如"一般,飞行"
     */
    private String typesCn;
}