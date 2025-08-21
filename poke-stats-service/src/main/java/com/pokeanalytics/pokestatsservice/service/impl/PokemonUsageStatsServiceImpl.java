package com.pokeanalytics.pokestatsservice.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokestatsservice.entity.PokemonUsageStats;
import com.pokeanalytics.pokestatsservice.mapper.PokemonUsageStatsMapper;
import com.pokeanalytics.pokestatsservice.service.PokemonUsageStatsService;
import org.springframework.stereotype.Service;

/**
 * 宝可梦使用率统计服务实现类
 * 提供对宝可梦使用率统计数据的基础操作实现，基于MyBatis-Plus的ServiceImpl
 */
@Service
public class PokemonUsageStatsServiceImpl extends ServiceImpl<PokemonUsageStatsMapper, PokemonUsageStats> implements PokemonUsageStatsService {}