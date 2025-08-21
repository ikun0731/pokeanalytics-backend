package com.pokeanalytics.pokedexdataservice.runner;

import com.pokeanalytics.pokedexdataservice.sync.PokemonSyncService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 宝可梦数据同步启动器
 * 在应用启动时自动执行宝可梦数据同步任务
 */
@Profile("sync")
@Component
public class DataSyncRunner implements CommandLineRunner {

    private final PokemonSyncService pokemonService;

    /**
     * 构造函数
     *
     * @param pokemonService 宝可梦同步服务
     */
    public DataSyncRunner(PokemonSyncService pokemonService) {
        this.pokemonService = pokemonService;
    }

    /**
     * 应用启动后执行的同步方法
     *
     * @param args 命令行参数
     * @throws Exception 同步过程中可能发生的异常
     */
    @Override
    public void run(String... args) throws Exception {
        pokemonService.syncPokemonData();
    }
}