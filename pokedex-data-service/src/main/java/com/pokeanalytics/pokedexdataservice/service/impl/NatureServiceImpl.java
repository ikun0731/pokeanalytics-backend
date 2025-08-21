package com.pokeanalytics.pokedexdataservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokedexdataservice.entity.Nature;
import com.pokeanalytics.pokedexdataservice.mapper.NatureMapper;
import com.pokeanalytics.pokedexdataservice.service.NatureService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 宝可梦性格服务实现类
 */
@Service
public class NatureServiceImpl extends ServiceImpl<NatureMapper, Nature> implements NatureService {
    
    private final NatureMapper natureMapper;
    
    /**
     * 构造函数
     *
     * @param natureMapper 性格数据访问对象
     */
    public NatureServiceImpl(NatureMapper natureMapper) {
        this.natureMapper = natureMapper;
    }
    
    /**
     * 获取所有性格列表
     *
     * @return 包含所有性格信息的列表
     */
    @Override
    public List<Nature> getAllNatures() {
        return natureMapper.selectList(null);
    }
}
