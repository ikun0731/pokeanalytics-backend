package com.pokeanalytics.pokestatsservice.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokestatsservice.entity.ItemInfo;
import com.pokeanalytics.pokestatsservice.mapper.ItemInfoMapper;
import com.pokeanalytics.pokestatsservice.service.ItemInfoService;
import org.springframework.stereotype.Service;

/**
 * 道具基础信息服务实现类
 * 提供对道具基础信息的操作实现，基于MyBatis-Plus的ServiceImpl
 */
@Service
public class ItemInfoServiceImpl extends ServiceImpl<ItemInfoMapper, ItemInfo> implements ItemInfoService {}