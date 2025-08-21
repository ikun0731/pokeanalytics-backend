package com.pokeanalytics.userteamservice.exception;

/**
 * 无效验证码异常
 * 当用户提供的验证码无效或已过期时抛出此异常
 */
public class InvalidCodeException extends RuntimeException {
    /**
     * 构造方法
     *
     * @param message 异常信息
     */
    public InvalidCodeException(String message) {
        super(message);
    }
}