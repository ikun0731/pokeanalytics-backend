package com.pokeanalytics.pokedexdataservice.dto.response;

import lombok.Data;
import java.util.List;

/**
 * 宝可梦学习技能方式分类数据传输对象
 * 按不同学习方式对宝可梦列表进行分类
 */
@Data
public class LearnedByPokemonDto {
    /**
     * 通过升级学习的宝可梦列表
     */
    private List<PokemonLearnerDto> byLevelUp;
    
    /**
     * 通过招式机学习的宝可梦列表
     */
    private List<PokemonLearnerDto> byMachine;
    
    /**
     * 通过技能教授学习的宝可梦列表
     */
    private List<PokemonLearnerDto> byTutor;
    
    /**
     * 通过蛋招式学习的宝可梦列表
     */
    private List<PokemonLearnerDto> byEgg;
}