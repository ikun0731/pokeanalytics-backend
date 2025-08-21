package com.pokeanalytics.pokedexdataservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pokeanalytics.pokedexdataservice.entity.PokemonAbility;

/**
 * 宝可梦特性关联数据访问接口
 * 提供宝可梦与特性关联关系的数据库操作方法
 */
public interface PokemonAbilityMapper extends BaseMapper<PokemonAbility> {
    // 继承了BaseMapper的所有基础CRUD方法
}