package com.pokeanalytics.userteamservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求数据传输对象
 * 包含用户登录所需的用户名和密码信息
 */
@Data
public class LoginRequestDto {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
}