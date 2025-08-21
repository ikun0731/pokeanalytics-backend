package com.pokeanalytics.pokedexdataservice.runner;

import com.pokeanalytics.pokedexdataservice.dto.sync.AbilityApiDto;
import com.pokeanalytics.pokedexdataservice.dto.sync.PokemonApiDto;
import com.pokeanalytics.pokedexdataservice.entity.Ability;
import com.pokeanalytics.pokedexdataservice.entity.Pokemon;
import com.pokeanalytics.pokedexdataservice.entity.PokemonAbility;
import com.pokeanalytics.pokedexdataservice.service.AbilityApiService;
import com.pokeanalytics.pokedexdataservice.service.PokemonAbilityService;
import com.pokeanalytics.pokedexdataservice.sync.PokemonSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 宝可梦特性数据同步启动器
 * 在应用启动时自动执行宝可梦特性关联数据同步任务
 */
@Profile("sync-ability")
@Component
@Slf4j
public class AbilitySyncRunner implements CommandLineRunner {

    private final PokemonSyncService pokemonService;
    private final AbilityApiService abilityService;
    private final PokemonAbilityService pokemonAbilityService;
    private final RestTemplate restTemplate;
    
    /**
     * 最新游戏版本常量
     */
    private static final String LATEST_VERSION_GROUP = "scarlet-violet";
    
    /**
     * PokéAPI基础URL
     */
    private static final String POKEAPI_BASE_URL = "https://pokeapi.co/api/v2/pokemon/";

    /**
     * 构造函数
     *
     * @param pokemonService 宝可梦同步服务
     * @param abilityService 特性服务
     * @param pokemonAbilityService 宝可梦特性关联服务
     * @param restTemplate HTTP请求模板
     */
    public AbilitySyncRunner(
            PokemonSyncService pokemonService, 
            AbilityApiService abilityService, 
            PokemonAbilityService pokemonAbilityService, 
            RestTemplate restTemplate) {
        this.pokemonService = pokemonService;
        this.abilityService = abilityService;
        this.pokemonAbilityService = pokemonAbilityService;
        this.restTemplate = restTemplate;
    }

    /**
     * 应用启动后执行的特性同步方法
     *
     * @param args 命令行参数
     * @throws Exception 同步过程中可能发生的异常
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("【特性同步任务】开始...");
        List<Pokemon> pokemonList = pokemonService.list();

        for (Pokemon pokemon : pokemonList) {
            try {
                syncPokemonAbilities(pokemon);
            } catch (Exception e) {
                log.error("同步宝可梦 #{} 特性时出错", pokemon.getId(), e);
            }
        }
        log.info("【特性同步任务】完成！");
    }

    /**
     * 同步单个宝可梦的特性数据
     *
     * @param pokemon 宝可梦实体
     */
    private void syncPokemonAbilities(Pokemon pokemon) {
        // 清理该宝可梦旧的特性关联，确保幂等性
        QueryWrapper<PokemonAbility> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pokemon_id", pokemon.getId());
        pokemonAbilityService.remove(queryWrapper);

        // 获取宝可梦API数据
        String pokemonUrl = POKEAPI_BASE_URL + pokemon.getId();
        PokemonApiDto pokemonDto = restTemplate.getForObject(pokemonUrl, PokemonApiDto.class);

        if (pokemonDto == null) return;

        // 处理当前世代的特性
        if (pokemonDto.getAbilities() != null) {
            for (PokemonApiDto.AbilityEntry abilityEntry : pokemonDto.getAbilities()) {
                savePokemonAbility(pokemon.getId(), abilityEntry, LATEST_VERSION_GROUP);
            }
        }

        // 处理过去世代的特性
        if (pokemonDto.getPastAbilities() != null) {
            for (PokemonApiDto.PastAbilityEntry pastAbilityEntry : pokemonDto.getPastAbilities()) {
                String generationName = pastAbilityEntry.getGeneration().get("name");
                for (PokemonApiDto.AbilityEntry abilityEntry : pastAbilityEntry.getAbilities()) {
                    savePokemonAbility(pokemon.getId(), abilityEntry, generationName);
                }
            }
        }
        
        log.info("成功同步宝可梦 #{} {} 的特性", pokemon.getId(), pokemon.getNameCn());
    }

    /**
     * 保存宝可梦特性关联数据
     *
     * @param pokemonId 宝可梦ID
     * @param abilityEntry 特性条目
     * @param generation 游戏世代
     */
    private void savePokemonAbility(Integer pokemonId, PokemonApiDto.AbilityEntry abilityEntry, String generation) {
        String abilityUrl = abilityEntry.getAbility().get("url");
        Ability ability = getOrSyncAbility(abilityUrl);

        if (ability != null) {
            PokemonAbility pokemonAbility = new PokemonAbility();
            pokemonAbility.setPokemonId(pokemonId);
            pokemonAbility.setAbilityId(ability.getId());
            pokemonAbility.setIsHidden(abilityEntry.isHidden());
            pokemonAbility.setLastGeneration(generation);
            pokemonAbilityService.save(pokemonAbility);
        }
    }

    /**
     * 获取或同步特性数据
     *
     * @param abilityUrl 特性API URL
     * @return 特性实体，如果同步失败则返回null
     */
    private Ability getOrSyncAbility(String abilityUrl) {
        // 从URL中提取特性ID
        String[] urlParts = abilityUrl.split("/");
        int abilityId = Integer.parseInt(urlParts[urlParts.length - 1]);

        // 检查特性是否已存在
        Ability existingAbility = abilityService.getById(abilityId);
        if (existingAbility != null) {
            return existingAbility;
        }

        // 如果不存在，则从API获取并保存特性
        try {
            AbilityApiDto abilityDto = restTemplate.getForObject(abilityUrl, AbilityApiDto.class);
            if (abilityDto == null) return null;

            Ability newAbility = new Ability();
            newAbility.setId(abilityDto.getId());
            newAbility.setNameEn(abilityDto.getName());

            // 设置中文名称
            abilityDto.getNames().stream()
                    .filter(n -> "zh-Hans".equals(n.getLanguage().get("name")))
                    .findFirst().ifPresent(n -> newAbility.setNameCn(n.getName()));

            // 设置特性效果描述
            abilityDto.getFlavorTextEntries().stream()
                    .filter(ft -> ft.getLanguage() != null && "zh-Hans".equals(ft.getLanguage().get("name")))
                    .findFirst()
                    .ifPresent(ft -> newAbility.setEffect(ft.getFlavorText().replace('\n', ' ')));

            abilityService.save(newAbility);
            log.info("  -> 新特性入库: #{} {}", newAbility.getId(), newAbility.getNameCn());
            return newAbility;
        } catch (Exception e) {
            log.error("同步特性失败: {}", abilityUrl, e);
            return null;
        }
    }
}