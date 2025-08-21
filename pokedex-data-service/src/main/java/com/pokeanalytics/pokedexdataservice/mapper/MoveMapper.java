package com.pokeanalytics.pokedexdataservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pokeanalytics.pokedexdataservice.dto.projection.MoveLearnerProjection;
import com.pokeanalytics.pokedexdataservice.entity.Move;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 技能数据访问接口
 * 提供宝可梦技能相关数据的数据库操作方法
 */
public interface MoveMapper extends BaseMapper<Move> {
    
    /**
     * 分页查询技能列表，支持按关键字搜索
     *
     * @param page 分页参数
     * @param keyword 搜索关键字（可选）
     * @return 分页后的技能实体列表
     */
    Page<Move> findMoveList(Page<Move> page, @Param("keyword") String keyword);
    
    /**
     * 根据技能ID查询所有能学会该技能的宝可梦及其学习方式
     *
     * @param moveId 技能ID
     * @return 包含宝可梦和学习信息的投影数据列表
     */
    List<MoveLearnerProjection> findPokemonLearnersByMoveId(@Param("moveId") int moveId);
}