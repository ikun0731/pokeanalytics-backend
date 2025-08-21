package com.pokeanalytics.pokestatsservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pokeanalytics.pokestatsservice.entity.PokemonInfo;

/**
 * 宝可梦基础信息数据库操作接口
 * 提供对pokemon_info表的增删改查操作
 */
public interface PokemonInfoMapper extends BaseMapper<PokemonInfo> {
}