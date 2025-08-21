package com.pokeanalytics.pokestatsservice.dto;
import lombok.Data;

/**
 * 特性DTO
 * 用于接收Feign客户端从pokedex-data-service获取的特性数据
 */
@Data
public class Ability {
    /**
     * 特性ID
     */
    private Integer id;
    
    /**
     * 特性英文名
     */
    private String nameEn;
    
    /**
     * 特性中文名
     */
    private String nameCn;
}