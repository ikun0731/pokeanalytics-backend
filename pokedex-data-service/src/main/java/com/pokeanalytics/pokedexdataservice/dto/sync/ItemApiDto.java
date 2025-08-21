package com.pokeanalytics.pokedexdataservice.dto.sync;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 道具API数据传输对象
 * 用于从PokeAPI获取道具数据
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemApiDto {
    /**
     * 道具ID
     */
    private int id;
    
    /**
     * 道具英文名称
     */
    private String name;
    
    /**
     * 道具名称列表（各语言）
     */
    private List<PokemonSpeciesApiDto.NameEntry> names;
    
    /**
     * 道具分类信息
     */
    private Map<String, String> category;
    
    /**
     * 道具图片信息
     */
    private Map<String, String> sprites;

    /**
     * 道具风味文本列表
     */
    @JsonProperty("flavor_text_entries")
    private List<ItemFlavorTextEntry> flavorTextEntries;

    /**
     * 道具效果列表
     */
    @JsonProperty("effect_entries")
    private List<EffectEntry> effectEntries;

    /**
     * 道具效果描述
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EffectEntry {
        /**
         * 详细效果描述
         */
        private String effect;
        
        /**
         * 简短效果描述
         */
        @JsonProperty("short_effect")
        private String shortEffect;
        
        /**
         * 语言信息
         */
        private Map<String, String> language;
    }

    /**
     * 道具风味文本条目
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemFlavorTextEntry {
        /**
         * 文本内容
         */
        @JsonProperty("text")
        private String text;
        
        /**
         * 语言信息
         */
        private Map<String, String> language;
        
        /**
         * 游戏版本组
         */
        @JsonProperty("version_group")
        private Map<String, String> versionGroup;
    }
}