package com.pokeanalytics.pokedexdataservice.dto.sync;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 进化链API数据传输对象
 * 用于从PokeAPI获取宝可梦进化链数据
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvolutionChainApiDto {
    /**
     * 进化链ID
     */
    private int id;
    
    /**
     * 进化链根节点
     */
    private ChainLink chain;

    /**
     * 进化链节点
     * 表示进化链中的一个宝可梦及其进化信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChainLink {
        /**
         * 宝可梦物种信息
         */
        private Map<String, String> species;
        
        /**
         * 进化到的下一级宝可梦列表
         */
        @JsonProperty("evolves_to")
        private List<ChainLink> evolvesTo;
        
        /**
         * 进化详情列表
         */
        @JsonProperty("evolution_details")
        private List<EvolutionDetail> evolutionDetails;
    }

    /**
     * 进化详情
     * 包含进化的具体条件
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EvolutionDetail {
        /**
         * 进化触发方式
         */
        private Map<String, String> trigger;
        
        /**
         * 进化所需道具
         */
        private Map<String, String> item;
        
        /**
         * 进化所需最低等级
         */
        @JsonProperty("min_level")
        private Integer minLevel;
    }
}