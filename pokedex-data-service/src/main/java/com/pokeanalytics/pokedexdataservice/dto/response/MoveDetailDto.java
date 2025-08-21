package com.pokeanalytics.pokedexdataservice.dto.response;

import com.pokeanalytics.pokedexdataservice.entity.Move;
import lombok.Data;

/**
 * 技能详情数据传输对象
 * 包含技能信息和能学会该技能的宝可梦列表
 */
@Data
public class MoveDetailDto {
    /**
     * 技能ID
     */
    private Integer moveId;
    
    /**
     * 技能中文名称
     */
    private String nameCn;
    
    /**
     * 技能属性中文
     */
    private String typeCn;
    
    /**
     * 技能伤害类别中文
     */
    private String damageClassCn;
    
    /**
     * 技能威力
     */
    private Integer power;
    
    /**
     * 技能命中率
     */
    private Integer accuracy;
    
    /**
     * 技能PP值
     */
    private Integer pp;
    
    /**
     * 技能风味文本描述
     */
    private String flavorText;

    /**
     * 能学会该技能的宝可梦列表（按学习方式分类）
     */
    private LearnedByPokemonDto learnedBy;

    /**
     * 工厂方法，用于从Move实体创建DTO对象
     *
     * @param move 技能实体对象
     * @return 技能详情DTO对象
     */
    public static MoveDetailDto fromEntity(Move move) {
        if (move == null) return null;
        MoveDetailDto dto = new MoveDetailDto();
        dto.setMoveId(move.getId());
        dto.setNameCn(move.getNameCn());
        dto.setTypeCn(move.getTypeCn());
        dto.setDamageClassCn(move.getDamageClassCn());
        dto.setPower(move.getPower());
        dto.setAccuracy(move.getAccuracy());
        dto.setPp(move.getPp());
        dto.setFlavorText(move.getFlavorText());
        return dto;
    }
}