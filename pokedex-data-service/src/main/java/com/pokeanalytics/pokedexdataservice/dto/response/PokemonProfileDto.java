package com.pokeanalytics.pokedexdataservice.dto.response;

import lombok.Data;
import java.util.List;

/**
 * 宝可梦详细资料数据传输对象
 * 包含宝可梦的各种生物学特性和游戏机制相关属性
 */
@Data
public class PokemonProfileDto {
    /**
     * 身高（米）
     */
    private double height;
    
    /**
     * 体重（千克）
     */
    private double weight;
    
    /**
     * 特性列表
     */
    private List<AbilityDto> abilities;
    
    /**
     * 蛋组列表
     */
    private List<String> eggGroups;
    
    /**
     * 性别比例
     */
    private String genderRatio;
    
    /**
     * 孵化步数
     */
    private int hatchSteps;
    
    /**
     * 捕获率
     */
    private int captureRate;
    
    /**
     * 基础经验值
     */
    private Integer baseExperience;
    
    /**
     * 努力值收益
     */
    private String evYield;
}