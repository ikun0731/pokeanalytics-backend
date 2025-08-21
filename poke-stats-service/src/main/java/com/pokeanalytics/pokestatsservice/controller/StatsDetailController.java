package com.pokeanalytics.pokestatsservice.controller;

import com.pokeanalytics.pokestatsservice.common.ResultVO;
import com.pokeanalytics.pokestatsservice.dto.PokemonStatsDetailDto;
import com.pokeanalytics.pokestatsservice.service.StatsDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 宝可梦对战数据详情控制器
 * 提供宝可梦在特定对战格式和天梯分数线下的详细数据查询接口
 */
@RestController
@RequestMapping("/stats")
@Tag(name = "对战数据详情接口")
public class StatsDetailController {

    /**
     * 对战数据详情服务
     */
    private final StatsDetailService statsDetailService;

    /**
     * 构造函数，通过依赖注入初始化对战数据详情服务
     *
     * @param statsDetailService 对战数据详情服务
     */
    public StatsDetailController(StatsDetailService statsDetailService) {
        this.statsDetailService = statsDetailService;
    }

    /**
     * 获取宝可梦在特定对战格式和天梯分数线下的详细数据
     * 包括使用率、胜率、常见队友、常见技能、常见特性、常见道具等
     *
     * @param format 对战格式，如"gen9ou"（第9代常规单打）
     * @param cutoff 天梯分数线，如1500、1630、1760等
     * @param pokemonNameEn 宝可梦英文名，如"great-tusk"
     * @return 包含宝可梦详细对战数据的统一响应结果，如果未找到则返回404错误
     */
    @GetMapping("/{format}/{cutoff}/{pokemonNameEn}")
    @Operation(summary = "获取宝可梦在特定榜单的详细数据")
    public ResultVO<PokemonStatsDetailDto> getStatsDetail(
            @Parameter(description = "对战格式", example = "gen9ou") @PathVariable String format,
            @Parameter(description = "天梯分数线", example = "1500") @PathVariable int cutoff,
            @Parameter(description = "宝可梦英文名", example = "great-tusk") @PathVariable String pokemonNameEn
    ) {
        PokemonStatsDetailDto detail = statsDetailService.getStatsDetail(format, cutoff, pokemonNameEn);
        if (detail != null) {
            return ResultVO.success(detail);
        } else {
            return ResultVO.fail(404, "对战数据详情未找到");
        }
    }
}