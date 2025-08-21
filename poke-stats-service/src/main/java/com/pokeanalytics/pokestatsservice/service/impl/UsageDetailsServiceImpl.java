package com.pokeanalytics.pokestatsservice.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokestatsservice.entity.UsageDetails;
import com.pokeanalytics.pokestatsservice.mapper.UsageDetailsMapper;
import com.pokeanalytics.pokestatsservice.service.UsageDetailsService;
import org.springframework.stereotype.Service;

/**
 * 使用详情服务实现类
 * 提供对使用详情数据的基础操作实现，基于MyBatis-Plus的ServiceImpl
 */
@Service
public class UsageDetailsServiceImpl extends ServiceImpl<UsageDetailsMapper, UsageDetails> implements UsageDetailsService {}