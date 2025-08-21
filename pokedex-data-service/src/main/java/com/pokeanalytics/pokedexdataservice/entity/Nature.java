package com.pokeanalytics.pokedexdataservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 宝可梦性格实体类
 * 对应数据库中的nature表
 */
@Data
@TableName("nature")
public class Nature {
    /**
     * 性格ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 性格中文名称
     */
    private String nameCn;
    
    /**
     * 性格英文名称
     */
    private String nameEn;
    
    /**
     * 增强的能力值
     */
    private String increasedStat;
    
    /**
     * 减弱的能力值
     */
    private String decreasedStat;
}