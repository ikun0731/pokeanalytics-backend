package com.pokeanalytics.userteamservice.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一API响应结果封装类
 * @param <T>
 */
@Data
public class ResultVO<T> implements Serializable {

    private Integer code;
    private String message;
    private T data;

    /**
     * 成功时调用的静态工厂方法
     * @param data 成功返回的数据
     * @return ResultVO 实例
     */
    public static <T> ResultVO<T> success(T data) {
        ResultVO<T> resultVO = new ResultVO<>();
        resultVO.setCode(200);
        resultVO.setMessage("Success");
        resultVO.setData(data);
        return resultVO;
    }

    /**
     * 失败时调用的静态工厂方法
     * @param code 错误码
     * @param message 错误信息
     * @return ResultVO 实例
     */
    public static <T> ResultVO<T> fail(Integer code, String message) {
        ResultVO<T> resultVO = new ResultVO<>();
        resultVO.setCode(code);
        resultVO.setMessage(message);
        return resultVO;
    }
    /**
     * 成功时调用的静态工厂方法（可自定义成功信息）
     * @param data 成功返回的数据
     * @param message 自定义的成功信息
     * @return ResultVO 实例
     */
    public static <T> ResultVO<T> success(T data, String message) {
        ResultVO<T> resultVO = new ResultVO<>();
        resultVO.setCode(200);
        resultVO.setMessage(message);
        resultVO.setData(data);
        return resultVO;
    }
}