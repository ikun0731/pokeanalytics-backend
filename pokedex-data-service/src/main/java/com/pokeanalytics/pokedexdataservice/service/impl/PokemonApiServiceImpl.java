package com.pokeanalytics.pokedexdataservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokedexdataservice.dto.response.*;
import com.pokeanalytics.pokedexdataservice.entity.EvolutionChain;
import com.pokeanalytics.pokedexdataservice.entity.Item;
import com.pokeanalytics.pokedexdataservice.entity.Pokemon;
import com.pokeanalytics.pokedexdataservice.mapper.ItemMapper;
import com.pokeanalytics.pokedexdataservice.mapper.PokemonMapper;
import com.pokeanalytics.pokedexdataservice.service.PokemonApiService;
import com.pokeanalytics.pokedexdataservice.util.TranslationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 宝可梦数据服务实现类
 * 提供宝可梦相关的业务逻辑实现
 */
@Slf4j
@Service
public class PokemonApiServiceImpl extends ServiceImpl<PokemonMapper, Pokemon> implements PokemonApiService {

    private final PokemonMapper pokemonMapper;
    private final ItemMapper itemMapper;
    
    /**
     * 构造函数
     *
     * @param pokemonMapper 宝可梦数据访问对象
     * @param itemMapper 道具数据访问对象
     */
    public PokemonApiServiceImpl(PokemonMapper pokemonMapper, ItemMapper itemMapper) {
        this.pokemonMapper = pokemonMapper;
        this.itemMapper = itemMapper;
    }
    /**
     * 获取宝可梦详细信息
     * 根据ID、中文名或英文名查找宝可梦并返回其详细信息
     *
     * @param idOrName 宝可梦的ID、中文名或英文名
     * @return 组装好的宝可梦详情DTO，如果未找到则返回null
     */
    @Cacheable(value = "pokemonDetail", key = "#idOrName.toLowerCase()")
    @Override
    public PokemonDetailResponseDto getPokemonDetail(String idOrName) {
        // 查找宝可梦基本信息
        Pokemon pokemon;

        if (NumberUtils.isCreatable(idOrName)) {
            // 如果是数字，按ID查询
            pokemon = this.getById(Integer.parseInt(idOrName));
        } else {
            // 否则按名称查询，使用更健壮的匹配方法
            String trimmedName = idOrName.trim().toLowerCase();
            pokemon = pokemonMapper.findBestMatchByName(trimmedName);
        }

        // 如果基础查询失败，直接返回null
        if (pokemon == null) {
            log.warn("未找到宝可梦: {}", idOrName);
            return null;
        }

        // 创建并填充返回DTO
        PokemonDetailResponseDto dto = PokemonDetailResponseDto.fromEntity(pokemon);

        // 添加特性信息
        enrichWithAbilities(dto, pokemon.getId());
        
        // 添加进化链信息
        enrichWithEvolutionChain(dto, pokemon.getId());
        
        // 添加其他形态信息
        enrichWithOtherForms(dto, pokemon);
        
        return dto;
    }
    
    /**
     * 为宝可梦DTO添加特性信息
     *
     * @param dto 宝可梦详情DTO
     * @param pokemonId 宝可梦ID
     */
    private void enrichWithAbilities(PokemonDetailResponseDto dto, Integer pokemonId) {
        List<AbilityDto> abilities = pokemonMapper.findAbilitiesByPokemonId(pokemonId);
        abilities.forEach(ability ->
                ability.setLastGeneration(TranslationUtils.translateVersion(ability.getLastGeneration()))
        );
        dto.getProfile().setAbilities(abilities);
    }
    
