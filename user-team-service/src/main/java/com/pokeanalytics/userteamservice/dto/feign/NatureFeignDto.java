package com.pokeanalytics.userteamservice.dto.feign;

import lombok.Data;

/**
 * 性格Feign数据传输对象
 * 用于获取宝可梦性格信息
 */
@Data
public class NatureFeignDto {
    /** 性格中文名称 */
    private String nameCn;
    
    /** 性格英文名称 */
    private String nameEn;
}