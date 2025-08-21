package com.pokeanalytics.userteamservice.service;

import com.pokeanalytics.userteamservice.dto.team.TeamDetailDto;

public interface TeamFormatService {
    /**
     * 将队伍详情DTO格式化为Pokepaste文本
     * @param teamDto 队伍详情
     * @return 格式化后的文本字符串
     */
    String formatToText(TeamDetailDto teamDto);
}