package com.pokeanalytics.pokedexdataservice.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 翻译工具类
 * 提供各种游戏术语的中英文翻译和格式化功能
 */
public class TranslationUtils {

    /**
     * 蛋组英文到中文的映射
     */
    private static final Map<String, String> EGG_GROUP_MAP = Map.ofEntries(
            Map.entry("monster", "怪兽"),
            Map.entry("water1", "水中1"),
            Map.entry("bug", "虫"),
            Map.entry("flying", "飞行"),
            Map.entry("ground", "陆上"),
            Map.entry("fairy", "妖精"),
            Map.entry("plant", "植物"),
            Map.entry("humanshape", "人形"),
            Map.entry("water3", "水中3"),
            Map.entry("mineral", "矿物"),
            Map.entry("indeterminate", "不定形"),
            Map.entry("water2", "水中2"),
            Map.entry("ditto", "百变怪"),
            Map.entry("dragon", "龙"),
            Map.entry("no-eggs", "未发现蛋")
    );

    /**
     * 能力值英文到中文的映射
     */
    private static final Map<String, String> STAT_NAME_MAP = Map.ofEntries(
            Map.entry("hp", "HP"),
            Map.entry("attack", "攻击"),
            Map.entry("defense", "防御"),
            Map.entry("special-attack", "特攻"),
            Map.entry("special-defense", "特防"),
            Map.entry("speed", "速度")
    );

    /**
     * 游戏版本英文到中文的映射
     */
    private static final Map<String, String> GAME_VERSION_MAP = Map.ofEntries(
            Map.entry("scarlet-violet", "朱/紫"),
            Map.entry("sword-shield", "剑/盾"),
            Map.entry("legends-arceus", "阿尔宙斯"),
            Map.entry("brilliant-diamond-and-shining-pearl", "珍钻复刻"),
            Map.entry("sun-moon", "日/月"),
            Map.entry("ultra-sun-ultra-moon", "究极日/月"),
            Map.entry("x-y", "X/Y")
    );

    /**
     * 努力值收益的正则表达式模式
     * 用于解析形如 "Speed: 2" 的字符串
     */
    private static final Pattern EV_YIELD_PATTERN = Pattern.compile("([a-zA-Z\\s\\-]+):\\s*(\\d+)");

    /**
     * 蛋组中文到英文的映射，动态根据EGG_GROUP_MAP反向生成
     */
    private static final Map<String, String> EGG_GROUP_MAP_REVERSE = EGG_GROUP_MAP.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (v1, v2) -> v1));

    /**
     * 格式化努力值收益字符串
     *
     * @param evYieldString 英文格式的努力值收益字符串，如"Speed: 2, Attack: 1"
     * @return 格式化后的中文努力值字符串，如"速度+2, 攻击+1"
     */
    public static String formatEvYield(String evYieldString) {
        if (evYieldString == null || evYieldString.isEmpty()) {
            return "未知";
        }

        List<String> formattedParts = new ArrayList<>();
        Matcher matcher = EV_YIELD_PATTERN.matcher(evYieldString);

        while (matcher.find()) {
            String rawStatKey = matcher.group(1);
            String statValue = matcher.group(2).trim();

            // 标准化处理，将"Special Attack"转换为"special-attack"格式
            String normalizedStatKey = rawStatKey.trim().toLowerCase().replace(" ", "-");

            String statNameCn = STAT_NAME_MAP.getOrDefault(normalizedStatKey, rawStatKey);
            formattedParts.add(statNameCn + "+" + statValue);
        }

        if (formattedParts.isEmpty()) {
            return evYieldString;
        }

        return String.join(", ", formattedParts);
    }

    /**
     * 将蛋组英文名称翻译为中文
     *
     * @param englishName 蛋组英文名称
     * @return 翻译后的蛋组中文名称，如果找不到对应翻译则返回原文
     */
    public static String translateEggGroup(String englishName) {
        if (englishName == null) return null;
        // 标准化处理键名
        String normalizedKey = englishName.trim().toLowerCase().replace(" ", "-");
        return EGG_GROUP_MAP.getOrDefault(normalizedKey, englishName);
    }

    /**
     * 将游戏版本英文名称翻译为中文
     *
     * @param englishName 游戏版本英文名称
     * @return 翻译后的游戏版本中文名称，如果找不到对应翻译则返回原文
     */
    public static String translateVersion(String englishName) {
        if (englishName == null) return null;
        String normalizedKey = englishName.trim().toLowerCase().replace(" ", "-");
        return GAME_VERSION_MAP.getOrDefault(normalizedKey, englishName);
    }

    /**
     * 将蛋组中文名称翻译为英文
     *
     * @param chineseName 蛋组中文名称
     * @return 翻译后的蛋组英文名称，如果找不到对应翻译则返回原文
     */
    public static String translateEggGroupToEn(String chineseName) {
        if (chineseName == null) return null;
        return EGG_GROUP_MAP_REVERSE.getOrDefault(chineseName, chineseName);
    }
}