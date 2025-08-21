package com.pokeanalytics.pokedexdataservice.controller;

import com.pokeanalytics.pokedexdataservice.common.ResultVO;
import com.pokeanalytics.pokedexdataservice.dto.response.PageResultDto;
import com.pokeanalytics.pokedexdataservice.dto.response.PokemonDetailResponseDto;
import com.pokeanalytics.pokedexdataservice.dto.response.PokemonListItemDto;
import com.pokeanalytics.pokedexdataservice.dto.response.PokemonMoveDto;
import com.pokeanalytics.pokedexdataservice.entity.Pokemon;
import com.pokeanalytics.pokedexdataservice.service.PokemonApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 宝可梦数据控制器
 * 处理与宝可梦相关的所有HTTP请求
 */
@RestController
@RequestMapping("/pokemon")
@Tag(name = "宝可梦数据接口", description = "提供宝可梦的详细信息查询")
public class PokemonController {

    private final PokemonApiService pokemonApiService;

    /**
     * 构造函数
     *
     * @param pokemonApiService 宝可梦服务接口
     */
    public PokemonController(PokemonApiService pokemonApiService) {
        this.pokemonApiService = pokemonApiService;
    }

    /**
     * 通过ID获取宝可梦详情
     *
     * @param id 宝可梦的全国图鉴ID
     * @return 宝可梦详细信息
     */
    @Operation(summary = "通过ID获取宝可梦详情", description = "根据宝可梦的全国图鉴ID精确查找一个资源。")
    @GetMapping("/{id}")
    public ResultVO<?> getPokemonDetailById(
            @Parameter(description = "宝可梦的全国图鉴ID", required = true, example = "25")
            @PathVariable int id) {
        PokemonDetailResponseDto pokemonDetail = pokemonApiService.getPokemonDetail(String.valueOf(id));
        if (pokemonDetail != null) {
            return ResultVO.success(pokemonDetail);
        } else {
            return ResultVO.fail(404, "宝可梦未找到: " + id);
        }
    }

    /**
     * 通过名称搜索宝可梦详情
     *
     * @param name 宝可梦的中文名或英文名
     * @return 宝可梦详细信息
     */
    @Operation(summary = "通过名称搜索宝可梦详情", description = "根据宝可梦的中文名或英文名进行搜索。")
    @GetMapping("/search")
    public ResultVO<?> searchPokemonDetailByName(
            @Parameter(description = "宝可梦的中文名或英文名", required = true, example = "皮卡丘")
            @RequestParam("name") String name) {
        PokemonDetailResponseDto pokemonDetail = pokemonApiService.getPokemonDetail(name);
        if (pokemonDetail != null) {
            return ResultVO.success(pokemonDetail);
        } else {
            return ResultVO.fail(404, "宝可梦未找到: " + name);
        }
    }
    
    /**
     * 分页获取宝可梦列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param keyword 名称关键字，可选参数
     * @param type 属性(中文)，可选参数
     * @param eggGroup 蛋组(英文)，可选参数
     * @return 包含宝可梦列表的分页结果
     */
    @Operation(summary = "分页获取宝可梦列表", description = "支持按名称、属性、蛋组进行多维度筛选。")
    @GetMapping
    public ResultVO<PageResultDto<PokemonListItemDto>> getPokemonList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") long pageSize,
            @Parameter(description = "名称关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "属性(中文)") @RequestParam(required = false) String type,
            @Parameter(description = "蛋组(英文)") @RequestParam(required = false) String eggGroup
    ) {
        PageResultDto<PokemonListItemDto> pokemonList = pokemonApiService.getPokemonList(pageNum, pageSize, keyword, type, eggGroup);
        return ResultVO.success(pokemonList);
    }
    
    /**
     * 获取宝可梦的技能列表
     *
     * @param idOrName 宝可梦的ID、中文名或英文名
     * @return 宝可梦的技能列表
     */
    @Operation(summary = "获取宝可梦的技能列表", description = "根据宝可梦的ID或名称，返回其完整的可学习技能池。")
    @GetMapping("/{idOrName}/moves")
    public ResultVO<?> getPokemonMoves(
            @Parameter(description = "宝可梦的ID、中文名或英文名", required = true, example = "皮卡丘")
            @PathVariable String idOrName) {
        List<PokemonMoveDto> moves = pokemonApiService.getPokemonMoves(idOrName);
        return ResultVO.success(moves);
    }
    
    /**
     * 根据名称列表批量获取宝可梦信息
     *
     * @param names 宝可梦英文名列表
     * @return 原始名称到宝可梦信息的映射
     */
    @Operation(summary = "根据名称列表批量获取宝可梦信息", description = "提供一个包含宝可梦英文名的列表，返回一个从原始名称到宝可梦信息的映射。")
    @PostMapping("/batch/by-name")
    public ResultVO<Map<String, PokemonListItemDto>> getPokemonListByNames(@RequestBody List<String> names) {
        Map<String, Pokemon> pokemonMap = pokemonApiService.findPokemonByNames(names);

        Map<String, PokemonListItemDto> dtoMap = new HashMap<>();
        pokemonMap.forEach((originalName, pokemon) -> {
            if (pokemon != null) {
                PokemonListItemDto dto = new PokemonListItemDto();
                BeanUtils.copyProperties(pokemon, dto);
                
                // 设置中文属性列表
                List<String> types = new ArrayList<>();
                if (pokemon.getType1Cn() != null) types.add(pokemon.getType1Cn());
                if (pokemon.getType2Cn() != null) types.add(pokemon.getType2Cn());
                dto.setTypes(types);
                
                // 设置英文属性列表
                List<String> typesEn = new ArrayList<>();
                if (pokemon.getType1() != null) typesEn.add(pokemon.getType1());
                if (pokemon.getType2() != null) typesEn.add(pokemon.getType2());
                dto.setTypesEn(typesEn);
                
                dtoMap.put(originalName, dto);
            }
        });

        return ResultVO.success(dtoMap);
    }
    
    /**
     * 获取所有蛋组列表
     *
     * @return 蛋组列表
     */
    @GetMapping("/egg-groups")
    @Operation(summary = "获取所有蛋组列表")
    public ResultVO<List<String>> getAllEggGroups() {
        return ResultVO.success(pokemonApiService.getAllEggGroups());
    }

    /**
     * 获取所有属性列表
     *
     * @return 属性列表
     */
    @GetMapping("/types")
    @Operation(summary = "获取所有属性列表")
    public ResultVO<List<String>> getAllTypes() {
        return ResultVO.success(pokemonApiService.getAllTypes());
    }
}