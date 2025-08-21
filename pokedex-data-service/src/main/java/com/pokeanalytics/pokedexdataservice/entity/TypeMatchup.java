package com.pokeanalytics.pokedexdataservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 宝可梦属性克制关系实体类
 * 对应数据库中的type_matchup表
 */
@Data
@TableName("type_matchup")
public class TypeMatchup {
    /**
     * 克制关系ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 攻击方属性
     */
    private String attackingType;
    
    /**
     * 防御方属性
     */
    private String defendingType;
    
    /**
     * 伤害倍率
     */
    private BigDecimal multiplier;
}