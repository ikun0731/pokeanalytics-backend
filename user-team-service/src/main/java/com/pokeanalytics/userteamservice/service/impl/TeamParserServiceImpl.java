package com.pokeanalytics.userteamservice.service.impl;

import com.pokeanalytics.userteamservice.dto.team.CreateTeamRequestDto;
import com.pokeanalytics.userteamservice.dto.team.TeamMemberDto;
import com.pokeanalytics.userteamservice.service.TeamParserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class TeamParserServiceImpl implements TeamParserService {


    /**
     * 解析宝可梦队伍文本
     * 
     * @param pasteText 需要解析的宝可梦队伍文本，通常是从Pokemon Showdown或其他格式复制的队伍信息
     * @return 解析后的队伍创建请求DTO对象
     */
    @Override
    public CreateTeamRequestDto parse(String pasteText) {
        CreateTeamRequestDto createTeamDto = new CreateTeamRequestDto();
        List<TeamMemberDto> members = new ArrayList<>();

        String[] pokemonBlocks = pasteText.trim().split("\\n\\s*\\n");

        for (String block : pokemonBlocks) {
            if (!StringUtils.hasText(block)) continue;

            TeamMemberDto member = new TeamMemberDto();
            String[] lines = block.trim().split("\\n");
            String pokemonName = "";
            String ivsLine = null;

            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("-")) {
                    if (member.getMoves() == null) member.setMoves(new ArrayList<>());

                    member.getMoves().add(formatName(line.substring(1).trim()));
                } else if (line.contains(" @ ")) {
                    String[] parts = line.split(" @ ");
                    pokemonName = parts[0].trim();

                    member.setItem(formatName(parts[1].trim()));
                } else if (line.toLowerCase().contains("ability:")) {

                    member.setAbility(formatName(line.split(":")[1].trim()));
                } else if (line.toLowerCase().contains("tera type:")) {

                    member.setTeraType(formatName(line.split(":")[1].trim()));
                } else if (line.toLowerCase().contains("nature")) {

                    member.setNature(formatName(line.replace("Nature", "").trim()));
                } else if (line.toLowerCase().startsWith("evs:")) {
                    member.setEvs(parseEvs(line));
                } else if (line.toLowerCase().startsWith("ivs:")){
                    ivsLine = line;
                }
                else if (pokemonName.isEmpty() && !line.isEmpty()){
                    pokemonName = line.trim();
                }
            }

            // 删除名字中的括号内容，例如"Pikachu (F)" -> "Pikachu"
            String cleanedPokemonName = pokemonName.replaceAll("\\s*\\(.*?\\)\\s*", "").trim();
            member.setPokemonNameEn(formatName(cleanedPokemonName));
            member.setIvs(parseIvs(ivsLine));

            if (member.getIsShiny() == null) {
                member.setIsShiny(false);
            }
            if (member.getMoves() == null) {
                member.setMoves(new ArrayList<>());
            }

            members.add(member);
        }

        createTeamDto.setMembers(members);
        return createTeamDto;
    }


    /**
     * 解析努力值(EVs)数据
     * 
     * @param line 包含EVs数据的文本行
     * @return 解析后的努力值映射表，包含各项能力值的分配
     */
    private Map<String, Integer> parseEvs(String line) {
        // 1. 先创建一个包含所有键且默认值为 0 的完整Map
        Map<String, Integer> evs = new HashMap<>();
        evs.put("hp", 0);
        evs.put("atk", 0);
        evs.put("def", 0);
        evs.put("spa", 0);
        evs.put("spd", 0);
        evs.put("spe", 0);

        // 2. 用解析到的值去覆盖默认值
        String evString = line.split(":")[1].trim();
        String[] stats = evString.split(" / ");
        for (String stat : stats) {
            String[] parts = stat.trim().split(" ");
            int value = Integer.parseInt(parts[0]);
            String statName = parts[1].toLowerCase();
            switch (statName) {
                case "hp": evs.put("hp", value); break;
                case "atk": evs.put("atk", value); break;
                case "def": evs.put("def", value); break;
                case "spa": evs.put("spa", value); break;
                case "spd": evs.put("spd", value); break;
                case "spe": evs.put("spe", value); break;
            }
        }
        return evs;
    }


    /**
     * 解析个体值(IVs)数据
     * 
     * @param line 包含IVs数据的文本行，可为null
     * @return 解析后的个体值映射表，默认所有项都为31
     */
    private Map<String, Integer> parseIvs(String line) {
        // 1. 先创建一个包含所有键且默认值为 31 的完整Map
        Map<String, Integer> ivs = new HashMap<>();
        ivs.put("hp", 31);
        ivs.put("atk", 31);
        ivs.put("def", 31);
        ivs.put("spa", 31);
        ivs.put("spd", 31);
        ivs.put("spe", 31);

        // 2. 如果提供了IVs行，则用解析到的值覆盖
        if (line != null && !line.isEmpty()) {
            String ivString = line.split(":")[1].trim();
            String[] stats = ivString.split(" / ");
            for (String stat : stats) {
                String[] parts = stat.trim().split(" ");
                int value = Integer.parseInt(parts[0]);
                String statName = parts[1].toLowerCase();
                switch (statName) {
                    case "hp": ivs.put("hp", value); break;
                    case "atk": ivs.put("atk", value); break;
                    case "def": ivs.put("def", value); break;
                    case "spa": ivs.put("spa", value); break;
                    case "spd": ivs.put("spd", value); break;
                    case "spe": ivs.put("spe", value); break;
                }
            }
        }
        return ivs;
    }


    /**
     * 格式化名称字符串
     * 将空格和大小写统一为数据库的标准格式（小写，连字符）
     * 
     * @param name 待格式化的名称
     * @return 格式化后的名称
     */
    private String formatName(String name) {
        return name.trim().toLowerCase().replace(" ", "-");
    }
}