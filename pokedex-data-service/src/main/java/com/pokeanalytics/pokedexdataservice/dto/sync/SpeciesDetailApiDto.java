package com.pokeanalytics.pokedexdataservice.dto.sync;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 宝可梦物种详情API数据传输对象
 * 用于从PokeAPI获取宝可梦物种详细信息，特别是形态变化
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpeciesDetailApiDto {

    /**
     * 宝可梦形态变种列表
     */
    private List<Variety> varieties;

    /**
     * 宝可梦形态变种类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Variety {
        /**
         * 是否为默认形态
         */
        @JsonProperty("is_default")
        private boolean isDefault;
        
        /**
         * 宝可梦信息
         */
        private Map<String, String> pokemon;
    }
}