package com.pokeanalytics.pokestatsservice.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pokeanalytics.pokestatsservice.entity.PokemonInfo;

/**
 * 宝可梦基础信息服务接口
 * 提供对宝可梦基础信息的操作，包括名称、属性、图片等基本数据
 */
public interface PokemonInfoService extends IService<PokemonInfo> {}