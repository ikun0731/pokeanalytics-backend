package com.pokeanalytics.userteamservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 密码重置请求数据传输对象
 * 包含用户请求密码重置所需的邮箱信息
 */
@Data
public class PasswordResetRequestDto {
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}