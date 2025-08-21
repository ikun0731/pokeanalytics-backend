package com.pokeanalytics.pokedexdataservice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 宝可梦实体类
 * 对应数据库中的pokemon表
 */
@Data
@TableName("pokemon")
public class Pokemon {
    /**
     * 宝可梦ID
     */
    @TableId
    private Integer id;
    
    /**
     * 宝可梦英文名称
     */
    @TableField("name_en")
    private String nameEn;
    
    /**
     * 宝可梦中文名称
     */
    @TableField("name_cn")
    private String nameCn;
    
    /**
     * 主属性（英文）
     */
    @TableField("type_1")
    private String type1;
    
    /**
     * 副属性（英文）
     */
    @TableField("type_2")
    private String type2;
    
    /**
     * 生命值
     */
    @TableField("hp")
    private Integer hp;
    
    /**
     * 攻击力
     */
    @TableField("attack")
    private Integer attack;
    
    /**
     * 防御力
     */
    @TableField("defense")
    private Integer defense;
    
    /**
     * 特殊攻击力
     */
    @TableField("special_attack")
    private Integer specialAttack;
    
    /**
     * 特殊防御力
     */
    @TableField("special_defense")
    private Integer specialDefense;
    
    /**
     * 速度
     */
    @TableField("speed")
    private Integer speed;
    
    /**
     * 像素图片URL
     */
    @TableField("sprite_pixel")
    private String spritePixel;
    
    /**
     * 闪光像素图片URL
     */
    @TableField("sprite_pixel_shiny")
    private String spritePixelShiny;
    
    /**
     * 高清图片URL
     */
    @TableField("sprite_hd")
    private String spriteHd;
    
    /**
     * 闪光高清图片URL
     */
    @TableField("sprite_hd_shiny")
    private String spriteHdShiny;
    
    /**
     * 宝可梦描述文本
     */
    @TableField("flavor_text")
    private String flavorText;
    
    /**
     * 是否为默认形态
     */
    @TableField("is_default")
    private Boolean isDefault;
    
    /**
     * 形态名称
     */
    @TableField("form_name")
    private String formName;
    
    /**
     * 主属性（中文）
     */
    @TableField("type_1_cn")
    private String type1Cn;
    
    /**
     * 副属性（中文）
     */
    @TableField("type_2_cn")
    private String type2Cn;
    
    /**
     * 身高（米）
     */
    private Double height;
    
    /**
     * 体重（千克）
     */
    private Double weight;
    
    /**
     * 基础经验值
     */
    @TableField("base_experience")
    private Integer baseExperience;
    
    /**
     * 努力值收益
     */
    @TableField("ev_yield")
    private String evYield;
    
    /**
     * 性别比例
     * -1表示无性别，0表示全部雄性，8表示全部雌性
     * 1-7表示雌性概率为(value/8)*100%
     */
    @TableField("gender_rate")
    private Integer genderRate;
    
    /**
     * 孵化步数
     */
    @TableField("hatch_steps")
    private Integer hatchSteps;
    
    /**
     * 捕获率
     */
    @TableField("capture_rate")
    private Integer captureRate;
    
    /**
     * 第一蛋组（英文）
     */
    @TableField("egg_group_1")
    private String eggGroup1;
    
    /**
     * 第二蛋组（英文）
     */
    @TableField("egg_group_2")
    private String eggGroup2;
}