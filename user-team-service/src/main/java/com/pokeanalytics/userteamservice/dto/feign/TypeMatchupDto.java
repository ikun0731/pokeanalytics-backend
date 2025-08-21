package com.pokeanalytics.userteamservice.dto.feign;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 属性相克关系数据传输对象
 * 描述攻击属性对防御属性的克制倍率
 */
@Data
public class TypeMatchupDto {
    /** 攻击属性（英文） */
    private String attackingType;
    
    /** 防御属性（英文） */
    private String defendingType;
    
    /** 相克倍率，如2.0表示效果拔群，0.5表示效果不佳，0表示免疫 */
    private BigDecimal multiplier;
}
