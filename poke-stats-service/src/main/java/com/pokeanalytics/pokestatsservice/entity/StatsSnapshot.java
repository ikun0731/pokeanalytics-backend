package com.pokeanalytics.pokestatsservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 统计数据快照实体类
 * 对应数据库表stats_snapshot，存储对战数据的统计快照信息
 */
@Data
@TableName("stats_snapshot")
public class StatsSnapshot {
    /**
     * 快照ID，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 对战格式
     * 如：gen9ou（第9代常规单打）、gen9ubers（第9代无限制单打）等
     */
    private String format;
    
    /**
     * 统计月份
     * 格式：YYYY-MM，如"2025-06"
     */
    private String statsMonth;
    
    /**
     * 天梯分数线
     * 如：1500、1630、1760等
     */
    private Integer ratingCutoff;
    
    /**
     * 创建时间
     * 快照数据的同步/创建时间
     */
    private LocalDateTime createdAt;
}