package com.pokeanalytics.pokedexdataservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pokeanalytics.pokedexdataservice.dto.response.AbilityDetailDto;
import com.pokeanalytics.pokedexdataservice.entity.Ability;

import java.util.List;

/**
 * 宝可梦特性服务接口
 * 提供特性相关的业务逻辑处理
 */
public interface AbilityApiService extends IService<Ability> {
    
    /**
     * 获取特性详细信息
     *
     * @param idOrName 特性的ID、中文名称或英文名称
     * @return 特性详细信息，包括拥有该特性的宝可梦列表
     */
    AbilityDetailDto getAbilityDetail(String idOrName);
    
    /**
     * 根据名称列表批量获取特性信息
     *
     * @param names 特性名称列表
     * @return 特性信息列表
     */
    List<Ability> getAbilitiesByNames(List<String> names);
}