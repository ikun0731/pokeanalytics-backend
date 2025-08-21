package com.pokeanalytics.pokestatsservice.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokestatsservice.entity.PokemonInfo;
import com.pokeanalytics.pokestatsservice.mapper.PokemonInfoMapper;
import com.pokeanalytics.pokestatsservice.service.PokemonInfoService;
import org.springframework.stereotype.Service;

/**
 * 宝可梦基础信息服务实现类
 * 提供对宝可梦基础信息的操作实现，基于MyBatis-Plus的ServiceImpl
 */
@Service
public class PokemonInfoServiceImpl extends ServiceImpl<PokemonInfoMapper, PokemonInfo> implements PokemonInfoService {}