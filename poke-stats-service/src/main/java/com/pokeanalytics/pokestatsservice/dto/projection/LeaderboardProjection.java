package com.pokeanalytics.pokestatsservice.dto.projection;

import lombok.Data;
import java.math.BigDecimal;

// 这个DTO专门用于接收Mapper的多表查询结果，字段类型与SQL查询出的列完全对应
@Data
public class LeaderboardProjection {
    private Integer rank;
    private Integer pokemonId;
    private String nameCn;
    private String nameEn;
    private String typesCn; // 注意：这里是 String 类型
    private String spritePixel;
    private String tier;
    private BigDecimal usageRate;
    private String topItem;
}