package com.pokeanalytics.userteamservice.dto.team;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建队伍请求数据传输对象
 * 包含创建宝可梦队伍所需的队伍名称、描述、格式和成员信息
 */
@Data
public class CreateTeamRequestDto {
    @NotBlank(message = "队伍名称不能为空")
    private String teamName;
    private String description;
    private String format;

    @Valid // 启用嵌套校验
    @Size(min = 1, max = 6, message = "队伍成员数量必须在1到6个之间")
    private List<TeamMemberDto> members;
}