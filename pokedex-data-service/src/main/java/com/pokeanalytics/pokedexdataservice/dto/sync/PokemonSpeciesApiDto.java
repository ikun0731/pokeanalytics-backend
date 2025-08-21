package com.pokeanalytics.pokedexdataservice.dto.sync;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 宝可梦物种API数据传输对象
 * 用于从PokeAPI获取宝可梦物种数据
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonSpeciesApiDto {

    /**
     * 性别比例
     * -1表示无性别，0表示全部雄性，8表示全部雌性
     * 1-7表示雌性概率为(value/8)*100%
     */
    @JsonProperty("gender_rate")
    private int genderRate;

    /**
     * 捕获率
     */
    @JsonProperty("capture_rate")
    private int captureRate;

    /**
     * 孵化周期计数
     */
    @JsonProperty("hatch_counter")
    private int hatchCounter;

    /**
     * 蛋组列表
     */
    @JsonProperty("egg_groups")
    private List<Map<String, String>> eggGroups;

    /**
     * 名称列表（各语言）
     */
    private List<NameEntry> names;

    /**
     * 风味文本列表
     */
    @JsonProperty("flavor_text_entries")
    private List<FlavorTextEntry> flavorTextEntries;

    /**
     * 进化前物种信息
     */
    @JsonProperty("evolves_from_species")
    private Map<String, String> evolvesFromSpecies;

    /**
     * 进化链信息
     */
    @JsonProperty("evolution_chain")
    private Map<String, String> evolutionChain;

    /**
     * 名称条目类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NameEntry {
        /**
         * 语言信息
         */
        private Map<String, String> language;
        
        /**
         * 名称
         */
        private String name;
    }

    /**
     * 风味文本条目类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FlavorTextEntry {
        /**
         * 风味文本内容
         */
        @JsonProperty("flavor_text")
        private String flavorText;
        
        /**
         * 语言信息
         */
        private Map<String, String> language;
        
        /**
         * 游戏版本信息
         */
        private Map<String, String> version;
    }
}