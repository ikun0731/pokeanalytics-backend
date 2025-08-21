package com.pokeanalytics.userteamservice.service;

import com.pokeanalytics.userteamservice.dto.analysis.TypeAnalysisDto;
import com.pokeanalytics.userteamservice.dto.team.*;
import com.pokeanalytics.userteamservice.entity.Team;

import java.util.List;

public interface TeamService {
    /**
     * 创建一个新队伍
     * @param requestDto 队伍数据
     * @param username 当前登录的用户名
     * @return 创建成功后的Team实体
     */
    Team createTeam(CreateTeamRequestDto requestDto, String username);
    /**
     * 根据ID获取队伍详情
     * @param teamId 队伍ID
     * @param username 当前用户名，用于校验权限
     * @return 包含成员的Team实体，如果找不到或无权访问则返回null
     */
    TeamDetailDto getTeamById(Long teamId, String username);
    /**
     * 获取指定用户的所有队伍摘要列表
     * @param username 当前用户名
     * @return 队伍摘要DTO列表
     */
    List<TeamSummaryDto> getTeamsByUsername(String username);
    /**
     * 更新一个已存在的队伍
     * @param teamId 要更新的队伍ID
     * @param requestDto 包含新数据的DTO
     * @param username 当前用户名，用于权限校验
     * @return 更新后的Team实体
     */
    Team updateTeam(Long teamId, CreateTeamRequestDto requestDto, String username);
    /**
     * 根据ID删除队伍
     * @param teamId 要删除的队伍ID
     * @param username 当前用户名，用于权限校验
     */
    void deleteTeam(Long teamId, String username);
    Team importTeam(ImportTeamRequestDto requestDto, String username);
    String exportTeam(Long teamId, String username);
    List<TypeAnalysisDto> analyzeTeam(Long teamId, String username);
}