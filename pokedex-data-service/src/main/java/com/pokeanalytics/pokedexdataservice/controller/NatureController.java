package com.pokeanalytics.pokedexdataservice.controller;

import com.pokeanalytics.pokedexdataservice.common.ResultVO;
import com.pokeanalytics.pokedexdataservice.entity.Nature;
import com.pokeanalytics.pokedexdataservice.service.NatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 宝可梦性格数据控制器
 * 处理与宝可梦性格相关的HTTP请求
 */
@RestController
@RequestMapping("/natures")
@Tag(name = "性格数据接口")
public class NatureController {

    private final NatureService natureService;

    /**
     * 构造函数
     *
     * @param natureService 性格服务接口
     */
    public NatureController(NatureService natureService) {
        this.natureService = natureService;
    }

    /**
     * 获取所有性格列表
     *
     * @return 包含所有性格信息的列表
     */
    @GetMapping
    @Operation(summary = "获取所有性格列表")
    @Cacheable("allNatures") // 性格数据是固定的，适合添加缓存
    public ResultVO<List<Nature>> getAllNatures() {
        return ResultVO.success(natureService.getAllNatures());
    }
}