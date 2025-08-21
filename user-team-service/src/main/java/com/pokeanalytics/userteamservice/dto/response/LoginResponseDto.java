package com.pokeanalytics.userteamservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应数据传输对象
 * 包含用户登录成功后返回的JWT令牌
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
}