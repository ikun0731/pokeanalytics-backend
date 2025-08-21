package com.pokeanalytics.pokedexdataservice.runner;

import com.pokeanalytics.pokedexdataservice.dto.sync.ItemApiDto;
import com.pokeanalytics.pokedexdataservice.entity.Item;
import com.pokeanalytics.pokedexdataservice.service.ItemApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 道具同步运行器
 * 用于从外部API获取并同步宝可梦道具数据
 * 只在指定的"sync-item"配置文件下运行
 */
@Profile("sync-item")
@Component
@Slf4j
public class ItemSyncRunner implements CommandLineRunner {

    /**
     * 最新的游戏版本组
     */
    private static final String LATEST_VERSION_GROUP = "scarlet-violet";
    
    /**
     * PokeAPI道具列表URL
     */
    private static final String POKEAPI_ITEM_LIST_URL = "https://pokeapi.co/api/v2/item?limit=2500";
    
    /**
     * 简体中文语言代码
     */
    private static final String LANGUAGE_ZH_HANS = "zh-Hans";
    
    /**
     * 英文语言代码
     */
    private static final String LANGUAGE_EN = "en";

    /**
     * 道具分类的中文翻译映射
     */
    private static final Map<String, String> CATEGORY_TRANSLATION_MAP = Map.ofEntries(
            Map.entry("standard-balls", "常规球"), 
            Map.entry("special-balls", "特殊球"),
            Map.entry("healing-items", "回复道具"), 
            Map.entry("vitamins", "营养饮料"),
            Map.entry("in-a-pinch", "危急时持有物"), 
            Map.entry("held-items", "携带物品"),
            Map.entry("choice", "选择系列"), 
            Map.entry("evolution", "进化"),
            Map.entry("type-enhancement", "属性增强"), 
            Map.entry("mega-stones", "超级石"),
            Map.entry("all-machines", "所有招式机"), 
            Map.entry("battle-items", "战斗道具"),
            Map.entry("stat-boosts", "能力增强"), 
            Map.entry("general-items", "一般道具")
    );

    private final ItemApiService itemService;
    private final RestTemplate restTemplate;

    /**
     * 构造函数
     *
     * @param itemService 道具服务
     * @param restTemplate REST请求模板
     */
    public ItemSyncRunner(ItemApiService itemService, RestTemplate restTemplate) {
        this.itemService = itemService;
        this.restTemplate = restTemplate;
    }

    /**
     * 运行道具同步任务
     * 
     * @param args 命令行参数
     * @throws Exception 执行过程中可能抛出的异常
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("【道具同步任务】开始...");
        
        // 获取道具列表
        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.getForObject(POKEAPI_ITEM_LIST_URL, Map.class);
        if (response == null || !response.containsKey("results")) {
            log.error("无法获取道具列表");
            return;
        }

        @SuppressWarnings("unchecked")
        List<Map<String, String>> results = (List<Map<String, String>>) response.get("results");
        
        // 遍历并同步道具
        for (Map<String, String> itemInfo : results) {
            String itemUrl = itemInfo.get("url");
            // 从URL中解析出道具ID
            int itemId = extractItemIdFromUrl(itemUrl);
            
            // 检查道具是否已存在
            if (itemService.getById(itemId) != null) {
                log.info("道具 #{} 已存在，跳过。", itemId);
                continue;
            }
            
            // 同步单个道具
            syncSingleItem(itemUrl);
        }
        
        log.info("【道具同步任务】完成！");
    }
    
    /**
     * 从URL中提取道具ID
     *
     * @param url 道具API URL
     * @return 道具ID
     */
    private int extractItemIdFromUrl(String url) {
        String[] urlParts = url.split("/");
        return Integer.parseInt(urlParts[urlParts.length - 1]);
    }
    
    /**
     * 同步单个道具
     *
     * @param itemUrl 道具API URL
     */
    private void syncSingleItem(String itemUrl) {
        try {
            // 获取道具详情
            ItemApiDto itemDto = restTemplate.getForObject(itemUrl, ItemApiDto.class);
            if (itemDto == null) {
                return;
            }

            // 创建道具实体
            Item item = createItemFromDto(itemDto);
            
            // 保存道具
            itemService.save(item);
            log.info("成功同步道具: #{} {}", item.getId(), 
                    item.getNameCn() != null ? item.getNameCn() : item.getNameEn());
        } catch (Exception e) {
            log.error("同步道具失败: {}", itemUrl, e);
        }
    }
    
