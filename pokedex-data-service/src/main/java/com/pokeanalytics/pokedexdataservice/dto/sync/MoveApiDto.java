package com.pokeanalytics.pokedexdataservice.dto.sync;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 技能API数据传输对象
 * 用于从PokeAPI获取技能数据
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoveApiDto {
    /**
     * 技能ID
     */
    private int id;
    
    /**
     * 技能英文名称
     */
    private String name;
    
    /**
     * 技能威力
     */
    private Integer power;
    
    /**
     * 技能PP值
     */
    private Integer pp;
    
    /**
     * 技能命中率
     */
    private Integer accuracy;
    
    /**
     * 技能属性
     */
    private Map<String, String> type;
    
    /**
     * 技能名称列表（各语言）
     */
    private List<PokemonSpeciesApiDto.NameEntry> names;

    /**
     * 伤害类别
     */
    @JsonProperty("damage_class")
    private Map<String, String> damageClass;

    /**
     * 技能风味文本列表
     */
    @JsonProperty("flavor_text_entries")
    private List<PokemonSpeciesApiDto.FlavorTextEntry> flavorTextEntries;
}