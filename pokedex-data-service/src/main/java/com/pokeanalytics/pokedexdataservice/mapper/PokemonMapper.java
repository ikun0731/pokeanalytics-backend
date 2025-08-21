package com.pokeanalytics.pokedexdataservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pokeanalytics.pokedexdataservice.dto.response.AbilityDto;
import com.pokeanalytics.pokedexdataservice.dto.response.PokemonMoveDto;
import com.pokeanalytics.pokedexdataservice.entity.EvolutionChain;
import com.pokeanalytics.pokedexdataservice.entity.Pokemon;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 宝可梦数据访问接口
 * 提供宝可梦及其相关数据的数据库操作方法
 */
public interface PokemonMapper extends BaseMapper<Pokemon> {
    /**
     * 根据宝可梦ID查询其所有特性
     *
     * @param pokemonId 宝可梦的ID
     * @return 特性DTO列表
     */
    List<AbilityDto> findAbilitiesByPokemonId(@Param("pokemonId") int pokemonId);

    /**
     * 根据宝可梦ID查询其所属的进化链ID
     *
     * @param pokemonId 宝可梦ID
     * @return 进化链ID，可能为null
     */
    Integer findChainIdByPokemonId(@Param("pokemonId") int pokemonId);

    /**
     * 根据进化链ID查询所有进化步骤
     *
     * @param chainId 进化链ID
     * @return 进化链实体列表
     */
    List<EvolutionChain> findEvolutionStepsByChainId(@Param("chainId") int chainId);
    
    /**
     * 根据基础英文名称查找相关的宝可梦形态
     *
     * @param baseName 宝可梦的基础英文名（如"charizard"）
     * @return 相关的宝可梦实体列表
     */
    List<Pokemon> findRelatedFormsByBaseName(@Param("baseName") String baseName);

    /**
     * 分页查询宝可梦列表，支持多条件筛选
     *
     * @param page 分页参数
     * @param keyword 搜索关键字（可选）
     * @param type 属性筛选（可选）
     * @param eggGroup 蛋组筛选（可选）
     * @return 分页后的宝可梦实体列表
     */
    Page<Pokemon> findPokemonList(Page<Pokemon> page,
                                  @Param("keyword") String keyword,
                                  @Param("type") String type,
                                  @Param("eggGroup") String eggGroup);
    
    /**
     * 根据宝可梦ID查询其可学习的技能列表
     *
     * @param pokemonId 宝可梦的ID
     * @return 技能DTO列表
     */
    List<PokemonMoveDto> findMovesByPokemonId(@Param("pokemonId") int pokemonId);

    /**
     * 获取所有蛋组列表
     *
     * @return 蛋组列表
     */
    List<String> findAllEggGroups();
    
    /**
     * 获取所有属性列表
     *
     * @return 属性列表
     */
    List<String> findAllTypes();
    
    /**
     * 根据名称查找最匹配的宝可梦
     * 
     * @param nameEn 宝可梦的名称（英文或中文）
     * @return 匹配的宝可梦实体，如果未找到则返回null
     */
    Pokemon findBestMatchByName(String nameEn);
}