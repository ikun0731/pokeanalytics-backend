package com.pokeanalytics.pokedexdataservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pokeanalytics.pokedexdataservice.dto.response.PageResultDto;
import com.pokeanalytics.pokedexdataservice.dto.response.PokemonDetailResponseDto;
import com.pokeanalytics.pokedexdataservice.dto.response.PokemonListItemDto;
import com.pokeanalytics.pokedexdataservice.dto.response.PokemonMoveDto;
import com.pokeanalytics.pokedexdataservice.entity.Pokemon;

import java.util.List;
import java.util.Map;

/**
 * 宝可梦数据服务接口
 * 提供宝可梦相关的业务逻辑处理
 */
public interface PokemonApiService extends IService<Pokemon> {

    /**
     * 根据ID、中文名或英文名获取宝可梦详细信息
     *
     * @param idOrName 宝可梦的ID、中文名或英文名
     * @return 组装好的宝可梦详情DTO，如果未找到则返回null
     */
    PokemonDetailResponseDto getPokemonDetail(String idOrName);
    
    /**
     * 分页查询宝可梦列表，支持多维度筛选
     *
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键字（可选）
     * @param type 属性筛选（可选）
     * @param eggGroup 蛋组筛选（可选）
     * @return 封装好的分页结果DTO
     */
    PageResultDto<PokemonListItemDto> getPokemonList(long pageNum, long pageSize, String keyword, String type, String eggGroup);
    
    /**
     * 获取宝可梦可学习的技能列表
     *
     * @param idOrName 宝可梦的ID、中文名或英文名
     * @return 宝可梦可学习的技能列表
     */
    List<PokemonMoveDto> getPokemonMoves(String idOrName);
    
    /**
     * 根据英文名列表批量获取宝可梦基础信息
     *
     * @param names 宝可梦英文名列表
     * @return 原始名称到宝可梦实体的映射，不存在的名称将被忽略
     */
    Map<String, Pokemon> findPokemonByNames(List<String> names);
    
    /**
     * 获取所有蛋组列表
     *
     * @return 所有蛋组的中文名称列表
     */
    List<String> getAllEggGroups();
    
    /**
     * 获取所有属性列表
     *
     * @return 所有属性的中文名称列表
     */
    List<String> getAllTypes();
}