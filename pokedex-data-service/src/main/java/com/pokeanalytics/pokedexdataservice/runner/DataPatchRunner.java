package com.pokeanalytics.pokedexdataservice.runner;

import com.pokeanalytics.pokedexdataservice.dto.sync.PokemonApiDto;
import com.pokeanalytics.pokedexdataservice.dto.sync.PokemonSpeciesApiDto;
import com.pokeanalytics.pokedexdataservice.entity.Pokemon;
import com.pokeanalytics.pokedexdataservice.sync.PokemonSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 宝可梦数据补丁运行器
 * 用于从外部API获取并补全缺失的宝可梦数据
 * 只在指定的"patch-data"配置文件下运行
 */
@Profile("patch-data")
@Component
@Slf4j
public class DataPatchRunner implements CommandLineRunner {
    
    /**
     * PokeAPI基础URL
     */
    private static final String POKEAPI_BASE_URL = "https://pokeapi.co/api/v2/pokemon/";

    private final PokemonSyncService pokemonService;
    private final RestTemplate restTemplate;

    /**
     * 构造函数
     *
     * @param pokemonService 宝可梦同步服务
     * @param restTemplate REST请求模板
     */
    public DataPatchRunner(PokemonSyncService pokemonService, RestTemplate restTemplate) {
        this.pokemonService = pokemonService;
        this.restTemplate = restTemplate;
    }

    /**
     * 运行数据补丁任务
     * 
     * @param args 命令行参数
     * @throws Exception 执行过程中可能抛出的异常
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("【最终数据补丁任务】开始...");
        List<Pokemon> pokemonList = pokemonService.list();

        for (Pokemon pokemon : pokemonList) {
            // 使用Height字段作为判断是否需要补丁的标志
            if (pokemon.getHeight() == null) {
                patchPokemonData(pokemon);
            }
        }
        log.info("【最终数据补丁任务】完成！");
    }
    
    /**
     * 为单个宝可梦补全数据
     *
     * @param pokemon 需要补全数据的宝可梦实体
     */
    private void patchPokemonData(Pokemon pokemon) {
        try {
            // 获取宝可梦基本数据
            String pokemonUrl = POKEAPI_BASE_URL + pokemon.getId();
            PokemonApiDto pokemonDto = restTemplate.getForObject(pokemonUrl, PokemonApiDto.class);
            if (pokemonDto == null) {
                return;
            }

            // 获取宝可梦种族数据
            String speciesUrl = pokemonDto.getSpecies().get("url");
            PokemonSpeciesApiDto speciesDto = restTemplate.getForObject(speciesUrl, PokemonSpeciesApiDto.class);
            if (speciesDto == null) {
                return;
            }

            boolean needsUpdate = false;

            // 更新图片链接
            if (pokemon.getSpriteHd() == null || pokemon.getSpriteHd().isEmpty()) {
                updateSprites(pokemon, pokemonDto);
                needsUpdate = true;
            }

            // 更新基本数据
            updateBasicInfo(pokemon, pokemonDto);
            needsUpdate = true;

            // 更新努力值
            updateEvYield(pokemon, pokemonDto);

            // 更新种族数据
            updateSpeciesData(pokemon, speciesDto);

            if (needsUpdate) {
                pokemonService.updateById(pokemon);
                log.info("成功为 #{} {} 补全数据", pokemon.getId(), pokemon.getNameCn());
            }
        } catch (Exception e) {
            log.error("为宝可梦 #{} 补全数据时出错", pokemon.getId(), e);
        }
    }

    /**
     * 更新宝可梦图片资源
     *
     * @param pokemon 宝可梦实体
     * @param pokemonDto API返回的宝可梦数据
     */
    private void updateSprites(Pokemon pokemon, PokemonApiDto pokemonDto) {
        if (pokemonDto.getSprites() != null) {
            pokemon.setSpritePixel(pokemonDto.getSprites().getFrontDefault());
            pokemon.setSpritePixelShiny(pokemonDto.getSprites().getFrontShiny());
            
            if (pokemonDto.getSprites().getOther() != null && 
                pokemonDto.getSprites().getOther().getOfficialArtwork() != null) {
                pokemon.setSpriteHd(pokemonDto.getSprites().getOther().getOfficialArtwork().getFrontDefault());
                pokemon.setSpriteHdShiny(pokemonDto.getSprites().getOther().getOfficialArtwork().getFrontShiny());
            }
        }
    }
    
    /**
     * 更新宝可梦基本信息
     *
     * @param pokemon 宝可梦实体
     * @param pokemonDto API返回的宝可梦数据
     */
    private void updateBasicInfo(Pokemon pokemon, PokemonApiDto pokemonDto) {
        // 身高单位从分米转为米
        pokemon.setHeight(pokemonDto.getHeight() / 10.0);
        // 体重单位从百克转为千克
        pokemon.setWeight(pokemonDto.getWeight() / 10.0);
        pokemon.setBaseExperience(pokemonDto.getBaseExperience());
    }
    
    /**
     * 更新宝可梦努力值
     *
     * @param pokemon 宝可梦实体
     * @param pokemonDto API返回的宝可梦数据
     */
    private void updateEvYield(Pokemon pokemon, PokemonApiDto pokemonDto) {
        String evYield = pokemonDto.getStats().stream()
                .filter(s -> s.getEffort() > 0)
                .map(s -> s.getStat().get("name") + ":" + s.getEffort())
                .collect(Collectors.joining(", "));
        pokemon.setEvYield(evYield);
    }
    
    /**
     * 更新宝可梦种族数据
     *
     * @param pokemon 宝可梦实体
     * @param speciesDto API返回的宝可梦种族数据
     */
    private void updateSpeciesData(Pokemon pokemon, PokemonSpeciesApiDto speciesDto) {
        pokemon.setCaptureRate(speciesDto.getCaptureRate());
        pokemon.setGenderRate(speciesDto.getGenderRate());
        // 孵化步数计算公式
        pokemon.setHatchSteps(255 * (speciesDto.getHatchCounter() + 1));

        if (speciesDto.getEggGroups() != null && !speciesDto.getEggGroups().isEmpty()) {
            pokemon.setEggGroup1(speciesDto.getEggGroups().get(0).get("name"));
            if (speciesDto.getEggGroups().size() > 1) {
                pokemon.setEggGroup2(speciesDto.getEggGroups().get(1).get("name"));
            }
        }
    }
}