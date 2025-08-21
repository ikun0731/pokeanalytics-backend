package com.pokeanalytics.pokestatsservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 宝可梦竞技分级实体类
 * 对应数据库表pokemon_tier，存储不同对战格式中宝可梦的官方分级
 */
@Data
@TableName("pokemon_tier")
public class PokemonTier {
    /**
     * 记录ID，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 对战格式
     * 如：gen9ou（第9代常规单打）、gen9ubers（第9代无限制单打）等
     */
    private String format;
    
    /**
     * 宝可梦英文名
     */
    private String pokemonNameEn;
    
    /**
     * 官方竞技分级
     * 如：OU(常规)、UU(次级)、Ubers(无限制)等
     */
    private String tier;
}