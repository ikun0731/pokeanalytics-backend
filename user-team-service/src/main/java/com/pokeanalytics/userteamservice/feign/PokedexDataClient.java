package com.pokeanalytics.userteamservice.feign;

import com.pokeanalytics.userteamservice.common.ResultVO;
import com.pokeanalytics.userteamservice.dto.feign.PokemonDetailFeignDto;
import com.pokeanalytics.userteamservice.dto.feign.TypeMatchupDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * 宝可梦图鉴数据服务Feign客户端
 * 用于从pokedex-data-service微服务获取宝可梦相关数据
 */
@FeignClient("pokedex-data-service")
public interface PokedexDataClient {

    /**
     * 根据英文名称批量获取宝可梦详细信息
     *
     * @param names 宝可梦英文名称列表
     * @return 以宝可梦名称为键、详细信息为值的映射表
     */
    @PostMapping("/pokemon/batch/by-name")
    ResultVO<Map<String, PokemonDetailFeignDto>> getPokemonListByNames(@RequestBody List<String> names);

    /**
     * 获取所有属性相克关系数据
     *
     * @return 属性相克关系列表，包含攻击属性、防御属性和相克倍率
     */
    @GetMapping("/types/matchups")
    ResultVO<List<TypeMatchupDto>> getAllTypeMatchups();
}