    /**
     * 为宝可梦DTO添加进化链信息
     *
     * @param dto 宝可梦详情DTO
     * @param pokemonId 宝可梦ID
     */
    private void enrichWithEvolutionChain(PokemonDetailResponseDto dto, Integer pokemonId) {
        Integer chainId = pokemonMapper.findChainIdByPokemonId(pokemonId);
        if (chainId != null) {
            List<EvolutionChain> rawSteps = pokemonMapper.findEvolutionStepsByChainId(chainId);
            if (rawSteps != null && !rawSteps.isEmpty()) {
                List<EvolutionStepDto> evolutionChain = assembleEvolutionChain(rawSteps);
                dto.setEvolutionChain(evolutionChain);
            }
        }
    }
    
    /**
     * 为宝可梦DTO添加其他形态信息
     *
     * @param dto 宝可梦详情DTO
     * @param pokemon 宝可梦实体
     */
    private void enrichWithOtherForms(PokemonDetailResponseDto dto, Pokemon pokemon) {
        String nameEn = pokemon.getNameEn();
        String baseName = nameEn.contains("-") ? nameEn.substring(0, nameEn.indexOf('-')) : nameEn;
        List<Pokemon> relatedForms = pokemonMapper.findRelatedFormsByBaseName(baseName);

        List<RelatedFormDto> otherForms = relatedForms.stream()
                .filter(form -> !form.getId().equals(pokemon.getId()))
                .map(this::convertToFormDto)
                .collect(Collectors.toList());

        dto.setOtherForms(otherForms);
    }
    
    /**
     * 将宝可梦实体转换为形态DTO
     *
     * @param form 宝可梦实体
     * @return 形态DTO
     */
    private RelatedFormDto convertToFormDto(Pokemon form) {
        RelatedFormDto formDto = new RelatedFormDto();
        formDto.setId(form.getId());
        formDto.setNameCn(form.getNameCn());
        formDto.setFormName(form.getFormName() != null ? form.getFormName() : "普通形态");
        formDto.setSpriteHd(form.getSpriteHd());
        formDto.setSpritePixel(form.getSpritePixel());
        return formDto;
    }
    /**
     * 组装宝可梦进化链
     * 将数据库中的扁平进化链记录转换为树状结构
     *
     * @param rawSteps 原始进化链数据
     * @return 树状结构的进化链
     */
    private List<EvolutionStepDto> assembleEvolutionChain(List<EvolutionChain> rawSteps) {
        if (rawSteps == null || rawSteps.isEmpty()) {
            return Collections.emptyList();
        }

        // 数据预处理：按起始宝可梦ID分组
        Map<Integer, List<EvolutionChain>> fromMap = rawSteps.stream()
                .collect(Collectors.groupingBy(EvolutionChain::getFromPokemonId));

        // 收集所有作为进化目标的宝可梦ID
        Set<Integer> toSet = rawSteps.stream()
                .map(EvolutionChain::getToPokemonId)
                .collect(Collectors.toSet());

        // 寻找进化链的起点（不是任何进化的目标的宝可梦）
        Integer startId = null;
        for (Integer fromId : fromMap.keySet()) {
            if (!toSet.contains(fromId)) {
                startId = fromId;
                break;
            }
        }

        // 如果找不到起点，返回空列表
        if (startId == null) {
            return Collections.emptyList();
        }

        // 从起点开始递归构建进化树
        return buildEvolutionTree(startId, fromMap);
    }

