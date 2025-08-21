package com.pokeanalytics.userteamservice.service.impl;

import com.pokeanalytics.userteamservice.common.ResultVO;
import com.pokeanalytics.userteamservice.dto.analysis.TypeAnalysisDto;
import com.pokeanalytics.userteamservice.dto.feign.PokemonDetailFeignDto;
import com.pokeanalytics.userteamservice.dto.feign.TypeMatchupDto;
import com.pokeanalytics.userteamservice.dto.team.TeamDetailDto;
import com.pokeanalytics.userteamservice.dto.team.TeamMemberDetailDto;
import com.pokeanalytics.userteamservice.feign.PokedexDataClient;
import com.pokeanalytics.userteamservice.service.TeamAnalysisService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamAnalysisServiceImpl implements TeamAnalysisService {

    private final PokedexDataClient pokedexDataClient;

    private static final Map<String, String> TYPE_CN_TO_EN_MAP = Map.ofEntries(
            Map.entry("一般", "Normal"), Map.entry("火", "Fire"), Map.entry("水", "Water"),
            Map.entry("电", "Electric"), Map.entry("草", "Grass"), Map.entry("冰", "Ice"),
            Map.entry("格斗", "Fighting"), Map.entry("毒", "Poison"), Map.entry("地面", "Ground"),
            Map.entry("飞行", "Flying"), Map.entry("超能力", "Psychic"), Map.entry("虫", "Bug"),
            Map.entry("岩石", "Rock"), Map.entry("幽灵", "Ghost"), Map.entry("龙", "Dragon"),
            Map.entry("恶", "Dark"), Map.entry("钢", "Steel"), Map.entry("妖精", "Fairy")
    );

    public TeamAnalysisServiceImpl(PokedexDataClient pokedexDataClient) {
        this.pokedexDataClient = pokedexDataClient;
    }

    /**
     * 分析队伍对各种属性的抗性
     * 
     * @param team 队伍详情DTO对象
     * @return 队伍对各种属性的抗性分析结果列表
     */
    @Override
    @Cacheable(value = "teamTypeAnalysis", key = "#team.id")
    public List<TypeAnalysisDto> analyzeTeamTypeWeakness(TeamDetailDto team) {
        Set<String> pokemonNames = team.getMembers().stream()
                .map(TeamMemberDetailDto::getPokemonNameEn)
                .collect(Collectors.toSet());

        // 【核心修正】现在我们获取到的是一个 Map<String, PokemonDetailFeignDto>
        Map<String, PokemonDetailFeignDto> pokemonDetailsMap = new HashMap<>();
        if (!pokemonNames.isEmpty()) {
            ResultVO<Map<String, PokemonDetailFeignDto>> pokemonDetailsResult = pokedexDataClient.getPokemonListByNames(new ArrayList<>(pokemonNames));
            if (pokemonDetailsResult != null && pokemonDetailsResult.getData() != null) {
                // 直接赋值，因为返回的已经是 Map
                pokemonDetailsMap = pokemonDetailsResult.getData();
            }
        }

        ResultVO<List<TypeMatchupDto>> matchupsResult = pokedexDataClient.getAllTypeMatchups();
        if (matchupsResult == null || matchupsResult.getData() == null) {
            return Collections.emptyList();
        }
        List<TypeMatchupDto> allMatchups = matchupsResult.getData();

        Map<String, TypeAnalysisDto> analysisMap = initializeAnalysisMap();

        for (Map.Entry<String, TypeAnalysisDto> entry : analysisMap.entrySet()) {
            String attackingTypeCn = entry.getKey();
            String attackingTypeEn = TYPE_CN_TO_EN_MAP.get(attackingTypeCn);
            TypeAnalysisDto analysisDto = entry.getValue();

            if (attackingTypeEn == null) continue;

            for (TeamMemberDetailDto member : team.getMembers()) {
                // 使用原始英文名从 Map 中获取详情
                PokemonDetailFeignDto details = pokemonDetailsMap.get(member.getPokemonNameEn());
                if (details == null || details.getTypesEn() == null) continue;

                List<String> defendingTypes = details.getTypesEn();

                double totalMultiplier = 1.0;
                for (String defendingType : defendingTypes) {
                    totalMultiplier *= getMultiplier(allMatchups, attackingTypeEn, defendingType);
                }

                // 根据总倍率计算该宝可梦对此属性的抗性: >1.0是弱点，<1.0是抵抗，=0是免疫
                if (totalMultiplier > 1.0) analysisDto.setWeakTo(analysisDto.getWeakTo() + 1);
                else if (totalMultiplier < 1.0 && totalMultiplier > 0.0) analysisDto.setResists(analysisDto.getResists() + 1);
                else if (totalMultiplier == 0.0) analysisDto.setImmuneTo(analysisDto.getImmuneTo() + 1);
            }
        }

        return new ArrayList<>(analysisMap.values());
    }

    /**
     * 获取属性克制倍率
     * 
     * @param matchups 所有属性克制关系列表
     * @param attacking 攻击属性
     * @param defending 防御属性
     * @return 克制倍率
     */
    private double getMultiplier(List<TypeMatchupDto> matchups, String attacking, String defending) {
        return matchups.stream()
                .filter(m -> m.getAttackingType().equalsIgnoreCase(attacking) && m.getDefendingType().equalsIgnoreCase(defending))
                .map(TypeMatchupDto::getMultiplier)
                .map(BigDecimal::doubleValue)
                .findFirst()
                .orElse(1.0);
    }

    /**
     * 初始化属性分析映射表
     * 
     * @return 初始化的属性分析映射表，包含所有18种属性及其初始数据
     */
    private Map<String, TypeAnalysisDto> initializeAnalysisMap() {
        String[] types = {"一般", "火", "水", "电", "草", "冰", "格斗", "毒", "地面", "飞行", "超能力", "虫", "岩石", "幽灵", "龙", "恶", "钢", "妖精"};
        Map<String, TypeAnalysisDto> map = new LinkedHashMap<>();
        for (String type : types) {
            map.put(type, new TypeAnalysisDto(type, 0, 0, 0));
        }
        return map;
    }
}