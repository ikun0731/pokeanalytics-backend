package com.pokeanalytics.userteamservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 队伍成员实体类
 * 对应数据库中的team_members表，存储队伍中各个宝可梦的详细配置信息
 */
@Data
@TableName(value = "team_members", autoResultMap = true) // autoResultMap=true 启用类型处理器
public class TeamMember {
    /** 成员ID，主键，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /** 所属队伍ID，外键关联到teams表 */
    private Long teamId;
    
    /** 在队伍中的位置，从0开始 */
    private Integer position;
    
    /** 宝可梦英文名称 */
    private String pokemonNameEn;
    
    /** 宝可梦昵称 */
    private String nickname;
    
    /** 携带道具英文名称 */
    private String item;
    
    /** 特性英文名称 */
    private String ability;
    
    /** 太晶属性英文名称 */
    private String teraType;
    
    /** 性格英文名称 */
    private String nature;
    
    /** 是否闪光 */
    private Boolean isShiny;

    /** 努力值分配，使用JacksonTypeHandler将JSON字符串转换为Map */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Integer> evs;

    /** 个体值分配，使用JacksonTypeHandler将JSON字符串转换为Map */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Integer> ivs;

    /** 技能列表，使用JacksonTypeHandler将JSON字符串转换为List */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> moves;
}