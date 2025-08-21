package com.pokeanalytics.pokestatsservice.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokestatsservice.entity.StatsSnapshot;
import com.pokeanalytics.pokestatsservice.mapper.StatsSnapshotMapper;
import com.pokeanalytics.pokestatsservice.service.StatsSnapshotService;
import org.springframework.stereotype.Service;

/**
 * 统计快照服务实现类
 * 提供对统计快照数据的基础操作实现，基于MyBatis-Plus的ServiceImpl
 */
@Service
public class StatsSnapshotServiceImpl extends ServiceImpl<StatsSnapshotMapper, StatsSnapshot> implements StatsSnapshotService {}