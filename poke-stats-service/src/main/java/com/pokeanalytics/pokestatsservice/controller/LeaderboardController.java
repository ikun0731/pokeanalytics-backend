package com.pokeanalytics.pokestatsservice.controller;

import com.pokeanalytics.pokestatsservice.common.ResultVO;
import com.pokeanalytics.pokestatsservice.dto.LeaderboardItemDto;
import com.pokeanalytics.pokestatsservice.dto.PageResultDto;
import com.pokeanalytics.pokestatsservice.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 宝可梦排行榜控制器
 * 提供宝可梦使用率排行榜相关的API接口
 */
@RestController
@RequestMapping("/leaderboards")
@Tag(name = "排行榜接口")
public class LeaderboardController {

    /**
     * 排行榜服务
     */
    private final LeaderboardService leaderboardService;

    /**
     * 构造函数，通过依赖注入初始化排行榜服务
     *
     * @param leaderboardService 排行榜服务
     */
    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    /**
     * 获取宝可梦使用率排行榜
     * 根据对战格式和天梯分数线，分页返回宝可梦使用率排行数据
     *
     * @param format 对战格式，如"gen9ou"（第9代常规单打）、"gen9ubers"（第9代无限制单打）等
     * @param cutoff 天梯分数线，如1500、1630、1760等，表示统计该分数以上的对战数据
     * @param pageNum 页码，从1开始
     * @param pageSize 每页数量
     * @return 包含分页排行榜数据的统一响应结果
     */
    @Operation(summary = "获取对战数据排行榜", description = "根据对战格式和天梯分数线，返回宝可梦使用率排行榜。")
    @GetMapping("/{format}/{cutoff}")
    public ResultVO<PageResultDto<LeaderboardItemDto>> getLeaderboard(
            @Parameter(description = "对战格式", example = "gen9ou") @PathVariable String format,
            @Parameter(description = "天梯分数线", example = "1500") @PathVariable int cutoff,
            @Parameter(description = "页码，从1开始", example = "1") @RequestParam(defaultValue = "1") long pageNum,
            @Parameter(description = "每页数量", example = "20") @RequestParam(defaultValue = "20") long pageSize
    ) {
        PageResultDto<LeaderboardItemDto> leaderboard = leaderboardService.getLeaderboard(format, cutoff, pageNum, pageSize);
        return ResultVO.success(leaderboard);
    }
}