package com.pokeanalytics.userteamservice.exception;

/**
 * 用户已存在异常
 * 当尝试注册的用户名或邮箱已被使用时抛出此异常
 */
public class UserAlreadyExistsException extends RuntimeException {
    /**
     * 构造方法
     *
     * @param message 异常信息
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}