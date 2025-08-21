package com.pokeanalytics.pokedexdataservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 宝可梦进化链实体类
 * 对应数据库中的evolution_chain表
 */
@Data
@TableName("evolution_chain")
public class EvolutionChain {
    /**
     * 进化链记录ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 进化链ID
     */
    @TableField("chain_id")
    private Integer chainId;
    
    /**
     * 进化前宝可梦ID
     */
    @TableField("from_pokemon_id")
    private Integer fromPokemonId;
    
    /**
     * 进化后宝可梦ID
     */
    @TableField("to_pokemon_id")
    private Integer toPokemonId;
    
    /**
     * 触发进化的方法
     */
    @TableField("trigger_method")
    private String triggerMethod;
    
    /**
     * 触发进化的道具名称
     */
    @TableField("trigger_item")
    private String triggerItem;
    
    /**
     * 进化所需最低等级
     */
    @TableField("min_level")
    private Integer minLevel;
}