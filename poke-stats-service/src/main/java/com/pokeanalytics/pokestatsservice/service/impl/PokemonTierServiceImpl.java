package com.pokeanalytics.pokestatsservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokestatsservice.entity.PokemonTier;
import com.pokeanalytics.pokestatsservice.mapper.PokemonTierMapper;
import com.pokeanalytics.pokestatsservice.service.PokemonTierService;
import org.springframework.stereotype.Service;

/**
 * 宝可梦竞技分级服务实现类
 * 提供对宝可梦竞技分级数据的基础操作实现，基于MyBatis-Plus的ServiceImpl
 */
@Service
public class PokemonTierServiceImpl extends ServiceImpl<PokemonTierMapper, PokemonTier> implements PokemonTierService {
}