package com.pokeanalytics.pokedexdataservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokedexdataservice.dto.response.AbilityDetailDto;
import com.pokeanalytics.pokedexdataservice.dto.response.PokemonLearnerDto;
import com.pokeanalytics.pokedexdataservice.entity.Ability;
import com.pokeanalytics.pokedexdataservice.mapper.AbilityMapper;
import com.pokeanalytics.pokedexdataservice.service.AbilityApiService;
import com.pokeanalytics.pokedexdataservice.util.TranslationUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 宝可梦特性服务实现类
 */
@Service
public class AbilityApiServiceImpl extends ServiceImpl<AbilityMapper, Ability> implements AbilityApiService {

    private final AbilityMapper abilityMapper;
    
    /**
     * 构造函数
     *
     * @param abilityMapper 特性数据访问对象
     */
    public AbilityApiServiceImpl(AbilityMapper abilityMapper) {
        this.abilityMapper = abilityMapper;
    }
    
    /**
     * 获取特性详细信息
     * 
     * @param idOrName 特性的ID、中文名称或英文名称
     * @return 特性详细信息，包括拥有该特性的宝可梦列表
     */
    @Override
    @Cacheable(value = "abilityDetail", key = "#idOrName")
    public AbilityDetailDto getAbilityDetail(String idOrName) {
        // 构建查询条件
        QueryWrapper<Ability> queryWrapper = new QueryWrapper<>();
        if (NumberUtils.isCreatable(idOrName)) {
            // 如果是数字，按ID查询
            queryWrapper.eq("id", Integer.parseInt(idOrName));
        } else {
            // 否则按中文名或英文名查询
            queryWrapper.eq("name_cn", idOrName.trim()).or().eq("name_en", idOrName.trim());
        }
        
        // 查询特性信息
        Ability ability = this.getOne(queryWrapper);
        if (ability == null) {
            return null;
        }

        // 创建并填充返回DTO
        AbilityDetailDto detailDto = new AbilityDetailDto();
        BeanUtils.copyProperties(ability, detailDto);

        // 查询拥有此特性的宝可梦
        List<PokemonLearnerDto> learners = this.baseMapper.findPokemonByAbilityId(ability.getId());

        // 翻译宝可梦世代信息
        learners.forEach(learner ->
                learner.setLastLearnGeneration(
                        TranslationUtils.translateVersion(learner.getLastLearnGeneration())
                )
        );

        // 设置宝可梦列表并返回
        detailDto.setLearnedBy(learners);
        return detailDto;
    }
    
    /**
     * 根据名称列表批量获取特性信息
     *
     * @param names 特性名称列表
     * @return 特性信息列表
     */
    @Override
    public List<Ability> getAbilitiesByNames(List<String> names) {
        if (names == null || names.isEmpty()) {
            return List.of(); // 返回一个不可变的空列表
        }
        return abilityMapper.selectBatchByNames(names);
    }
}