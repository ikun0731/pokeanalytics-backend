package com.pokeanalytics.pokedexdataservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokedexdataservice.entity.TypeMatchup;
import com.pokeanalytics.pokedexdataservice.mapper.TypeMatchupMapper;
import com.pokeanalytics.pokedexdataservice.service.TypeMatchupService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 宝可梦属性克制关系服务实现类
 */
@Service
public class TypeMatchupServiceImpl extends ServiceImpl<TypeMatchupMapper, TypeMatchup> implements TypeMatchupService {
    
    /**
     * 获取所有属性克制关系列表
     *
     * @return 包含所有属性克制关系的列表
     */
    @Override
    public List<TypeMatchup> getAllMatchups() {
        return this.list();
    }
}
