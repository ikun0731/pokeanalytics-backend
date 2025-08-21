/*
 Navicat Premium Data Transfer

 Source Server         : MySQL80
 Source Server Type    : MySQL
 Source Server Version : 80041
 Source Host           : localhost:3306
 Source Schema         : pokemon

 Target Server Type    : MySQL
 Target Server Version : 80041
 File Encoding         : 65001

 Date: 17/08/2025 01:05:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ability
-- ----------------------------
DROP TABLE IF EXISTS `ability`;
CREATE TABLE `ability`  (
  `id` int NOT NULL COMMENT '特性ID',
  `name_en` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '英文名',
  `name_cn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '中文名',
  `effect` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '特性效果描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '特性信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for evolution_chain
-- ----------------------------
DROP TABLE IF EXISTS `evolution_chain`;
CREATE TABLE `evolution_chain`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `chain_id` int NOT NULL COMMENT '进化链ID',
  `from_pokemon_id` int NOT NULL COMMENT '进化前的宝可梦ID',
  `to_pokemon_id` int NOT NULL COMMENT '进化后的宝可梦ID',
  `trigger_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '触发方式 (level-up, use-item, trade)',
  `trigger_item` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '触发道具',
  `min_level` int NULL DEFAULT NULL COMMENT '最低等级',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_chain_id`(`chain_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 959 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '进化链详情表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for item
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item`  (
  `id` int NOT NULL COMMENT '道具ID',
  `name_en` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '英文名',
  `name_cn` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '中文名',
  `category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类英文',
  `category_cn` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类中文',
  `effect` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '效果描述',
  `flavor_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '图鉴描述',
  `sprite_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '道具信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for move
-- ----------------------------
DROP TABLE IF EXISTS `move`;
CREATE TABLE `move`  (
  `id` int NOT NULL COMMENT '技能ID',
  `name_en` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '英文名',
  `name_cn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '中文名',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '属性',
  `type_cn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '属性中文',
  `power` int NULL DEFAULT NULL COMMENT '威力',
  `pp` int NULL DEFAULT NULL COMMENT 'PP值',
  `accuracy` int NULL DEFAULT NULL COMMENT '命中率',
  `flavor_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '技能描述',
  `damage_class` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '伤害类型英文',
  `damage_class_cn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '伤害类型中文',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '技能信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for nature
-- ----------------------------
DROP TABLE IF EXISTS `nature`;
CREATE TABLE `nature`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name_cn` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '中文名',
  `name_en` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '英文名',
  `increased_stat` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '提升的能力值 (atk,def,spa,spd,spe)',
  `decreased_stat` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '降低的能力值 (atk,def,spa,spd,spe)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '性格信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pokemon
-- ----------------------------
DROP TABLE IF EXISTS `pokemon`;
CREATE TABLE `pokemon`  (
  `id` int NOT NULL COMMENT '宝可梦的全国图鉴ID',
  `name_en` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '英文名',
  `name_cn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '中文名',
  `type_1` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '属性1',
  `type_2` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '属性2',
  `hp` int NULL DEFAULT NULL COMMENT 'HP种族值',
  `attack` int NULL DEFAULT NULL COMMENT '攻击种族值',
  `defense` int NULL DEFAULT NULL COMMENT '防御种族值',
  `special_attack` int NULL DEFAULT NULL COMMENT '特攻种族值',
  `special_defense` int NULL DEFAULT NULL COMMENT '特防种族值',
  `speed` int NULL DEFAULT NULL COMMENT '速度种族值',
  `sprite_pixel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '像素风图片地址',
  `flavor_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '图鉴介绍文本',
  `type_1_cn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '属性1中文',
  `type_2_cn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '属性2中文',
  `is_default` tinyint(1) NULL DEFAULT NULL COMMENT '是否是默认形态',
  `form_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '形态名称 (如 Alolan, Galarian)',
  `sprite_pixel_shiny` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '像素风图片地址(闪光)',
  `sprite_hd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '高清图片地址',
  `sprite_hd_shiny` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '高清图片地址(闪光)',
  `height` decimal(10, 2) NULL DEFAULT NULL COMMENT '身高(m)',
  `weight` decimal(10, 2) NULL DEFAULT NULL COMMENT '体重(kg)',
  `base_experience` int NULL DEFAULT NULL COMMENT '基础经验值',
  `ev_yield` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '击败后获得的努力值',
  `gender_rate` int NULL DEFAULT NULL COMMENT '性别比例(八分之几是雌性, -1为无性别)',
  `hatch_steps` int NULL DEFAULT NULL COMMENT '孵化步数',
  `capture_rate` int NULL DEFAULT NULL COMMENT '捕捉率',
  `egg_group_1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '蛋组1',
  `egg_group_2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '蛋组2',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '宝可梦基本信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for pokemon_ability
-- ----------------------------
DROP TABLE IF EXISTS `pokemon_ability`;
CREATE TABLE `pokemon_ability`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `pokemon_id` int NOT NULL COMMENT '宝可梦ID',
  `ability_id` int NOT NULL COMMENT '特性ID',
  `is_hidden` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是隐藏特性 (1:是, 0:否)',
  `last_generation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后存在的世代名称',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pokemon_id`(`pokemon_id` ASC) USING BTREE,
  INDEX `idx_ability_id`(`ability_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5372 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '宝可梦-特性关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for pokemon_move
-- ----------------------------
DROP TABLE IF EXISTS `pokemon_move`;
CREATE TABLE `pokemon_move`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `pokemon_id` int NOT NULL COMMENT '宝可梦ID',
  `move_id` int NOT NULL COMMENT '技能ID',
  `learn_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学习方式 (level-up, machine, tutor)',
  `level_learned_at` int NULL DEFAULT NULL COMMENT '升级学习的等级',
  `last_learn_generation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后可学习的世代名称',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pokemon_id`(`pokemon_id` ASC) USING BTREE,
  INDEX `idx_move_id`(`move_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 244139 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '宝可梦-技能关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for type_matchup
-- ----------------------------
DROP TABLE IF EXISTS `type_matchup`;
CREATE TABLE `type_matchup`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `attacking_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '攻击方属性',
  `defending_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '防御方属性',
  `multiplier` decimal(2, 1) NOT NULL COMMENT '伤害倍率 (2.0, 1.0, 0.5, 0.0)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_type_pair`(`attacking_type` ASC, `defending_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 241 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '属性克制关系表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
