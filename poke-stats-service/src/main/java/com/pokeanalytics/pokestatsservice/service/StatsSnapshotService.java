package com.pokeanalytics.pokestatsservice.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pokeanalytics.pokestatsservice.entity.StatsSnapshot;

/**
 * 统计快照服务接口
 * 提供对统计快照数据的基础操作，用于管理不同对战格式和天梯分数线的统计快照
 */
public interface StatsSnapshotService extends IService<StatsSnapshot> {}