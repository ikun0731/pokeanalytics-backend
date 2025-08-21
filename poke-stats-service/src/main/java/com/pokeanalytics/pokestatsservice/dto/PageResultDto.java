package com.pokeanalytics.pokestatsservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果DTO
 * 用于封装分页查询的结果数据
 *
 * @param <T> 分页数据项的类型
 */
@Data
public class PageResultDto<T> implements Serializable {
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 总页数
     */
    private long totalPages;
    
    /**
     * 当前页的数据列表
     */
    private List<T> items;
}