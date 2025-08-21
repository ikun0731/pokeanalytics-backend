package com.pokeanalytics.userteamservice.dto.request;

import lombok.Data;
import java.util.List;

/**
 * AI分析请求数据传输对象
 * 包含对宝可梦配置进行AI分析所需的宝可梦名称和配置信息
 */
@Data
public class AiAnalyzeRequestDto {
    /** 宝可梦中文名称 */
    private String pokemonName;
    
    /** 特性英文名称 */
    private String ability;
    
    /** 道具英文名称 */
    private String item;
    
    /** 技能英文名称列表 */
    private List<String> moves;
}