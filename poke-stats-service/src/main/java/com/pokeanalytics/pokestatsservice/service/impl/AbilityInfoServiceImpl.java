package com.pokeanalytics.pokestatsservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokestatsservice.entity.AbilityInfo;
import com.pokeanalytics.pokestatsservice.mapper.AbilityInfoMapper;
import com.pokeanalytics.pokestatsservice.service.AbilityInfoService;
import org.springframework.stereotype.Service;

/**
 * 特性基础信息服务实现类
 * 提供对特性基础信息的操作实现，基于MyBatis-Plus的ServiceImpl
 */
@Service
public class AbilityInfoServiceImpl extends ServiceImpl<AbilityInfoMapper, AbilityInfo> implements AbilityInfoService {
}
