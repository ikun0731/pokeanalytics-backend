package com.pokeanalytics.pokestatsservice.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pokeanalytics.pokestatsservice.entity.PokemonUsageStats;

/**
 * 宝可梦使用率统计服务接口
 * 提供对宝可梦使用率统计数据的基础操作，包括使用率、排名等信息
 */
public interface PokemonUsageStatsService extends IService<PokemonUsageStats> {}