    /**
     * 递归构建进化树
     *
     * @param currentId 当前要处理的宝可梦ID
     * @param fromMap 预处理好的进化关系图
     * @return 包含所有子节点的进化树列表
     */
    private List<EvolutionStepDto> buildEvolutionTree(Integer currentId, Map<Integer, List<EvolutionChain>> fromMap) {
        // 获取当前宝可梦的基本信息
        Pokemon currentPokemon = this.getById(currentId);
        if (currentPokemon == null) {
            return Collections.emptyList();
        }

        // 查找当前宝可梦的所有直接进化分支
        List<EvolutionChain> nextEvolutions = fromMap.get(currentId);

        // 如果当前宝可梦没有下一步进化，创建叶子节点
        if (nextEvolutions == null || nextEvolutions.isEmpty()) {
            EvolutionStepDto leafNode = createEvolutionNode(currentPokemon);
            return Collections.singletonList(leafNode);
        }

        // 处理所有进化分支
        List<EvolutionStepDto> results = new ArrayList<>();
        for (EvolutionChain evolution : nextEvolutions) {
            // 递归处理每个进化分支
            List<EvolutionStepDto> childChains = buildEvolutionTree(evolution.getToPokemonId(), fromMap);
            
            // 获取并设置进化条件
            String condition = getEvolutionCondition(evolution);
            
            // 为子节点设置进化条件
            for (EvolutionStepDto childNode : childChains) {
                childNode.setCondition(condition);
            }
            results.addAll(childChains);
        }

        // 创建当前节点并添加所有子节点
        EvolutionStepDto rootNode = createEvolutionNode(currentPokemon);
        rootNode.setEvolvesTo(results);
        
        return Collections.singletonList(rootNode);
    }
    
    /**
     * 创建进化链节点
     *
     * @param pokemon 宝可梦实体
     * @return 进化步骤DTO
     */
    private EvolutionStepDto createEvolutionNode(Pokemon pokemon) {
        EvolutionStepDto node = new EvolutionStepDto();
        node.setPokemonId(pokemon.getId());
        node.setPokemonName(pokemon.getNameCn());
        node.setSpritePixel(pokemon.getSpritePixel());
        return node;
    }
    
    /**
     * 获取进化条件的描述
     *
     * @param evolution 进化链实体
     * @return 进化条件描述
     */
    private String getEvolutionCondition(EvolutionChain evolution) {
        // 等级进化
        if ("level-up".equals(evolution.getTriggerMethod()) && evolution.getMinLevel() != null) {
            return "Lv. " + evolution.getMinLevel();
        } 
        // 道具进化
        else if ("use-item".equals(evolution.getTriggerMethod()) && evolution.getTriggerItem() != null) {
            Item triggerItem = itemMapper.selectOne(
                    new QueryWrapper<Item>().eq("name_en", evolution.getTriggerItem())
            );
            String itemNameCn = (triggerItem != null) ? 
                    triggerItem.getNameCn() : evolution.getTriggerItem();
            return "使用 " + itemNameCn;
        } 
        // 其他进化方式
        else {
            return evolution.getTriggerMethod();
        }
    }
    /**
     * 分页查询宝可梦列表，支持多维度筛选
     *
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键字（可选）
     * @param type 属性筛选（可选）
     * @param eggGroup 蛋组筛选（可选）
     * @return 封装好的分页结果DTO
     */
    @Override
    public PageResultDto<PokemonListItemDto> getPokemonList(long pageNum, long pageSize, String keyword, String type, String eggGroup) {
        // 创建分页对象
        Page<Pokemon> page = new Page<>(pageNum, pageSize);
        
        // 将蛋组名称转换为英文（如果有）
        String eggGroupEn = TranslationUtils.translateEggGroupToEn(eggGroup);
        
        // 执行分页查询
        Page<Pokemon> pokemonPage = pokemonMapper.findPokemonList(page, keyword, type, eggGroupEn);

        // 将实体对象转换为DTO对象
        List<PokemonListItemDto> dtoList = pokemonPage.getRecords().stream()
                .map(this::convertToPokemonListItemDto)
                .collect(Collectors.toList());

        // 封装分页结果
        PageResultDto<PokemonListItemDto> pageResult = new PageResultDto<>();
        pageResult.setTotal(pokemonPage.getTotal());
        pageResult.setTotalPages(pokemonPage.getPages());
        pageResult.setItems(dtoList);

        return pageResult;
    }
    
