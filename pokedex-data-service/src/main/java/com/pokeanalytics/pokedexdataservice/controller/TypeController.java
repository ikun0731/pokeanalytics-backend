package com.pokeanalytics.pokedexdataservice.controller;

import com.pokeanalytics.pokedexdataservice.common.ResultVO;
import com.pokeanalytics.pokedexdataservice.entity.TypeMatchup;
import com.pokeanalytics.pokedexdataservice.service.TypeMatchupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 宝可梦属性数据控制器
 * 处理与宝可梦属性相关的HTTP请求
 */
@RestController
@RequestMapping("/types")
@Tag(name = "属性数据接口")
public class TypeController {
    
    private final TypeMatchupService typeMatchupService;
    
    /**
     * 构造函数
     *
     * @param typeMatchupService 属性克制关系服务接口
     */
    public TypeController(TypeMatchupService typeMatchupService) {
        this.typeMatchupService = typeMatchupService;
    }
    
    /**
     * 获取全量属性克制关系表
     *
     * @return 包含所有属性克制关系的列表
     */
    @GetMapping("/matchups")
    @Operation(summary = "获取全量属性克制关系表")
    public ResultVO<List<TypeMatchup>> getAllMatchups() {
        return ResultVO.success(typeMatchupService.getAllMatchups());
    }
}