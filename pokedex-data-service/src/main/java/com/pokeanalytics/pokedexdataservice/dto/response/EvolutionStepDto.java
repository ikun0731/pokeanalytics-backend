package com.pokeanalytics.pokedexdataservice.dto.response;

import lombok.Data;
import java.util.List;

/**
 * 进化步骤DTO
 * 用于表示宝可梦进化树中的一个节点，可递归构建完整进化树
 */
@Data
public class EvolutionStepDto {
    /**
     * 宝可梦ID
     */
    private int pokemonId;
    
    /**
     * 宝可梦名称
     */
    private String pokemonName;
    
    /**
     * 进化条件描述
     * 如：等级30、使用火之石、白天等
     */
    private String condition;
    
    /**
     * 下一级进化分支列表
     * 用于构建多分支进化树结构
     */
    private List<EvolutionStepDto> evolvesTo;
    
    /**
     * 宝可梦像素图片URL
     * 注意：此处变量名首字母大写是为了与现有代码兼容，保持一致
     */
    private String SpritePixel;
}