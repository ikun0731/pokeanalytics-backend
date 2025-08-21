package com.pokeanalytics.userteamservice.dto.feign;

import lombok.Data;
import java.util.List;

/**
 * 宝可梦详情Feign数据传输对象
 * 与pokedex-data-service服务中/pokemon/batch/by-name接口返回的PokemonListItemDto保持一致
 */
@Data
public class PokemonDetailFeignDto {
    /** 宝可梦ID */
    private Long id;
    
    /** 宝可梦中文名称 */
    private String nameCn;
    
    /** 宝可梦英文名称 */
    private String nameEn;
    
    /** 像素风格精灵图链接 */
    private String spritePixel;
    
    /** 宝可梦中文属性列表 */
    private List<String> types;
    
    /** 宝可梦英文属性列表 */
    private List<String> typesEn;
}