package com.pokeanalytics.userteamservice.exception;

/**
 * 禁止访问异常
 * 当用户尝试访问没有权限的资源时抛出此异常
 */
public class ForbiddenAccessException extends RuntimeException {
    /**
     * 构造方法
     *
     * @param message 异常信息
     */
    public ForbiddenAccessException(String message) {
        super(message);
    }
}