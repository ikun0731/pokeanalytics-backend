package com.pokeanalytics.pokedexdataservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pokeanalytics.pokedexdataservice.entity.PokemonMove;

/**
 * 宝可梦技能关联数据访问接口
 * 提供宝可梦与技能关联关系的数据库操作方法
 */
public interface PokemonMoveMapper extends BaseMapper<PokemonMove> {
    // 继承了BaseMapper的所有基础CRUD方法
}