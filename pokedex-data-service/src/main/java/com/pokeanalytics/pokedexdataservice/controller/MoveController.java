package com.pokeanalytics.pokedexdataservice.controller;

import com.pokeanalytics.pokedexdataservice.common.ResultVO;
import com.pokeanalytics.pokedexdataservice.dto.response.MoveDetailDto;
import com.pokeanalytics.pokedexdataservice.dto.response.MoveListItemDto;
import com.pokeanalytics.pokedexdataservice.dto.response.PageResultDto;
import com.pokeanalytics.pokedexdataservice.service.MoveApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 宝可梦技能控制器
 * 处理与宝可梦技能相关的所有HTTP请求
 */
@RestController
@RequestMapping("/moves")
@Tag(name = "技能数据接口")
public class MoveController {
    private final MoveApiService moveApiService;

    /**
     * 构造函数
     *
     * @param moveApiService 技能服务接口
     */
    public MoveController(MoveApiService moveApiService) {
        this.moveApiService = moveApiService;
    }

    /**
     * 分页获取技能列表
     *
     * @param pageNum 页码，从1开始
     * @param pageSize 每页数量
     * @param keyword 搜索关键字，可选参数
     * @return 包含技能列表的分页结果
     */
    @Operation(summary = "分页获取技能列表", description = "支持按名称关键字进行模糊搜索。")
    @GetMapping
    public ResultVO<PageResultDto<MoveListItemDto>> getMoveList(
            @Parameter(description = "页码，从1开始", example = "1") 
            @RequestParam(defaultValue = "1") long pageNum,
            @Parameter(description = "每页数量", example = "20") 
            @RequestParam(defaultValue = "20") long pageSize,
            @Parameter(description = "搜索关键字(可选)") 
            @RequestParam(required = false) String keyword
    ) {
        PageResultDto<MoveListItemDto> moveList = moveApiService.getMoveList(pageNum, pageSize, keyword);
        return ResultVO.success(moveList);
    }
    
    /**
     * 获取技能详情
     *
     * @param idOrName 技能的ID或中文名
     * @return 技能详细信息，包括按学习方式分类的宝可梦列表
     */
    @Operation(summary = "获取技能详情", description = "返回技能的详细信息，以及按学习方式分类的宝可梦列表。")
    @GetMapping("/{idOrName}")
    public ResultVO<MoveDetailDto> getMoveDetail(
            @Parameter(description = "技能的ID或中文名", required = true, example = "飞叶快刀")
            @PathVariable String idOrName
    ) {
        MoveDetailDto moveDetail = moveApiService.getMoveDetail(idOrName);
        if (moveDetail != null) {
            return ResultVO.success(moveDetail);
        } else {
            return ResultVO.fail(404, "技能未找到: " + idOrName);
        }
    }
    
    /**
     * 根据名称列表批量获取技能信息
     *
     * @param names 技能名称列表
     * @return 技能信息列表
     */
    @PostMapping("/batch/by-name")
    @Operation(summary = "根据名称列表批量获取技能信息")
    public ResultVO<List<MoveListItemDto>> getMovesByNames(@RequestBody List<String> names) {
        return ResultVO.success(moveApiService.findMovesByNames(names));
    }
}
