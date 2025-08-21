package com.pokeanalytics.userteamservice.service;

import com.pokeanalytics.userteamservice.dto.team.CreateTeamRequestDto;

public interface TeamParserService {
    /**
     * 将Pokepaste格式的文本解析为创建队伍的DTO
     * @param pasteText 原始文本
     * @return CreateTeamRequestDto
     */
    CreateTeamRequestDto parse(String pasteText);
}