package com.pokeanalytics.userteamservice.dto.response;

import lombok.Data;

/**
 * 用户响应数据传输对象
 * 包含返回给前端的用户基本信息，不包含敏感数据如密码
 */
@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
}