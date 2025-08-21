package com.pokeanalytics.pokestatsservice.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokestatsservice.entity.MoveInfo;
import com.pokeanalytics.pokestatsservice.mapper.MoveInfoMapper;
import com.pokeanalytics.pokestatsservice.service.MoveInfoService;
import org.springframework.stereotype.Service;

/**
 * 技能基础信息服务实现类
 * 提供对技能基础信息的操作实现，基于MyBatis-Plus的ServiceImpl
 */
@Service
public class MoveInfoServiceImpl extends ServiceImpl<MoveInfoMapper, MoveInfo> implements MoveInfoService {}