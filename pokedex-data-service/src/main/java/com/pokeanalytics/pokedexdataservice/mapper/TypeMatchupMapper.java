package com.pokeanalytics.pokedexdataservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pokeanalytics.pokedexdataservice.entity.TypeMatchup;

/**
 * 属性相克数据访问接口
 * 提供宝可梦属性相克关系的数据库操作方法
 */
public interface TypeMatchupMapper extends BaseMapper<TypeMatchup> {
    // 继承了BaseMapper的所有基础CRUD方法
}
