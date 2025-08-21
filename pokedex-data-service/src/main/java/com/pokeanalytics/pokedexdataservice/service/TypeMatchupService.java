package com.pokeanalytics.pokedexdataservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pokeanalytics.pokedexdataservice.entity.TypeMatchup;

import java.util.List;

/**
 * 宝可梦属性克制关系服务接口
 * 提供属性克制关系相关的业务逻辑处理
 */
public interface TypeMatchupService extends IService<TypeMatchup> {
    
    /**
     * 获取所有属性克制关系列表
     *
     * @return 包含所有属性克制关系的列表
     */
    List<TypeMatchup> getAllMatchups();
}
