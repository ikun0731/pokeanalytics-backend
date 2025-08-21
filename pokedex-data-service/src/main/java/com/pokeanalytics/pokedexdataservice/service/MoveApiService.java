package com.pokeanalytics.pokedexdataservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pokeanalytics.pokedexdataservice.dto.response.MoveDetailDto;
import com.pokeanalytics.pokedexdataservice.dto.response.MoveListItemDto;
import com.pokeanalytics.pokedexdataservice.dto.response.PageResultDto;
import com.pokeanalytics.pokedexdataservice.entity.Move;

import java.util.List;

/**
 * 宝可梦技能服务接口
 * 提供技能相关的业务逻辑处理
 */
public interface MoveApiService extends IService<Move> {
    
    /**
     * 分页查询技能列表
     *
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键字 (可选)
     * @return 封装好的分页结果DTO
     */
    PageResultDto<MoveListItemDto> getMoveList(long pageNum, long pageSize, String keyword);
    
    /**
     * 获取技能详情，包含能学会该技能的宝可梦列表
     *
     * @param idOrName 技能的ID或名称
     * @return 技能详情DTO
     */
    MoveDetailDto getMoveDetail(String idOrName);
    
    /**
     * 根据名称列表批量获取技能信息
     *
     * @param names 技能名称列表
     * @return 技能信息列表
     */
    List<MoveListItemDto> findMovesByNames(List<String> names);
}