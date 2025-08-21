package com.pokeanalytics.pokedexdataservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pokeanalytics.pokedexdataservice.entity.Item;
import org.apache.ibatis.annotations.Param;

/**
 * 道具数据访问接口
 * 提供道具相关数据的数据库操作方法
 */
public interface ItemMapper extends BaseMapper<Item> {

    /**
     * 分页查询道具列表，支持按关键字搜索
     *
     * @param page 分页参数
     * @param keyword 搜索关键字（可选）
     * @return 分页后的道具实体列表
     */
    Page<Item> findItemList(Page<Item> page, @Param("keyword") String keyword);

}