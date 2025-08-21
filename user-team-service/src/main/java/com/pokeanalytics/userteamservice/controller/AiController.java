package com.pokeanalytics.userteamservice.controller;

import com.pokeanalytics.userteamservice.common.ResultVO;
import com.pokeanalytics.userteamservice.dto.request.AiAnalyzeRequestDto;
import com.pokeanalytics.userteamservice.dto.response.AiAnalyzeResponseDto;
import com.pokeanalytics.userteamservice.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users/ai")
@Tag(name = "AI 战术分析", description = "提供基于AI的宝可梦战术分析功能")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/analyze-pokemon")
    @Operation(
        summary = "获取宝可梦单体战术分析", 
        description = "根据提供的宝可梦名称和配置信息，使用AI分析并推荐适合的战术思路。" +
                "如果提供了特性、道具和技能，则对现有配置进行分析；" +
                "如果未提供配置信息，则推荐多种流行配置方案。"
    )
    public ResultVO<AiAnalyzeResponseDto> getAnalysis(@RequestBody AiAnalyzeRequestDto requestDto) {
        String analysisResult = aiService.getPokemonAnalysis(requestDto);
        return ResultVO.success(new AiAnalyzeResponseDto(analysisResult));
    }
}