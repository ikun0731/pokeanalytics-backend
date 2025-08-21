/*
 Navicat Premium Data Transfer

 Source Server         : MySQL80
 Source Server Type    : MySQL
 Source Server Version : 80041
 Source Host           : localhost:3306
 Source Schema         : pokemon_stats

 Target Server Type    : MySQL
 Target Server Version : 80041
 File Encoding         : 65001

 Date: 17/08/2025 01:05:40
*/
CREATE DATABASE IF NOT EXISTS `pokemon_stats` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `pokemon_stats`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ability_info
-- ----------------------------
DROP TABLE IF EXISTS `ability_info`;
CREATE TABLE `ability_info`  (
  `id` int NOT NULL,
  `name_en` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name_cn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '特性基础信息冗余表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for item_info
-- ----------------------------
DROP TABLE IF EXISTS `item_info`;
CREATE TABLE `item_info`  (
  `id` int NOT NULL COMMENT '道具ID',
  `name_en` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name_cn` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `category_cn` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类中文',
  `sprite_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '道具基础信息冗余表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for move_info
-- ----------------------------
DROP TABLE IF EXISTS `move_info`;
CREATE TABLE `move_info`  (
  `id` int NOT NULL COMMENT '技能ID',
  `name_en` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name_cn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `type_cn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '属性中文',
  `damage_class_cn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '伤害类型中文',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '技能基础信息冗余表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pokemon_info
-- ----------------------------
DROP TABLE IF EXISTS `pokemon_info`;
CREATE TABLE `pokemon_info`  (
  `id` int NOT NULL COMMENT '宝可梦的全国图鉴ID',
  `name_en` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '英文名',
  `name_cn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '中文名',
  `sprite_pixel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '像素图地址',
  `types_cn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '中文属性, 用逗号分隔',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '宝可梦基础信息冗余表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pokemon_tier
-- ----------------------------
DROP TABLE IF EXISTS `pokemon_tier`;
CREATE TABLE `pokemon_tier`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `format` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '对战格式 (e.g., gen9ou, gen9nationaldex)',
  `pokemon_name_en` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '宝可梦英文名',
  `tier` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '官方分级 (e.g., OU, UU, Uber)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_format_pokemon`(`format` ASC, `pokemon_name_en` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 71 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '宝可梦官方分级总表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pokemon_usage_stats
-- ----------------------------
DROP TABLE IF EXISTS `pokemon_usage_stats`;
CREATE TABLE `pokemon_usage_stats`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `snapshot_id` int NOT NULL,
  `pokemon_name_en` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `usage_rate` decimal(10, 6) NOT NULL,
  `rank` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4924 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '宝可梦使用率统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stats_snapshot
-- ----------------------------
DROP TABLE IF EXISTS `stats_snapshot`;
CREATE TABLE `stats_snapshot`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `format` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '对战格式 (e.g., gen9ou)',
  `stats_month` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '数据月份 (YYYY-MM)',
  `rating_cutoff` int NOT NULL COMMENT '天梯分数线 (e.g., 1500, 1695, 1825)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_format_month_cutoff`(`format` ASC, `stats_month` ASC, `rating_cutoff` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '对战数据快照信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for usage_details
-- ----------------------------
DROP TABLE IF EXISTS `usage_details`;
CREATE TABLE `usage_details`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `stats_id` int NOT NULL,
  `detail_type` enum('move','item','ability','teammate','tera') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `detail_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `usage_percentage` decimal(30, 15) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_stats_id_type`(`stats_id` ASC, `detail_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2408556 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '使用率详情表 (招式、道具、特性等)' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
