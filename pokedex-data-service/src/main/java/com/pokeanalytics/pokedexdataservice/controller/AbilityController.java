package com.pokeanalytics.pokedexdataservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pokeanalytics.pokedexdataservice.common.ResultVO;
import com.pokeanalytics.pokedexdataservice.dto.response.AbilityDetailDto;
import com.pokeanalytics.pokedexdataservice.dto.response.PageResultDto;
import com.pokeanalytics.pokedexdataservice.entity.Ability;
import com.pokeanalytics.pokedexdataservice.service.AbilityApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 特性数据控制器
 * 处理与宝可梦特性相关的所有HTTP请求
 */
@RestController
@RequestMapping("/abilities")
@Tag(name = "特性数据接口")
public class AbilityController {

    private final AbilityApiService abilityService;

    /**
     * 构造函数
     * 
     * @param abilityService 特性服务接口
     */
    public AbilityController(AbilityApiService abilityService) {
        this.abilityService = abilityService;
    }

    /**
     * 分页获取特性列表
     * 
     * @param pageNum 当前页码，默认为1
     * @param pageSize 每页数据量，默认为50
     * @param keyword 搜索关键词，可选参数
     * @return 包含特性列表的分页结果
     */
    @GetMapping
    @Operation(summary = "分页获取特性列表", description = "支持按名称关键字进行模糊搜索。")
    public ResultVO<PageResultDto<Ability>> getAbilityList(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "50") long pageSize,
            @RequestParam(required = false) String keyword
    ) {
        Page<Ability> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Ability> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("name_cn", keyword).or().like("name_en", keyword);
        }
        queryWrapper.orderByAsc("id");
        Page<Ability> resultPage = abilityService.page(page, queryWrapper);
        
        PageResultDto<Ability> pageResult = new PageResultDto<>();
        pageResult.setTotal(resultPage.getTotal());
        pageResult.setTotalPages(resultPage.getPages());
        pageResult.setItems(resultPage.getRecords());
        return ResultVO.success(pageResult);
    }

    /**
     * 获取特性详情
     * 
     * @param idOrName 特性的ID、中文名或英文名
     * @return 特性详细信息，包括拥有该特性的宝可梦列表
     */
    @GetMapping("/{idOrName}")
    @Operation(summary = "获取特性详情", description = "根据特性的ID、中文名或英文名获取详细信息，以及拥有该特性的宝可梦列表。")
    public ResultVO<AbilityDetailDto> getAbilityDetail(@PathVariable String idOrName) {
        AbilityDetailDto abilityDetail = abilityService.getAbilityDetail(idOrName);
        return ResultVO.success(abilityDetail);
    }
    
    /**
     * 根据名称列表批量获取特性信息
     * 
     * @param names 特性名称列表
     * @return 特性信息列表
     */
    @PostMapping("/batch/by-name")
    @Operation(summary = "根据名称列表批量获取特性信息")
    public ResultVO<List<Ability>> getAbilitiesByNames(@RequestBody List<String> names) {
        List<Ability> abilities = abilityService.getAbilitiesByNames(names);
        return ResultVO.success(abilities);
    }
}