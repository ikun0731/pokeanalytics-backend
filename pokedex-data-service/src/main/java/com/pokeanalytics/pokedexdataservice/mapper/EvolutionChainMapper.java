package com.pokeanalytics.pokedexdataservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pokeanalytics.pokedexdataservice.entity.EvolutionChain;

/**
 * 进化链数据访问接口
 * 提供宝可梦进化链相关数据的数据库操作方法
 */
public interface EvolutionChainMapper extends BaseMapper<EvolutionChain> {
    // 继承了BaseMapper的所有基础CRUD方法
}