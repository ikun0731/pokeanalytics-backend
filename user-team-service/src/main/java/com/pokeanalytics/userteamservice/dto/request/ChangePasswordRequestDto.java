package com.pokeanalytics.userteamservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改密码请求数据传输对象
 * 包含用户修改密码所需的当前密码和新密码信息
 */
@Data
public class ChangePasswordRequestDto {
    @NotEmpty(message = "当前密码不能为空")
    private String oldPassword;

    @NotEmpty(message = "新密码不能为空")
    @Size(min = 6, max = 30, message = "新密码长度必须在6到30个字符之间")
    private String newPassword;
}