package com.pokeanalytics.userteamservice.dto.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeAnalysisDto {
    private String typeName;
    private Integer weakTo;
    private Integer resists;
    private Integer immuneTo;
}
