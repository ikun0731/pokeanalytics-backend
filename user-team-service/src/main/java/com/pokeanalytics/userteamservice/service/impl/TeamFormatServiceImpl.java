package com.pokeanalytics.userteamservice.service.impl;

import com.pokeanalytics.userteamservice.dto.team.TeamDetailDto;
import com.pokeanalytics.userteamservice.entity.TeamMember;
import com.pokeanalytics.userteamservice.service.TeamFormatService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

@Service
public class TeamFormatServiceImpl implements TeamFormatService {

    /**
     * 将队伍详情格式化为文本
     * 
     * @param teamDto 队伍详情DTO对象
     * @return 格式化后的队伍文本，可导入到Pokemon Showdown等应用
     */
    @Override
    public String formatToText(TeamDetailDto teamDto) {
        if (teamDto == null || teamDto.getMembers() == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (TeamMember member : teamDto.getMembers()) {
            // 1. 宝可梦名称和道具
            sb.append(formatPokemonName(member.getPokemonNameEn()));
            if (StringUtils.hasText(member.getItem())) {
                sb.append(" @ ").append(formatName(member.getItem()));
            }
            sb.append("\n");

            // 2. 特性
            if (StringUtils.hasText(member.getAbility())) {
                sb.append("Ability: ").append(formatName(member.getAbility())).append("\n");
            }

            // 3. 太晶属性
            if (StringUtils.hasText(member.getTeraType())) {
                sb.append("Tera Type: ").append(formatName(member.getTeraType())).append("\n");
            }

            // 4. 努力值 EVs
            if (member.getEvs() != null && !member.getEvs().isEmpty()) {
                sb.append("EVs: ");
                String evsString = member.getEvs().entrySet().stream()
                        .map(entry -> entry.getValue() + " " + formatStatName(entry.getKey()))
                        .collect(Collectors.joining(" / "));
                sb.append(evsString).append("\n");
            }

            // 5. 性格
            if (StringUtils.hasText(member.getNature())) {
                sb.append(formatName(member.getNature())).append(" Nature\n");
            }

            // 6. 个体值 IVs (通常只在不为31时显示)
            if (member.getIvs() != null && !member.getIvs().isEmpty()) {
                String ivsString = member.getIvs().entrySet().stream()
                        .filter(entry -> entry.getValue() < 31)
                        .map(entry -> entry.getValue() + " " + formatStatName(entry.getKey()))
                        .collect(Collectors.joining(" / "));
                if (StringUtils.hasText(ivsString)) {
                    sb.append("IVs: ").append(ivsString).append("\n");
                }
            }

            // 7. 技能
            if (member.getMoves() != null && !member.getMoves().isEmpty()) {
                member.getMoves().forEach(move ->
                        sb.append("- ").append(formatName(move)).append("\n")
                );
            }

            // 8. 每个宝可梦之间加一个空行
            sb.append("\n");
        }

        return sb.toString().trim();
    }

    /**
     * 将名称首字母大写
     * 例如将"pelipper"格式化为"Pelipper"
     * 
     * @param name 待格式化的名称
     * @return 格式化后的名称
     */
    private String formatName(String name) {
        if (!StringUtils.hasText(name)) return "";
        String[] parts = name.split("-");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1))
                    .append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * 格式化宝可梦名称
     * 例如将"ninetales-alola"格式化为"Ninetales-Alola"
     * 
     * @param name 待格式化的宝可梦名称
     * @return 格式化后的宝可梦名称
     */
    private String formatPokemonName(String name) {
        if (!StringUtils.hasText(name)) return "";
        // 先转换为 Title Case
        String formatted = formatName(name);
        // 再把空格替换回连字符，这是Pokemon Showdown对宝可梦名的特殊要求 (e.g. Charizard-Mega-Y)
        return formatted.replace(" ", "-");
    }


    /**
     * 格式化属性名称
     * 例如将"hp", "atk"等格式化为"HP", "Atk"
     * 
     * @param key 属性名称的键
     * @return 格式化后的属性名称
     */
    private String formatStatName(String key) {
        switch (key.toLowerCase()) {
            case "hp": return "HP";
            case "atk": return "Atk";
            case "def": return "Def";
            case "spa": return "SpA"; // 修正大小写
            case "spd": return "SpD"; // 修正大小写
            case "spe": return "Spe"; // 修正大小写
            default: return "";
        }
    }
}