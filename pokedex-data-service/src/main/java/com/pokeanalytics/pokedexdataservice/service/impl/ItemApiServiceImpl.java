package com.pokeanalytics.pokedexdataservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokedexdataservice.dto.response.ItemListItemDto;
import com.pokeanalytics.pokedexdataservice.dto.response.PageResultDto;
import com.pokeanalytics.pokedexdataservice.entity.Item;
import com.pokeanalytics.pokedexdataservice.mapper.ItemMapper;
import com.pokeanalytics.pokedexdataservice.service.ItemApiService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 宝可梦道具服务实现类
 */
@Service
public class ItemApiServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemApiService {

    private final ItemMapper itemMapper;

    /**
     * 构造函数
     *
     * @param itemMapper 道具数据访问对象
     */
    public ItemApiServiceImpl(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    /**
     * 分页查询道具列表
     *
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键字 (可选)
     * @return 封装好的分页结果DTO
     */
    @Override
    public PageResultDto<ItemListItemDto> getItemList(long pageNum, long pageSize, String keyword) {
        // 创建分页对象
        Page<Item> page = new Page<>(pageNum, pageSize);

        // 执行分页查询
        Page<Item> itemPage = itemMapper.findItemList(page, keyword);

        // 将实体对象转换为DTO对象
        List<ItemListItemDto> dtoList = itemPage.getRecords().stream().map(item -> {
            ItemListItemDto dto = new ItemListItemDto();
            dto.setId(item.getId());
            dto.setNameCn(item.getNameCn());
            dto.setNameEn(item.getNameEn());
            dto.setCategoryCn(item.getCategoryCn());
            dto.setSpriteUrl(item.getSpriteUrl());
            dto.setFlavorText(item.getFlavorText());
            return dto;
        }).collect(Collectors.toList());

        // 封装分页结果
        PageResultDto<ItemListItemDto> pageResult = new PageResultDto<>();
        pageResult.setTotal(itemPage.getTotal());
        pageResult.setTotalPages(itemPage.getPages());
        pageResult.setItems(dtoList);

        return pageResult;
    }
    
    /**
     * 获取道具详细信息
     *
     * @param idOrName 道具的ID、中文名称或英文名称
     * @return 道具详细信息
     */
    @Override
    @Cacheable(value = "itemDetail", key = "#idOrName")
    public Item getItemDetail(String idOrName) {
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        if (NumberUtils.isCreatable(idOrName)) {
            // 如果是数字，按ID查询
            queryWrapper.eq("id", Integer.parseInt(idOrName));
        } else {
            // 否则按中文名或英文名查询
            queryWrapper.eq("name_cn", idOrName.trim())
                    .or()
                    .eq("name_en", idOrName.trim());
        }
        return this.getOne(queryWrapper);
    }
    
    /**
     * 根据名称列表批量获取道具信息
     *
     * @param names 道具名称列表
     * @return 道具信息列表
     */
    @Override
    public List<ItemListItemDto> findItemsByNames(List<String> names) {
        if (names == null || names.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 根据英文名称列表查询道具
        List<Item> items = this.list(new QueryWrapper<Item>().in("name_en", names));

        // 转换为DTO对象
        return items.stream().map(item -> {
            ItemListItemDto dto = new ItemListItemDto();
            dto.setId(item.getId());
            dto.setNameCn(item.getNameCn());
            dto.setNameEn(item.getNameEn());
            dto.setSpriteUrl(item.getSpriteUrl());
            dto.setCategoryCn(item.getCategoryCn());
            dto.setFlavorText(item.getFlavorText());
            return dto;
        }).collect(Collectors.toList());
    }
}