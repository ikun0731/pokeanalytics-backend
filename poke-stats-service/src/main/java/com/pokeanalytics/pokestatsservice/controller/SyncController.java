package com.pokeanalytics.pokestatsservice.controller;

import com.pokeanalytics.pokestatsservice.service.DataSyncService;
import com.pokeanalytics.pokestatsservice.service.StatsSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据同步控制器
 * 提供手动触发各类数据同步任务的接口
 */
@RestController
@RequestMapping("/sync")
@Tag(name = "数据同步接口", description = "用于手动触发各类数据同步任务")
public class SyncController {

    /**
     * 对战数据同步服务
     */
    private final StatsSyncService statsSyncService;
    
    /**
     * 基础数据同步服务
     */
    private final DataSyncService dataSyncService;

    /**
     * 构造函数，通过依赖注入初始化同步服务
     *
     * @param statsSyncService 对战数据同步服务
     * @param dataSyncService 基础数据同步服务
     */
    public SyncController(StatsSyncService statsSyncService, DataSyncService dataSyncService) {
        this.statsSyncService = statsSyncService;
        this.dataSyncService = dataSyncService;
    }

    /**
     * 手动触发月度对战数据同步
     * 从配置的数据源同步最新月度对战数据统计
     *
     * @return 同步任务启动状态消息
     */
    @PostMapping("/monthly-stats")
    @Operation(summary = "手动触发月度对战数据同步")
    public String triggerMonthlySync() {
        statsSyncService.syncMonthlyStats();
        return "月度对战数据同步任务已在后台启动";
    }

    /**
     * 手动触发宝可梦基础信息同步
     * 从宝可梦数据服务同步宝可梦基础信息
     *
     * @return 同步任务启动状态消息
     */
    @PostMapping("/pokemon-info")
    @Operation(summary = "手动触发宝可梦基础信息同步")
    public String triggerPokemonInfoSync() {
        new Thread(() -> dataSyncService.syncPokemonInfo()).start();
        return "宝可梦基础信息同步任务已启动";
    }

    /**
     * 手动触发道具基础信息同步
     * 从宝可梦数据服务同步道具基础信息
     *
     * @return 同步任务启动状态消息
     */
    @PostMapping("/item-info")
    @Operation(summary = "手动触发道具基础信息同步")
    public String triggerItemInfoSync() {
        new Thread(() -> dataSyncService.syncItemInfo()).start();
        return "道具基础信息同步任务已启动";
    }

    /**
     * 手动触发技能基础信息同步
     * 从宝可梦数据服务同步技能基础信息
     *
     * @return 同步任务启动状态消息
     */
    @PostMapping("/move-info")
    @Operation(summary = "手动触发技能基础信息同步")
    public String triggerMoveInfoSync() {
        new Thread(() -> dataSyncService.syncMoveInfo()).start();
        return "技能基础信息同步任务已启动";
    }

    /**
     * 手动触发特性基础信息同步
     * 从宝可梦数据服务同步特性基础信息
     *
     * @return 同步任务启动状态消息
     */
    @PostMapping("/ability-info")
    @Operation(summary = "手动触发特性基础信息同步")
    public String triggerAbilityInfoSync() {
        new Thread(() -> dataSyncService.syncAbilityInfo()).start();
        return "特性基础信息同步任务已启动";
    }
    
    /**
     * 手动触发补充缺失的太晶数据
     * 此脚本只添加数据，不删除任何现有数据
     *
     * @return 同步任务启动状态消息
     */
    @PostMapping("/patch-teras")
    @Operation(summary = "手动触发补充缺失的太晶数据", description = "此脚本只添加数据，不删除任何现有数据。")
    public String triggerTeraPatch() {
        statsSyncService.patchMissingTeraData();
        return "补充缺失太晶数据的任务已在后台启动";
    }
}