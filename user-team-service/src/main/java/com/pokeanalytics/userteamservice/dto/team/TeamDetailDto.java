package com.pokeanalytics.userteamservice.dto.team;

import lombok.Data;
import java.util.List;

@Data
public class TeamDetailDto {
    private Long id;
    private String teamName;
    private String description;
    private String format;
    private List<TeamMemberDetailDto> members;
}