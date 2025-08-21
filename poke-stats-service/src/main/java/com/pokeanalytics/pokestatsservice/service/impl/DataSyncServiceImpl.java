package com.pokeanalytics.pokestatsservice.service.impl;

import com.pokeanalytics.pokestatsservice.common.ResultVO;
import com.pokeanalytics.pokestatsservice.dto.*;
import com.pokeanalytics.pokestatsservice.entity.AbilityInfo;
import com.pokeanalytics.pokestatsservice.entity.ItemInfo;
import com.pokeanalytics.pokestatsservice.entity.MoveInfo;
import com.pokeanalytics.pokestatsservice.entity.PokemonInfo;
import com.pokeanalytics.pokestatsservice.feign.PokedexDataClient;
import com.pokeanalytics.pokestatsservice.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataSyncServiceImpl implements DataSyncService {

    private final PokedexDataClient pokedexDataClient;
    private final PokemonInfoService pokemonInfoService;
    private final ItemInfoService itemInfoService;
    private final MoveInfoService moveInfoService;
    private final AbilityInfoService abilityInfoService;

    public DataSyncServiceImpl(PokedexDataClient pokedexDataClient, PokemonInfoService pokemonInfoService, ItemInfoService itemInfoService, MoveInfoService moveInfoService, AbilityInfoService abilityInfoService) {
        this.pokedexDataClient = pokedexDataClient;
        this.pokemonInfoService = pokemonInfoService;
        this.itemInfoService = itemInfoService;
        this.moveInfoService = moveInfoService;
        this.abilityInfoService = abilityInfoService;
    }

    @Override
    public void syncPokemonInfo() {
        log.info("--- 开始同步宝可梦基础信息 ---");
        pokemonInfoService.remove(null);
        long currentPage = 1;
        long totalPages = 1;
        final long PAGE_SIZE = 200;
        do {
            log.info("--> 正在同步宝可梦第 {} 页 / 共 {} 页...", currentPage, totalPages);
            ResultVO<PageResultDto<PokemonListItemDto>> result = pokedexDataClient.getPokemonList(currentPage, PAGE_SIZE);
            if (result != null && result.getCode() == 200 && result.getData() != null) {
                PageResultDto<PokemonListItemDto> pageData = result.getData();
                totalPages = pageData.getTotalPages();
                List<PokemonInfo> list = pageData.getItems().stream().map(dto -> {
                    PokemonInfo info = new PokemonInfo();
                    info.setId(dto.getId());
                    info.setNameCn(dto.getNameCn());
                    info.setNameEn(dto.getNameEn());
                    info.setSpritePixel(dto.getSpritePixel());
                    if (dto.getTypes() != null) info.setTypesCn(String.join(",", dto.getTypes()));
                    return info;
                }).collect(Collectors.toList());
                if (!list.isEmpty()) pokemonInfoService.saveBatch(list);
                currentPage++;
            } else {
                log.error("--> 同步宝可梦第 {} 页失败！", currentPage);
                break;
            }
        } while (currentPage <= totalPages);
        log.info("--- 宝可梦基础信息同步完成！---");
    }

    @Override
    public void syncItemInfo() {
        log.info("--- 开始同步道具基础信息 ---");
        itemInfoService.remove(null);
        long currentPage = 1;
        long totalPages = 1;
        final long PAGE_SIZE = 200;
        do {
            log.info("--> 正在同步道具第 {} 页 / 共 {} 页...", currentPage, totalPages);
            ResultVO<PageResultDto<ItemListItemDto>> result = pokedexDataClient.getItemList(currentPage, PAGE_SIZE);
            if (result != null && result.getCode() == 200 && result.getData() != null) {
                PageResultDto<ItemListItemDto> pageData = result.getData();
                totalPages = pageData.getTotalPages();
                List<ItemInfo> list = pageData.getItems().stream().map(dto -> {
                    ItemInfo info = new ItemInfo();
                    info.setId(dto.getId());
                    info.setNameCn(dto.getNameCn());
                    info.setNameEn(dto.getNameEn());
                    info.setCategoryCn(dto.getCategoryCn());
                    info.setSpriteUrl(dto.getSpriteUrl());
                    return info;
                }).collect(Collectors.toList());
                if (!list.isEmpty()) itemInfoService.saveBatch(list);
                currentPage++;
            } else {
                log.error("--> 同步道具第 {} 页失败！", currentPage);
                break;
            }
        } while (currentPage <= totalPages);
        log.info("--- 道具基础信息同步完成！---");
    }

    @Override
    public void syncMoveInfo() {
        log.info("--- 开始同步技能基础信息 ---");
        moveInfoService.remove(null);
        long currentPage = 1;
        long totalPages = 1;
        final long PAGE_SIZE = 200;
        do {
            log.info("--> 正在同步技能第 {} 页 / 共 {} 页...", currentPage, totalPages);
            ResultVO<PageResultDto<MoveListItemDto>> result = pokedexDataClient.getMoveList(currentPage, PAGE_SIZE);
            if (result != null && result.getCode() == 200 && result.getData() != null) {
                PageResultDto<MoveListItemDto> pageData = result.getData();
                totalPages = pageData.getTotalPages();
                List<MoveInfo> list = pageData.getItems().stream().map(dto -> {
                    MoveInfo info = new MoveInfo();
                    info.setId(dto.getId());
                    info.setNameCn(dto.getNameCn());
                    info.setNameEn(dto.getNameEn());
                    info.setTypeCn(dto.getTypeCn());
                    info.setDamageClassCn(dto.getDamageClassCn());
                    return info;
                }).collect(Collectors.toList());
                if (!list.isEmpty()) moveInfoService.saveBatch(list);
                currentPage++;
            } else {
                log.error("--> 同步技能第 {} 页失败！", currentPage);
                break;
            }
        } while (currentPage <= totalPages);
        log.info("--- 技能基础信息同步完成！---");
    }

    @Override
    public void syncAbilityInfo() {
        log.info("--- 开始同步特性基础信息 ---");
        abilityInfoService.remove(null);
        long currentPage = 1;
        long totalPages = 1;
        final long PAGE_SIZE = 200;
        do {
            log.info("--> 正在同步特性第 {} 页 / 共 {} 页...", currentPage, totalPages);
            ResultVO<PageResultDto<Ability>> result = pokedexDataClient.getAbilityList(currentPage, PAGE_SIZE);
            if (result != null && result.getCode() == 200 && result.getData() != null) {
                PageResultDto<Ability> pageData = result.getData();
                totalPages = pageData.getTotalPages();
                List<AbilityInfo> list = pageData.getItems().stream().map(dto -> {
                    AbilityInfo info = new AbilityInfo();
                    info.setId(dto.getId());
                    info.setNameCn(dto.getNameCn());
                    info.setNameEn(dto.getNameEn());
                    return info;
                }).collect(Collectors.toList());
                if (!list.isEmpty()) abilityInfoService.saveBatch(list);
                currentPage++;
            } else {
                log.error("--> 同步特性第 {} 页失败！", currentPage);
                break;
            }
        } while (currentPage <= totalPages);
        log.info("--- 特性基础信息同步完成！---");
    }
}