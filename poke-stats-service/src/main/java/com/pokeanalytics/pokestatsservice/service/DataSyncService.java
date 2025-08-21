package com.pokeanalytics.pokestatsservice.service;

/**
 * 基础数据同步服务接口
 * 提供从宝可梦数据服务同步基础数据的功能
 */
public interface DataSyncService {
    /**
     * 同步宝可梦基础信息
     * 从pokedex-data-service同步宝可梦基础数据到本地数据库
     */
    void syncPokemonInfo();
    
    /**
     * 同步道具基础信息
     * 从pokedex-data-service同步道具基础数据到本地数据库
     */
    void syncItemInfo();
    
    /**
     * 同步技能基础信息
     * 从pokedex-data-service同步技能基础数据到本地数据库
     */
    void syncMoveInfo();
    
    /**
     * 同步特性基础信息
     * 从pokedex-data-service同步特性基础数据到本地数据库
     */
    void syncAbilityInfo();
}