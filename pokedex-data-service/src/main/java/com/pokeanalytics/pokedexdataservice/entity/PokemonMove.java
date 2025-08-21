package com.pokeanalytics.pokedexdataservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 宝可梦技能关联实体类
 * 对应数据库中的pokemon_move表
 */
@Data
@TableName("pokemon_move")
public class PokemonMove {
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
     * 技能ID
     */
    @TableField("move_id")
    private Integer moveId;
    
    /**
     * 学习方法
     */
    @TableField("learn_method")
    private String learnMethod;
    
    /**
     * 学习所需等级
     */
    @TableField("level_learned_at")
    private Integer levelLearnedAt;
    
    /**
     * 最后可学习的游戏版本
     */
    @TableField("last_learn_generation")
    private String lastLearnGeneration;
}