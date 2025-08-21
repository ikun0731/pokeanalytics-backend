package com.pokeanalytics.pokedexdataservice.dto.sync;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 宝可梦API数据传输对象
 * 用于从PokeAPI获取宝可梦基础数据
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonApiDto {

    /**
     * 宝可梦ID
     */
    private int id;
    
    /**
     * 宝可梦英文名称
     */
    private String name;
    
    /**
     * 宝可梦身高（单位：分米）
     */
    private int height;
    
    /**
     * 宝可梦体重（单位：百克）
     */
    private int weight;

    /**
     * 基础经验值
     */
    @JsonProperty("base_experience")
    private int baseExperience;

    /**
     * 能力值列表
     */
    private List<StatEntry> stats;
    
    /**
     * 属性列表
     */
    private List<TypeEntry> types;
    
    /**
     * 图片资源
     */
    private Sprites sprites;
    
    /**
     * 可学习的技能列表
     */
    private List<MoveEntry> moves;
    
    /**
     * 特性列表
     */
    private List<AbilityEntry> abilities;

    /**
     * 是否为默认形态
     */
    @JsonProperty("is_default")
    private boolean isDefault;

    /**
     * 过去版本的特性列表
     */
    @JsonProperty("past_abilities")
    private List<PastAbilityEntry> pastAbilities;

    /**
     * 物种信息
     */
    private Map<String, String> species;

    /**
     * 图片资源类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sprites {
        /**
         * 默认正面图片
         */
        @JsonProperty("front_default")
        private String frontDefault;
        
        /**
         * 闪光正面图片
         */
        @JsonProperty("front_shiny")
        private String frontShiny;
        
        /**
         * 其他图片资源
         */
        private OtherSprites other;
    }

    /**
     * 其他图片资源类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OtherSprites {
        /**
         * 官方艺术图
         */
        @JsonProperty("official-artwork")
        private OfficialArtwork officialArtwork;
    }

    /**
     * 官方艺术图类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OfficialArtwork {
        /**
         * 默认正面图片
         */
        @JsonProperty("front_default")
        private String frontDefault;
        
        /**
         * 闪光正面图片
         */
        @JsonProperty("front_shiny")
        private String frontShiny;
    }

    /**
     * 能力值条目类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatEntry {
        /**
         * 基础能力值
         */
        @JsonProperty("base_stat")
        private int baseStat;
        
        /**
         * 努力值
         */
        private int effort;
        
        /**
         * 能力值类型
         */
        private Map<String, String> stat;
    }

    /**
     * 属性条目类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TypeEntry {
        /**
         * 属性槽位
         */
        private int slot;
        
        /**
         * 属性信息
         */
        private Map<String, String> type;
    }

    /**
     * 技能条目类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MoveEntry {
        /**
         * 技能信息
         */
        private Map<String, String> move;
        
        /**
         * 版本组详情列表
         */
        @JsonProperty("version_group_details")
        private List<VersionGroupDetail> versionGroupDetails;
    }

    /**
     * 版本组详情类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VersionGroupDetail {
        /**
         * 学习等级
         */
        @JsonProperty("level_learned_at")
        private int levelLearnedAt;
        
        /**
         * 学习方法
         */
        @JsonProperty("move_learn_method")
        private Map<String, String> moveLearnMethod;
        
        /**
         * 版本组信息
         */
        @JsonProperty("version_group")
        private Map<String, String> versionGroup;
    }

    /**
     * 特性条目类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AbilityEntry {
        /**
         * 特性信息
         */
        private Map<String, String> ability;
        
        /**
         * 是否为隐藏特性
         */
        @JsonProperty("is_hidden")
        private boolean isHidden;
        
        /**
         * 特性槽位
         */
        private int slot;
    }

    /**
     * 过去特性条目类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PastAbilityEntry {
        /**
         * 特性列表
         */
        private List<AbilityEntry> abilities;
        
        /**
         * 世代信息
         */
        private Map<String, String> generation;
    }
}