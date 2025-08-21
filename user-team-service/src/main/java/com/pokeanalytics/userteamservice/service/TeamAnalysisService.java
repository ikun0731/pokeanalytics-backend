package com.pokeanalytics.userteamservice.service;

import com.pokeanalytics.userteamservice.dto.analysis.TypeAnalysisDto;
import com.pokeanalytics.userteamservice.dto.team.TeamDetailDto;

import java.util.List;

public interface TeamAnalysisService {
    List<TypeAnalysisDto> analyzeTeamTypeWeakness(TeamDetailDto team);
}
