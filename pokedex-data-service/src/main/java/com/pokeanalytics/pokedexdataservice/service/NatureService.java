package com.pokeanalytics.pokedexdataservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pokeanalytics.pokedexdataservice.entity.Nature;

import java.util.List;

/**
 * 宝可梦性格服务接口
 * 提供性格相关的业务逻辑处理
 */
public interface NatureService extends IService<Nature> {
    
    /**
     * 获取所有性格列表
     *
     * @return 包含所有性格信息的列表
     */
    List<Nature> getAllNatures();
}
