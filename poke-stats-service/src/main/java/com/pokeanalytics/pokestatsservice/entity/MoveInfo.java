package com.pokeanalytics.pokestatsservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 技能基础信息实体类
 * 对应数据库表move_info，存储宝可梦技能的基本信息
 */
@Data
@TableName("move_info")
public class MoveInfo {
    /**
     * 技能ID，主键
     * 主键值来自于pokedex-data-service服务，非自增
     */
    @TableId(type = IdType.INPUT)
    private Integer id;
    
    /**
     * 技能英文名
     */
    private String nameEn;
    
    /**
     * 技能中文名
     */
    private String nameCn;
    
    /**
     * 技能属性中文名
     * 如：一般、火、水、电等
     */
    private String typeCn;
    
    /**
     * 技能伤害类别中文名
     * 如：物理、特殊、变化
     */
    private String damageClassCn;
}