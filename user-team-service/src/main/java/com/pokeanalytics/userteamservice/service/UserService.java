package com.pokeanalytics.userteamservice.service;


import com.pokeanalytics.userteamservice.dto.request.*;
import com.pokeanalytics.userteamservice.dto.response.UserResponseDto;


public interface UserService{
    /**
     * 注册新用户
     * @param requestDto 包含用户名、密码、邮箱的注册信息
     */
    void register(RegisterRequestDto requestDto);
    /**
     * 用户登录
     * @param requestDto 包含用户名和密码
     * @return JWT
     */
    String login(LoginRequestDto requestDto);
    void requestPasswordReset(String email);
    void performPasswordReset(PerformResetRequestDto requestDto);
    /**
     * 请求登录验证码
     * @param email 目标邮箱
     */
    void requestLoginCode(String email);

    /**
     * 使用邮箱和验证码登录
     * @param requestDto 包含邮箱和验证码
     * @return JWT
     */
    String loginWithCode(EmailCodeLoginRequestDto requestDto);
    // 【新增】根据用户名获取用户 DTO
    UserResponseDto getUserProfile(String username);

    // 【新增】更新邮箱
    void updateEmail(String username, UpdateEmailRequestDto requestDto);

    // 【新增】修改密码
    void changePassword(String username, ChangePasswordRequestDto requestDto);

    // 【新增】检查邮箱是否已被其他用户使用
    boolean isEmailTakenByOtherUser(String email, String currentUsername);
}
