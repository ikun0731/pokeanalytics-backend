package com.pokeanalytics.userteamservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 邮箱验证码登录请求数据传输对象
 * 包含用户通过邮箱验证码登录所需的邮箱和验证码信息
 */
@Data
public class EmailCodeLoginRequestDto {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "验证码不能为空")
    private String code;
}