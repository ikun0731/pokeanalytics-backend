package com.pokeanalytics.userteamservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 更新用户邮箱请求数据传输对象
 * 包含更新用户邮箱所需的新邮箱地址和当前密码（用于验证身份）
 */
@Data
public class UpdateEmailRequestDto {
    @NotEmpty(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotEmpty(message = "请输入当前密码以进行验证")
    private String currentPassword;
}