package com.pokeanalytics.pokedexdataservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pokeanalytics.pokedexdataservice.dto.response.ItemListItemDto;
import com.pokeanalytics.pokedexdataservice.dto.response.PageResultDto;
import com.pokeanalytics.pokedexdataservice.entity.Item;

import java.util.List;

/**
 * 宝可梦道具服务接口
 * 提供道具相关的业务逻辑处理
 */
public interface ItemApiService extends IService<Item> {

    /**
     * 分页查询道具列表
     *
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键字 (可选)
     * @return 封装好的分页结果DTO
     */
    PageResultDto<ItemListItemDto> getItemList(long pageNum, long pageSize, String keyword);
    
    /**
     * 获取道具详细信息
     *
     * @param idOrName 道具的ID、中文名称或英文名称
     * @return 道具详细信息
     */
    Item getItemDetail(String idOrName);
    
    /**
     * 根据名称列表批量获取道具信息
     *
     * @param names 道具名称列表
     * @return 道具信息列表
     */
    List<ItemListItemDto> findItemsByNames(List<String> names);
}