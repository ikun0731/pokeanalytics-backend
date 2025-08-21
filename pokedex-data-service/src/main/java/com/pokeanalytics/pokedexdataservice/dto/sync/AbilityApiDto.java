package com.pokeanalytics.pokedexdataservice.dto.sync;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * 特性API数据传输对象
 * 用于从PokeAPI获取特性数据
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbilityApiDto {
    /**
     * 特性ID
     */
    private int id;
    
    /**
     * 特性英文名称
     */
    private String name;
    
    /**
     * 特性名称列表（各语言）
     */
    private List<PokemonSpeciesApiDto.NameEntry> names;
    
    /**
     * 特性风味文本列表
     */
    @JsonProperty("flavor_text_entries")
    private List<PokemonSpeciesApiDto.FlavorTextEntry> flavorTextEntries;
}