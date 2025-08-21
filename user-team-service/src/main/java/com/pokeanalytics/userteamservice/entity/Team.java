package com.pokeanalytics.userteamservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 队伍实体类
 * 对应数据库中的teams表，存储宝可梦队伍的基本信息
 */
@Data
@TableName("teams")
public class Team {
    /** 队伍ID，主键，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /** 所属用户ID，外键关联到用户表 */
    private Long userId;
    
    /** 队伍名称 */
    private String teamName;
    
    /** 队伍描述 */
    private String description;
    
    /** 队伍格式（如OU, UU等） */
    private String format;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 最后更新时间 */
    private LocalDateTime updatedAt;
}