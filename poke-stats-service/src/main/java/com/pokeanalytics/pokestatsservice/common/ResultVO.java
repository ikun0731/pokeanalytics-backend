package com.pokeanalytics.pokestatsservice.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一API响应结果封装类
 * 用于规范化所有接口的返回格式，便于前端处理
 *
 * @param <T> 返回的数据类型
 */
@Data
public class ResultVO<T> implements Serializable {

    /**
     * 响应状态码
     * 200表示成功，其他值表示失败
     */
    private Integer code;
    
    /**
     * 响应消息
     * 成功时为"Success"，失败时提供错误描述
     */
    private String message;
    
    /**
     * 响应数据
     * 成功时包含返回的数据，失败时通常为null
     */
    private T data;

    /**
     * 成功时调用的静态工厂方法
     *
     * @param data 成功返回的数据
     * @return 包含成功状态和数据的ResultVO实例
     * @param <T> 返回的数据类型
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
     *
     * @param code 错误码
     * @param message 错误信息
     * @return 包含失败状态和错误信息的ResultVO实例
     * @param <T> 返回的数据类型
     */
    public static <T> ResultVO<T> fail(Integer code, String message) {
        ResultVO<T> resultVO = new ResultVO<>();
        resultVO.setCode(code);
        resultVO.setMessage(message);
        return resultVO;
    }
}