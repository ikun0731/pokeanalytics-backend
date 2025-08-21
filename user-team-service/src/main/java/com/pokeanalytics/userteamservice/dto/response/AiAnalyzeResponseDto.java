package com.pokeanalytics.userteamservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalyzeResponseDto {
    private String analysis; // AI返回的分析文本
}