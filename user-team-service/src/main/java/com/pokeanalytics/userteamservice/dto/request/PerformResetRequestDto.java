package com.pokeanalytics.userteamservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 执行密码重置请求数据传输对象
 * 包含执行密码重置所需的邮箱、验证码和新密码信息
 */
@Data
public class PerformResetRequestDto {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "验证码不能为空")
    private String code;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 30, message = "新密码长度必须在6到30个字符之间")
    private String newPassword;
}