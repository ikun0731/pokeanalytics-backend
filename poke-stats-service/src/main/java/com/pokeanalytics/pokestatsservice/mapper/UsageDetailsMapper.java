package com.pokeanalytics.pokestatsservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pokeanalytics.pokestatsservice.entity.UsageDetails;

/**
 * 使用详情数据库操作接口
 * 提供对usage_details表的增删改查操作
 * 存储宝可梦的特性、道具、技能、队友、太晶属性等使用详情
 */
public interface UsageDetailsMapper extends BaseMapper<UsageDetails> {
}
