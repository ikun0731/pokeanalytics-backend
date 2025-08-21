package com.pokeanalytics.pokedexdataservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pokeanalytics.pokedexdataservice.dto.response.PokemonLearnerDto;
import com.pokeanalytics.pokedexdataservice.entity.Ability;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 宝可梦特性数据访问接口
 * 提供特性及其相关数据的数据库操作方法
 */
public interface AbilityMapper extends BaseMapper<Ability> {
    
    /**
     * 根据特性ID查找所有拥有该特性的宝可梦
     *
     * @param abilityId 特性ID
     * @return 拥有该特性的宝可梦列表
     */
    List<PokemonLearnerDto> findPokemonByAbilityId(@Param("abilityId") Integer abilityId);
    
    /**
     * 根据名称列表批量查询特性
     *
     * @param names 特性名称列表
     * @return 特性实体列表
     */
    List<Ability> selectBatchByNames(@Param("names") List<String> names);
}