    /**
     * 从DTO创建道具实体
     *
     * @param itemDto 道具API DTO
     * @return 道具实体
     */
    private Item createItemFromDto(ItemApiDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setNameEn(itemDto.getName());
        
        // 设置图片URL
        if (itemDto.getSprites() != null) {
            item.setSpriteUrl(itemDto.getSprites().get("default"));
        }
        
        // 设置道具分类
        if (itemDto.getCategory() != null) {
            String categoryEn = itemDto.getCategory().get("name");
            item.setCategory(categoryEn);
            item.setCategoryCn(CATEGORY_TRANSLATION_MAP.getOrDefault(categoryEn, categoryEn));
        }
        
        // 设置中文名称、效果和风味文本
        findBestName(itemDto).ifPresent(item::setNameCn);
        findBestEffect(itemDto).ifPresent(item::setEffect);
        findBestFlavorText(itemDto).ifPresent(item::setFlavorText);
        
        return item;
    }

    /**
     * 寻找最佳的道具名称（优先简体中文）
     *
     * @param itemDto 道具API DTO
     * @return 最佳名称，可能为空
     */
    private Optional<String> findBestName(ItemApiDto itemDto) {
        if (itemDto.getNames() == null) {
            return Optional.empty();
        }
        return itemDto.getNames().stream()
                .filter(n -> n.getLanguage() != null && 
                        LANGUAGE_ZH_HANS.equals(n.getLanguage().get("name")))
                .map(n -> n.getName())
                .findFirst();
    }

    /**
     * 寻找最佳的道具效果描述（优先简体中文，其次英文）
     *
     * @param itemDto 道具API DTO
     * @return 最佳效果描述，可能为空
     */
    private Optional<String> findBestEffect(ItemApiDto itemDto) {
        if (itemDto.getEffectEntries() == null) {
            return Optional.empty();
        }
        
        // 尝试获取简体中文描述
        Optional<String> effectOpt = itemDto.getEffectEntries().stream()
                .filter(e -> e.getLanguage() != null && 
                        LANGUAGE_ZH_HANS.equals(e.getLanguage().get("name")) && 
                        e.getShortEffect() != null)
                .map(ItemApiDto.EffectEntry::getShortEffect)
                .findFirst();

        // 如果没有简体中文，尝试获取英文描述
        if (effectOpt.isEmpty()) {
            effectOpt = itemDto.getEffectEntries().stream()
                    .filter(e -> e.getLanguage() != null && 
                            LANGUAGE_EN.equals(e.getLanguage().get("name")) && 
                            e.getShortEffect() != null)
                    .map(ItemApiDto.EffectEntry::getShortEffect)
                    .findFirst();
        }
        
        return effectOpt;
    }

    /**
     * 寻找最佳的道具风味文本（按优先级：最新版本的简体中文 > 任何简体中文 > 任何英文）
     *
     * @param itemDto 道具API DTO
     * @return 最佳风味文本，可能为空
     */
    private Optional<String> findBestFlavorText(ItemApiDto itemDto) {
        if (itemDto.getFlavorTextEntries() == null) {
            return Optional.empty();
        }
        
        // 优先级1：最新版本的简体中文
        Optional<String> textOpt = itemDto.getFlavorTextEntries().stream()
                .filter(ft -> ft.getLanguage() != null && 
                        LANGUAGE_ZH_HANS.equals(ft.getLanguage().get("name")) && 
                        ft.getVersionGroup() != null && 
                        LATEST_VERSION_GROUP.equals(ft.getVersionGroup().get("name")) && 
                        ft.getText() != null)
                .map(ft -> formatFlavorText(ft.getText()))
                .findFirst();

        // 优先级2：任何简体中文
        if (textOpt.isEmpty()) {
            textOpt = itemDto.getFlavorTextEntries().stream()
                    .filter(ft -> ft.getLanguage() != null && 
                            LANGUAGE_ZH_HANS.equals(ft.getLanguage().get("name")) && 
                            ft.getText() != null)
                    .map(ft -> formatFlavorText(ft.getText()))
                    .findFirst();
        }

        // 优先级3：任何英文
        if (textOpt.isEmpty()) {
            textOpt = itemDto.getFlavorTextEntries().stream()
                    .filter(ft -> ft.getLanguage() != null && 
                            LANGUAGE_EN.equals(ft.getLanguage().get("name")) && 
                            ft.getText() != null)
                    .map(ft -> formatFlavorText(ft.getText()))
                    .findFirst();
        }
        
        return textOpt;
    }
    
    /**
     * 格式化风味文本（替换换行符和换页符为空格）
     *
     * @param text 原始文本
     * @return 格式化后的文本
     */
    private String formatFlavorText(String text) {
        return text.replace('\n', ' ').replace('\f', ' ');
    }
}