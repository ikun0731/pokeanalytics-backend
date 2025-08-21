package com.pokeanalytics.userteamservice.service;

import com.pokeanalytics.userteamservice.dto.request.AiAnalyzeRequestDto;

public interface AiService {
    String getPokemonAnalysis(AiAnalyzeRequestDto requestDto);
}