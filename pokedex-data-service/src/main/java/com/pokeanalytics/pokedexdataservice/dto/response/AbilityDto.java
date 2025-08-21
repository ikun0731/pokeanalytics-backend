package com.pokeanalytics.pokedexdataservice.dto.response;

import lombok.Data;

@Data
public class AbilityDto {
    private String name;
    private String nameEn;
    private boolean isHidden;
    private String lastGeneration;
}