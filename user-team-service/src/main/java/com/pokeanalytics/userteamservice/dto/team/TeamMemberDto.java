package com.pokeanalytics.userteamservice.dto.team;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 队伍成员数据传输对象
 * 包含宝可梦队伍成员的详细配置信息，包括宝可梦名称、道具、特性、性格等
 */
@Data
public class TeamMemberDto {
    private String pokemonNameEn;
    private String nickname;
    private String item;
    private String ability;
    private String teraType;
    private String nature;
    private Boolean isShiny;
    private Map<String, Integer> evs;
    private Map<String, Integer> ivs;
    private List<String> moves;
}