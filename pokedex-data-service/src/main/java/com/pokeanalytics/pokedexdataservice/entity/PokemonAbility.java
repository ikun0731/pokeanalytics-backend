package com.pokeanalytics.pokedexdataservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 宝可梦特性关联实体类
 * 对应数据库中的pokemon_ability表
 */
@Data
@TableName("pokemon_ability")
public class PokemonAbility {
    /**
     * 关联ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 宝可梦ID
     */
    @TableField("pokemon_id")
    private Integer pokemonId;
    
    /**
     * 特性ID
     */
    @TableField("ability_id")
    private Integer abilityId;
    
    /**
     * 是否为隐藏特性
     */
    @TableField("is_hidden")
    private Boolean isHidden;
    
    /**
     * 最后出现的游戏版本
     */
    @TableField("last_generation")
    private String lastGeneration;
}