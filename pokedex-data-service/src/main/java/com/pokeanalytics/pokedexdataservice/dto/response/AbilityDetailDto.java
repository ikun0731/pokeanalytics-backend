package com.pokeanalytics.pokedexdataservice.dto.response;

import com.pokeanalytics.pokedexdataservice.entity.Ability;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 特性详情数据传输对象
 * 继承自Ability实体类，额外包含拥有该特性的宝可梦列表
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AbilityDetailDto extends Ability {
    /**
     * 拥有该特性的宝可梦列表
     */
    private List<PokemonLearnerDto> learnedBy;
}