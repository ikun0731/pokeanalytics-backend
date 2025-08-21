package com.pokeanalytics.pokedexdataservice.sync;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pokeanalytics.pokedexdataservice.dto.sync.PokemonSpeciesApiDto;
import com.pokeanalytics.pokedexdataservice.entity.Pokemon;

/**
 * 宝可梦数据同步服务接口
 * 提供从外部API同步宝可梦数据的功能
 */
public interface PokemonSyncService extends IService<Pokemon> {

    /**
     * 同步所有宝可梦数据
     * 从外部API获取并同步所有宝可梦的基础数据、特性、技能等信息
     */
    void syncPokemonData();

    /**
     * 同步单个宝可梦数据
     *
     * @param pokemonId 要同步的宝可梦ID
     */
    void syncSinglePokemon(int pokemonId);
    
    /**
     * 同步宝可梦特殊形态数据
     *
     * @param formId 形态ID
     * @param speciesDto 物种数据DTO
     */
    void syncPokemonForm(int formId, PokemonSpeciesApiDto speciesDto);
}