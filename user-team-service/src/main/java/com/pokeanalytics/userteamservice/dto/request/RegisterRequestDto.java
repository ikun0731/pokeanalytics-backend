package com.pokeanalytics.userteamservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求数据传输对象
 * 包含用户注册所需的用户名、密码和邮箱信息
 */
@Data
public class RegisterRequestDto {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3到20个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 30, message = "密码长度必须在6到30个字符之间")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;
}