    /**
     * 将宝可梦实体转换为列表项DTO
     *
     * @param pokemon 宝可梦实体
     * @return 宝可梦列表项DTO
     */
    private PokemonListItemDto convertToPokemonListItemDto(Pokemon pokemon) {
        PokemonListItemDto dto = new PokemonListItemDto();
        dto.setId(pokemon.getId());
        dto.setNameCn(pokemon.getNameCn());
        dto.setNameEn(pokemon.getNameEn());
        dto.setSpritePixel(pokemon.getSpritePixel());

        // 组装中文属性列表
        List<String> types = new ArrayList<>();
        if (pokemon.getType1Cn() != null) types.add(pokemon.getType1Cn());
        if (pokemon.getType2Cn() != null) types.add(pokemon.getType2Cn());
        dto.setTypes(types);

        // 组装英文属性列表
        List<String> typesEn = new ArrayList<>();
        if (pokemon.getType1() != null) typesEn.add(pokemon.getType1());
        if (pokemon.getType2() != null) typesEn.add(pokemon.getType2());
        dto.setTypesEn(typesEn);

        return dto;
    }
    /**
     * 获取宝可梦可学习的技能列表
     *
     * @param idOrName 宝可梦的ID、中文名或英文名
     * @return 宝可梦可学习的技能列表
     */
    @Override
    public List<PokemonMoveDto> getPokemonMoves(String idOrName) {
        // 查找宝可梦
        Pokemon pokemon = findPokemonByIdOrName(idOrName);
        
        // 如果找不到宝可梦，返回一个空列表
        if (pokemon == null) {
            return Collections.emptyList();
        }

        // 查询宝可梦可学习的技能
        List<PokemonMoveDto> moves = pokemonMapper.findMovesByPokemonId(pokemon.getId());

        // 翻译游戏版本名称
        moves.forEach(move ->
                move.setLastLearnGeneration(
                        TranslationUtils.translateVersion(move.getLastLearnGeneration())
                )
        );

        return moves;
    }
    
    /**
     * 根据ID或名称查找宝可梦
     *
     * @param idOrName 宝可梦的ID、中文名或英文名
     * @return 宝可梦实体，如果未找到则返回null
     */
    private Pokemon findPokemonByIdOrName(String idOrName) {
        if (NumberUtils.isCreatable(idOrName)) {
            return this.getById(Integer.parseInt(idOrName));
        } else {
            return pokemonMapper.findBestMatchByName(idOrName.trim().toLowerCase());
        }
    }

    /**
     * 根据英文名列表批量获取宝可梦基础信息
     *
     * @param names 宝可梦英文名列表
     * @return 原始名称到宝可梦实体的映射，不存在的名称将被忽略
     */
    @Override
    public Map<String, Pokemon> findPokemonByNames(List<String> names) {
        if (names == null || names.isEmpty()) {
            return Collections.emptyMap();
        }

        // 构建名称到宝可梦的映射
        return names.stream()
                .distinct() // 避免重复查询
                .map(name -> {
                    // 为每个名字查找最佳匹配的宝可梦
                    Pokemon foundPokemon = pokemonMapper.findBestMatchByName(name.trim().toLowerCase());
                    // 创建一个键值对，即使宝可梦是null
                    return new AbstractMap.SimpleEntry<>(name, foundPokemon);
                })
                .filter(entry -> entry.getValue() != null) // 只保留找到宝可梦的条目
                .collect(Collectors.toMap(
                        AbstractMap.SimpleEntry::getKey,   // 键：原始名称
                        AbstractMap.SimpleEntry::getValue  // 值：宝可梦实体
                ));
    }
    
    /**
     * 获取所有蛋组列表
     *
     * @return 所有蛋组的中文名称列表
     */
    @Override
    @Cacheable("allEggGroups")
    public List<String> getAllEggGroups() {
        return pokemonMapper.findAllEggGroups().stream()
                .map(TranslationUtils::translateEggGroup)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 获取所有属性列表
     *
     * @return 所有属性的中文名称列表
     */
    @Override
    @Cacheable("allTypes")
    public List<String> getAllTypes() {
        return pokemonMapper.findAllTypes().stream()
                .map(TranslationUtils::translateEggGroup)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
