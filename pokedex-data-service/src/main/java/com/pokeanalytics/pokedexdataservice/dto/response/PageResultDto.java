package com.pokeanalytics.pokedexdataservice.dto.response;

import lombok.Data;
import java.util.List;

/**
 * 分页结果数据传输对象
 * 用于封装分页查询的结果数据
 *
 * @param <T> 列表项的数据类型
 */
@Data
public class PageResultDto<T> {
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