package com.pokeanalytics.userteamservice.controller;

import com.pokeanalytics.userteamservice.common.ResultVO;
import com.pokeanalytics.userteamservice.dto.analysis.TypeAnalysisDto;
import com.pokeanalytics.userteamservice.dto.team.*;
import com.pokeanalytics.userteamservice.entity.Team;
import com.pokeanalytics.userteamservice.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/teams")
@Tag(name = "队伍管理", description = "提供队伍的创建、查询、更新和删除功能")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    @Operation(summary = "创建新队伍", description = "创建一个包含1-6个成员的完整队伍。需要有效的JWT认证。")
    public ResultVO<Long> createTeam(@Valid @RequestBody CreateTeamRequestDto requestDto, Authentication authentication) {
        String username = authentication.getName();
        Team createdTeam = teamService.createTeam(requestDto, username);
        return ResultVO.success(createdTeam.getId(), "队伍创建成功");
    }
    @GetMapping("/{teamId}")
    @Operation(summary = "获取单个队伍详情", description = "根据队伍ID获取其完整配置信息。只能获取自己的队伍。")
    public ResultVO<TeamDetailDto> getTeamById(@PathVariable Long teamId, Authentication authentication) {
        TeamDetailDto team = teamService.getTeamById(teamId, authentication.getName());
        return ResultVO.success(team);
    }
    @GetMapping
    @Operation(summary = "获取当前用户的所有队伍列表", description = "返回一个包含该用户所有队伍摘要信息的列表。需要有效的JWT认证。")
    public ResultVO<List<TeamSummaryDto>> getTeamList(Authentication authentication) {
        List<TeamSummaryDto> teams = teamService.getTeamsByUsername(authentication.getName());
        return ResultVO.success(teams);
    }
    @PutMapping("/{teamId}")
    @Operation(summary = "更新指定队伍", description = "根据队伍ID更新队伍的完整信息。会替换掉所有旧的队伍成员。")
    public ResultVO<Long> updateTeam(
            @PathVariable Long teamId,
            @Valid @RequestBody CreateTeamRequestDto requestDto,
            Authentication authentication
    ) {
        teamService.updateTeam(teamId, requestDto, authentication.getName());
        return ResultVO.success(teamId, "队伍更新成功");
    }
    @DeleteMapping("/{teamId}")
    @Operation(summary = "删除指定队伍", description = "根据队伍ID删除队伍及其所有成员。只能删除自己的队伍。")
    public ResultVO<Void> deleteTeam(
            @PathVariable Long teamId,
            Authentication authentication
    ) {
        teamService.deleteTeam(teamId, authentication.getName());
        return ResultVO.success(null, "队伍删除成功");
    }
    @PostMapping("/import")
    @Operation(summary = "从文本导入队伍", description = "从Pokepaste/Pokémon Showdown格式的文本中解析并创建一个新队伍。")
    public ResultVO<Long> importTeam(@Valid @RequestBody ImportTeamRequestDto requestDto, Authentication authentication) {
        Team importedTeam = teamService.importTeam(requestDto, authentication.getName());
        return ResultVO.success(importedTeam.getId(), "队伍导入成功");
    }
    @GetMapping("/{teamId}/export")
    @Operation(summary = "导出指定队伍为文本格式", description = "返回一个可以直接粘贴到Pokémon Showdown的文本。")
    public ResponseEntity<String> exportTeam(@PathVariable Long teamId, Authentication authentication) {
        String teamText = teamService.exportTeam(teamId, authentication.getName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        // 设置内容处理方式为文件下载，并指定文件名
        headers.setContentDispositionFormData("attachment", "team-" + teamId + ".txt");

        return ResponseEntity.ok()
                .headers(headers)
                .body(teamText);
    }
    @GetMapping("/{teamId}/analysis")
    @Operation(summary = "分析指定队伍的属性弱点", description = "分析指定队伍的每个成员对18种属性的抗性情况，包括弱点数量、抵抗数量和免疫数量。")
    public ResultVO<List<TypeAnalysisDto>> analyzeTeam(@PathVariable Long teamId, Authentication authentication) {
        List<TypeAnalysisDto> analysis = teamService.analyzeTeam(teamId, authentication.getName());
        return ResultVO.success(analysis);
    }
}