package com.pokeanalytics.pokedexdataservice.controller;

import com.pokeanalytics.pokedexdataservice.common.ResultVO;
import com.pokeanalytics.pokedexdataservice.dto.response.ItemListItemDto;
import com.pokeanalytics.pokedexdataservice.dto.response.PageResultDto;
import com.pokeanalytics.pokedexdataservice.entity.Item;
import com.pokeanalytics.pokedexdataservice.service.ItemApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 道具数据控制器
 * 处理与宝可梦道具相关的所有HTTP请求
 */
@RestController
@RequestMapping("/items")
@Tag(name = "道具数据接口")
public class ItemController {

    private final ItemApiService itemApiService;

    /**
     * 构造函数
     *
     * @param itemApiService 道具服务接口
     */
    public ItemController(ItemApiService itemApiService) {
        this.itemApiService = itemApiService;
    }

    /**
     * 分页获取道具列表
     *
     * @param pageNum 页码，从1开始
     * @param pageSize 每页数量
     * @param keyword 搜索关键字，可选参数
     * @return 包含道具列表的分页结果
     */
    @Operation(summary = "分页获取道具列表", description = "支持按名称关键字进行模糊搜索。")
    @GetMapping
    public ResultVO<PageResultDto<ItemListItemDto>> getItemList(
            @Parameter(description = "页码，从1开始", example = "1") 
            @RequestParam(defaultValue = "1") long pageNum,
            @Parameter(description = "每页数量", example = "20") 
            @RequestParam(defaultValue = "20") long pageSize,
            @Parameter(description = "搜索关键字(可选)") 
            @RequestParam(required = false) String keyword
    ) {
        PageResultDto<ItemListItemDto> itemList = itemApiService.getItemList(pageNum, pageSize, keyword);
        return ResultVO.success(itemList);
    }
    
    /**
     * 获取道具详情
     *
     * @param idOrName 道具的ID、中文名或英文名
     * @return 道具详细信息
     */
    @Operation(summary = "获取道具详情", description = "根据道具的ID、中文名或英文名获取其详细信息。")
    @GetMapping("/{idOrName}")
    public ResultVO<Item> getItemDetail(
            @Parameter(description = "道具的ID、中文名或英文名", required = true, example = "大师球")
            @PathVariable String idOrName
    ) {
        Item item = itemApiService.getItemDetail(idOrName);
        if (item != null) {
            return ResultVO.success(item);
        } else {
            return ResultVO.fail(404, "道具未找到: " + idOrName);
        }
    }
    
    /**
     * 根据名称列表批量获取道具信息
     *
     * @param names 道具名称列表
     * @return 道具信息列表
     */
    @PostMapping("/batch/by-name")
    @Operation(summary = "根据名称列表批量获取道具信息")
    public ResultVO<List<ItemListItemDto>> getItemsByNames(@RequestBody List<String> names) {
        return ResultVO.success(itemApiService.findItemsByNames(names));
    }
}