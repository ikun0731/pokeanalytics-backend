package com.pokeanalytics.userteamservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库中的user表，存储用户的基本信息
 */
@Data
@TableName("user")
public class User {
    /** 用户ID，主键，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /** 用户名，唯一标识 */
    private String username;
    
    /** 用户密码，存储加密后的哈希值 */
    private String password;
    
    /** 用户邮箱 */
    private String email;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 最后更新时间 */
    private LocalDateTime updatedAt;
}