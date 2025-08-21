package com.pokeanalytics.pokestatsservice.feign;

import com.pokeanalytics.pokestatsservice.common.ResultVO;
import com.pokeanalytics.pokestatsservice.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 宝可梦数据服务Feign客户端
 * 用于调用pokedex-data-service微服务的接口
 */
@FeignClient("pokedex-data-service")
public interface PokedexDataClient {

    /**
     * 获取宝可梦列表
     *
     * @param pageNum 页码，从1开始
     * @param pageSize 每页数量
     * @return 分页的宝可梦列表数据
     */
    @GetMapping("/pokemon")
    ResultVO<PageResultDto<PokemonListItemDto>> getPokemonList(
            @RequestParam("pageNum") long pageNum,
            @RequestParam("pageSize") long pageSize
    );

    /**
     * 获取道具列表
     *
     * @param pageNum 页码，从1开始
     * @param pageSize 每页数量
     * @return 分页的道具列表数据
     */
    @GetMapping("/items")
    ResultVO<PageResultDto<ItemListItemDto>> getItemList(
            @RequestParam("pageNum") long pageNum,
            @RequestParam("pageSize") long pageSize
    );

    /**
     * 获取技能列表
     *
     * @param pageNum 页码，从1开始
     * @param pageSize 每页数量
     * @return 分页的技能列表数据
     */
    @GetMapping("/moves")
    ResultVO<PageResultDto<MoveListItemDto>> getMoveList(
            @RequestParam("pageNum") long pageNum,
            @RequestParam("pageSize") long pageSize
    );
    
    /**
     * 获取特性列表
     *
     * @param pageNum 页码，从1开始
     * @param pageSize 每页数量
     * @return 分页的特性列表数据
     */
    @GetMapping("/abilities")
    ResultVO<PageResultDto<Ability>> getAbilityList(
            @RequestParam("pageNum") long pageNum,
            @RequestParam("pageSize") long pageSize
    );
}