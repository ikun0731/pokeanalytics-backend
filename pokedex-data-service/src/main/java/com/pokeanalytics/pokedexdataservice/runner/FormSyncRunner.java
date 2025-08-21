package com.pokeanalytics.pokedexdataservice.runner;

import com.pokeanalytics.pokedexdataservice.dto.sync.PokemonSpeciesApiDto;
import com.pokeanalytics.pokedexdataservice.dto.sync.SpeciesDetailApiDto;
import com.pokeanalytics.pokedexdataservice.sync.PokemonSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 宝可梦形态同步运行器
 * 用于从外部API同步宝可梦的特殊形态数据
 * 只在指定的"sync-form"配置文件下运行
 */
@Profile("sync-form")
@Component
@Slf4j
public class FormSyncRunner implements CommandLineRunner {
    
    /**
     * PokeAPI物种基础URL
     */
    private static final String POKEAPI_SPECIES_URL = "https://pokeapi.co/api/v2/pokemon-species/";
    
    /**
     * 宝可梦总数
     */
    private static final int MAX_POKEMON_ID = 1025;

    private final PokemonSyncService pokemonService;
    private final RestTemplate restTemplate;

    /**
     * 构造函数
     *
     * @param pokemonService 宝可梦同步服务
     * @param restTemplate REST请求模板
     */
    public FormSyncRunner(PokemonSyncService pokemonService, RestTemplate restTemplate) {
        this.pokemonService = pokemonService;
        this.restTemplate = restTemplate;
    }

    /**
     * 运行形态同步任务
     * 
     * @param args 命令行参数
     * @throws Exception 执行过程中可能抛出的异常
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("【特殊形态同步任务】开始...");

        for (int i = 1; i <= MAX_POKEMON_ID; i++) {
            syncPokemonForms(i);
        }

        log.info("【特殊形态同步任务】完成！");
    }
    
    /**
     * 同步特定宝可梦的所有特殊形态
     *
     * @param speciesId 宝可梦种族ID
     */
    private void syncPokemonForms(int speciesId) {
        try {
            // 获取物种详情
            String speciesUrl = POKEAPI_SPECIES_URL + speciesId;
            SpeciesDetailApiDto speciesDetailDto = restTemplate.getForObject(speciesUrl, SpeciesDetailApiDto.class);

            if (speciesDetailDto == null || speciesDetailDto.getVarieties() == null) {
                return;
            }

            // 获取物种基础数据，以便传递给形态同步方法
            PokemonSpeciesApiDto speciesDataDto = restTemplate.getForObject(speciesUrl, PokemonSpeciesApiDto.class);

            // 遍历所有非默认形态
            for (SpeciesDetailApiDto.Variety variety : speciesDetailDto.getVarieties()) {
                if (!variety.isDefault()) {
                    // 从URL中提取形态ID
                    int formId = extractFormIdFromUrl(variety.getPokemon().get("url"));
                    
                    // 如果数据库中不存在该形态，则进行同步
                    if (pokemonService.getById(formId) == null) {
                        log.info("发现新形态，物种ID: {}, 形态ID: {}, 准备同步...", speciesId, formId);
                        pokemonService.syncPokemonForm(formId, speciesDataDto);
                    }
                }
            }
        } catch (Exception e) {
            log.error("同步物种 #{} 的特殊形态时出错", speciesId, e);
        }
    }
    
    /**
     * 从URL中提取宝可梦形态ID
     *
     * @param url 宝可梦API URL
     * @return 形态ID
     */
    private int extractFormIdFromUrl(String url) {
        String[] urlParts = url.split("/");
        return Integer.parseInt(urlParts[urlParts.length - 1]);
    }
}