package com.pokeanalytics.pokestatsservice.service;
import com.pokeanalytics.pokestatsservice.dto.PokemonStatsDetailDto;

/**
 * 宝可梦对战数据详情服务接口
 * 提供获取宝可梦在特定对战格式和天梯分数线下的详细数据功能
 */
public interface StatsDetailService {
    
    /**
     * 获取宝可梦在特定对战格式和天梯分数线下的详细数据
     *
     * @param format 对战格式，如"gen9ou"（第9代常规单打）
     * @param cutoff 天梯分数线，如1500、1630、1760等
     * @param pokemonNameEn 宝可梦英文名，如"great-tusk"
     * @return 宝可梦的详细对战数据，如果未找到则返回null
     */
    PokemonStatsDetailDto getStatsDetail(String format, int cutoff, String pokemonNameEn);
}