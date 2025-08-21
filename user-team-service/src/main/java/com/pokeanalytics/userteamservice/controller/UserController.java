package com.pokeanalytics.userteamservice.controller;

import com.pokeanalytics.userteamservice.common.ResultVO;
import com.pokeanalytics.userteamservice.dto.request.*;
import com.pokeanalytics.userteamservice.dto.response.LoginResponseDto;
import com.pokeanalytics.userteamservice.dto.response.UserResponseDto;
import com.pokeanalytics.userteamservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "用户认证与管理", description = "提供用户注册、登录、信息获取和密码管理等一系列核心功能")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "用户注册", description = "提供用户名、密码和邮箱进行新用户注册。用户名必须唯一。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "注册成功"),
            @ApiResponse(responseCode = "400", description = "请求参数校验失败（如用户名/密码长度不足）"),
            @ApiResponse(responseCode = "409", description = "用户名或邮箱已被注册")
    })
    @PostMapping("/register")
    public ResultVO<Void> registerUser(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        userService.register(registerRequestDto);
        return ResultVO.success(null, "注册成功");
    }

    @Operation(summary = "用户名密码登录", description = "使用用户名和密码进行认证。认证成功后，返回JWT用于后续接口的访问。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功，返回JWT"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    @PostMapping("/login")
    public ResultVO<LoginResponseDto> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        String token = userService.login(loginRequestDto);
        return ResultVO.success(new LoginResponseDto(token), "登录成功");
    }

    @Operation(summary = "邮箱验证码登录", description = "使用邮箱和接收到的验证码进行认证。认证成功后，返回JWT。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功，返回JWT"),
            @ApiResponse(responseCode = "400", description = "验证码不正确或已过期")
    })
    @PostMapping("/login/code/perform")
    public ResultVO<LoginResponseDto> loginWithCode(@Valid @RequestBody EmailCodeLoginRequestDto requestDto) {
        String token = userService.loginWithCode(requestDto);
        return ResultVO.success(new LoginResponseDto(token), "登录成功");
    }

    @Operation(summary = "获取当前用户信息", description = "需要携带有效的JWT进行访问。此接口用于验证Token并获取当前登录用户的用户名。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户信息"),
            @ApiResponse(responseCode = "401", description = "未提供或提供了无效的JWT")
    })
    @GetMapping("/me")
    public ResultVO<String> getCurrentUser(Authentication authentication) {
        String currentUsername = authentication.getName();
        return ResultVO.success("当前登录的用户是: " + currentUsername);
    }

    @Operation(summary = "请求登录验证码", description = "向已注册的邮箱发送一个用于登录的6位验证码。验证码有效期为5分钟。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "请求已受理。为防止用户信息泄露，无论邮箱是否存在，均返回此成功响应。")
    })
    @PostMapping("/login/code/request")
    public ResultVO<Void> requestLoginCode(@Valid @RequestBody PasswordResetRequestDto requestDto) {
        userService.requestLoginCode(requestDto.getEmail());
        return ResultVO.success(null, "如果您的邮箱已注册，一封包含登录验证码的邮件将会发送给您。");
    }

    @Operation(summary = "请求重置密码验证码", description = "向已注册的邮箱发送一个用于重置密码的6位验证码。验证码有效期为10分钟。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "请求已受理。同样，为防止用户信息泄露，无论邮箱是否存在，均返回此成功响应。")
    })
    @PostMapping("/password/request-reset")
    public ResultVO<Void> requestPasswordReset(@Valid @RequestBody PasswordResetRequestDto requestDto) {
        userService.requestPasswordReset(requestDto.getEmail());
        return ResultVO.success(null, "如果您的邮箱已注册，一封包含验证码的邮件将会发送给您。");
    }

    @Operation(summary = "执行密码重置", description = "使用邮箱、验证码和新密码来完成密码的重置操作。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "密码重置成功"),
            @ApiResponse(responseCode = "400", description = "验证码不正确或已过期")
    })
    @PostMapping("/password/perform-reset")
    public ResultVO<Void> performPasswordReset(@Valid @RequestBody PerformResetRequestDto requestDto) {
        userService.performPasswordReset(requestDto);
        return ResultVO.success(null, "密码重置成功");
    }

    @GetMapping("/profile")
    @Operation(summary = "获取当前用户的个人资料", description = "获取已登录用户的详细个人信息资料。需要有效的JWT令牌。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户资料"),
            @ApiResponse(responseCode = "401", description = "未提供或提供了无效的JWT")
    })
    public ResultVO<UserResponseDto> getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        return ResultVO.success(userService.getUserProfile(username));
    }


    @PutMapping("/profile")
    @Operation(summary = "更新当前用户的邮箱", description = "更新已登录用户的邮箱地址。需要提供当前密码以验证身份。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "邮箱更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数校验失败或当前密码不正确"),
            @ApiResponse(responseCode = "401", description = "未提供或提供了无效的JWT"),
            @ApiResponse(responseCode = "409", description = "新邮箱已被其他用户注册")
    })
    public ResultVO<Void> updateUserProfile(Authentication authentication, @Valid @RequestBody UpdateEmailRequestDto requestDto) {
        String username = authentication.getName();
        userService.updateEmail(username, requestDto);
        return ResultVO.success(null, "邮箱更新成功");
    }


    @PutMapping("/password/change")
    @Operation(summary = "当前用户修改密码", description = "更改已登录用户的密码。需要提供当前密码以验证身份。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "密码修改成功"),
            @ApiResponse(responseCode = "400", description = "请求参数校验失败或当前密码不正确"),
            @ApiResponse(responseCode = "401", description = "未提供或提供了无效的JWT")
    })
    public ResultVO<Void> changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordRequestDto requestDto) {
        String username = authentication.getName();
        userService.changePassword(username, requestDto);
        return ResultVO.success(null, "密码修改成功");
    }
}