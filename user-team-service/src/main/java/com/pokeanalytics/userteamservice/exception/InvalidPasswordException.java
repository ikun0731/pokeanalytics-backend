package com.pokeanalytics.userteamservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 无效密码异常
 * 当用户提供的密码不正确时抛出此异常
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // 自动返回 400 Bad Request 状态码
public class InvalidPasswordException extends RuntimeException {
    /**
     * 构造方法
     *
     * @param message 异常信息
     */
    public InvalidPasswordException(String message) {
        super(message);
    }
}