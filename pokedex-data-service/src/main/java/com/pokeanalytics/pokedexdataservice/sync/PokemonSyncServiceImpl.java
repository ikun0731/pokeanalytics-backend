package com.pokeanalytics.pokedexdataservice.sync;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokedexdataservice.dto.sync.EvolutionChainApiDto;
import com.pokeanalytics.pokedexdataservice.dto.sync.MoveApiDto;
import com.pokeanalytics.pokedexdataservice.dto.sync.PokemonApiDto;
import com.pokeanalytics.pokedexdataservice.dto.sync.PokemonSpeciesApiDto;
import com.pokeanalytics.pokedexdataservice.entity.EvolutionChain;
import com.pokeanalytics.pokedexdataservice.entity.Move;
import com.pokeanalytics.pokedexdataservice.entity.Pokemon;
import com.pokeanalytics.pokedexdataservice.entity.PokemonMove;
import com.pokeanalytics.pokedexdataservice.mapper.PokemonMapper;
import com.pokeanalytics.pokedexdataservice.service.EvolutionChainService;
import com.pokeanalytics.pokedexdataservice.service.MoveApiService;
import com.pokeanalytics.pokedexdataservice.service.PokemonMoveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 宝可梦数据同步服务实现类
 * 提供从外部API同步宝可梦数据的功能实现
 */
@Slf4j
@Service
public class PokemonSyncServiceImpl extends ServiceImpl<PokemonMapper, Pokemon> implements PokemonSyncService {

    /**
     * 要同步的宝可梦世代
     * 1=第一代, 2=第二代, 以此类推
     */
    private static final int GENERATION_TO_SYNC = 9;

    /**
     * 最新的游戏版本组名称
     */
    private static final String LATEST_VERSION_GROUP = "scarlet-violet";
    
    /**
     * 各世代宝可梦ID范围映射
     */
    private static final Map<Integer, int[]> GENERATION_RANGES = new LinkedHashMap<>();
    static {
        GENERATION_RANGES.put(1, new int[]{1, 151});      // 第一世代：红/绿/蓝/黄
        GENERATION_RANGES.put(2, new int[]{152, 251});    // 第二世代：金/银/水晶
        GENERATION_RANGES.put(3, new int[]{252, 386});    // 第三世代：红宝石/蓝宝石/绿宝石
        GENERATION_RANGES.put(4, new int[]{387, 493});    // 第四世代：钻石/珍珠/白金
        GENERATION_RANGES.put(5, new int[]{494, 649});    // 第五世代：黑/白/黑2/白2
        GENERATION_RANGES.put(6, new int[]{650, 721});    // 第六世代：X/Y/欧米伽红宝石/阿尔法蓝宝石
        GENERATION_RANGES.put(7, new int[]{722, 809});    // 第七世代：太阳/月亮/究极太阳/究极月亮
        GENERATION_RANGES.put(8, new int[]{810, 905});    // 第八世代：剑/盾/明亮珍珠/闪耀钻石
        GENERATION_RANGES.put(9, new int[]{906, 1025});   // 第九世代：朱/紫
    }

    /**
     * REST请求模板
     */
    private final RestTemplate restTemplate;
    
    /**
     * 技能服务
     */
    private final MoveApiService moveService;
    
    /**
     * 宝可梦技能关联服务
     */
    private final PokemonMoveService pokemonMoveService;
    
    /**
     * 进化链服务
     */
    private final EvolutionChainService evolutionChainService;

    /**
     * 自身服务的懒加载引用，用于事务处理
     */
    @Lazy
    @Autowired
    private PokemonSyncService self;

    /**
     * 属性英文到中文的翻译映射
     */
    private static final Map<String, String> TYPE_TRANSLATION_MAP = Map.ofEntries(
            Map.entry("normal", "一般"), Map.entry("fighting", "格斗"), Map.entry("flying", "飞行"),
            Map.entry("poison", "毒"), Map.entry("ground", "地面"), Map.entry("rock", "岩石"),
            Map.entry("bug", "虫"), Map.entry("ghost", "幽灵"), Map.entry("steel", "钢"),
            Map.entry("fire", "火"), Map.entry("water", "水"), Map.entry("grass", "草"),
            Map.entry("electric", "电"), Map.entry("psychic", "超能力"), Map.entry("ice", "冰"),
            Map.entry("dragon", "龙"), Map.entry("dark", "恶"), Map.entry("fairy", "妖精")
    );

    /**
     * 伤害类型英文到中文的翻译映射
     */
    private static final Map<String, String> DAMAGE_CLASS_TRANSLATION_MAP = Map.of(
            "physical", "物理",
            "special", "特殊",
            "status", "变化"
    );

    /**
     * 构造函数，通过依赖注入初始化所需服务
     *
     * @param restTemplate REST请求模板，用于调用外部API
     * @param moveService 技能服务，用于保存和查询技能数据
     * @param pokemonMoveService 宝可梦技能关联服务，用于处理宝可梦与技能的关联关系
     * @param evolutionChainService 进化链服务，用于保存宝可梦进化关系
     */
    public PokemonSyncServiceImpl(RestTemplate restTemplate, MoveApiService moveService, PokemonMoveService pokemonMoveService, EvolutionChainService evolutionChainService) {
        this.restTemplate = restTemplate;
        this.moveService = moveService;
        this.pokemonMoveService = pokemonMoveService;
        this.evolutionChainService = evolutionChainService;
    }

    /**
     * 同步所有宝可梦数据
     * 根据配置的世代编号，从外部API同步该世代的所有宝可梦数据
     * 使用事务性调用单个宝可梦同步方法，确保每个宝可梦数据的完整性
     */
    @Override
    public void syncPokemonData() {
        log.info("准备开始同步第 {} 代宝可梦数据...", GENERATION_TO_SYNC);

        // 获取当前配置世代的ID范围
        int[] range = GENERATION_RANGES.get(GENERATION_TO_SYNC);
        if (range == null) {
            log.error("无效的世代编号: {}", GENERATION_TO_SYNC);
            return;
        }

        int startId = range[0];
        int endId = range[1];

        // 检查数据库中是否已存在该世代的宝可梦数据，避免重复同步
        long countInGen = this.count(new QueryWrapper<Pokemon>().between("id", startId, endId));
        if (countInGen > 0) {
            log.info("第 {} 代宝可梦数据已存在 ({}条)，跳过同步。", GENERATION_TO_SYNC, countInGen);
            return;
        }

        log.info("--- 开始同步第 {} 代 (ID: {} - {}) ---", GENERATION_TO_SYNC, startId, endId);
        // 逐个同步该世代范围内的每只宝可梦
        for (int i = startId; i <= endId; i++) {
            try {
                // 使用self引用调用确保事务生效
                self.syncSinglePokemon(i);
            } catch (Exception e) {
                log.error("同步ID为 {} 的宝可梦时发生严重错误，已跳过", i, e);
            }
        }
        log.info("--- 第 {} 代同步完成！ ---", GENERATION_TO_SYNC);
    }

    /**
     * 同步单个宝可梦数据
     * 从外部API获取并保存指定ID宝可梦的基础数据、技能池和进化链
     *
     * @param pokemonId 要同步的宝可梦ID
     */
    @Override
    @Transactional
    public void syncSinglePokemon(int pokemonId) {
        // 调用PokeAPI获取宝可梦基础数据
        PokemonApiDto pokemonDto = restTemplate.getForObject("https://pokeapi.co/api/v2/pokemon/" + pokemonId, PokemonApiDto.class);
        // 调用PokeAPI获取宝可梦物种数据
        PokemonSpeciesApiDto speciesDto = restTemplate.getForObject("https://pokeapi.co/api/v2/pokemon-species/" + pokemonId, PokemonSpeciesApiDto.class);
        // 如果任一API调用失败，则中止同步
        if (pokemonDto == null || speciesDto == null) return;

        // 构建并保存宝可梦实体
        Pokemon pokemon = buildPokemonEntity(pokemonDto, speciesDto);
        this.save(pokemon);
        log.info("成功同步宝可梦: #{} {}", pokemon.getId(), pokemon.getNameCn());

        // 同步该宝可梦可学习的所有技能
        syncMovesForPokemon(pokemonDto);

        // 只有进化链的起始宝可梦才需要同步整个进化链
        // 通过检查是否有进化前形态来判断(evolvesFromSpecies为null表示没有进化前形态)
        if (speciesDto.getEvolvesFromSpecies() == null && speciesDto.getEvolutionChain() != null) {
            String evolutionChainUrl = speciesDto.getEvolutionChain().get("url");
            syncEvolutionChain(evolutionChainUrl);
        }
    }

    /**
     * 同步宝可梦可学习的技能
     * 处理一个宝可梦的所有可学习技能，并按照学习方式分类保存到数据库
     * 
     * @param pokemonDto 宝可梦数据DTO，包含技能学习信息
     */
    private void syncMovesForPokemon(PokemonApiDto pokemonDto) {
        // 遍历宝可梦可以学习的每一个技能条目
        for (PokemonApiDto.MoveEntry moveEntry : pokemonDto.getMoves()) {

            // 将同一个技能的所有学习详情，按学习方法（如"level-up"升级习得、"machine"技能机习得等）分组
            // 一个技能可能通过多种方式学习，我们需要分别记录
            Map<String, List<PokemonApiDto.VersionGroupDetail>> detailsByMethod = moveEntry.getVersionGroupDetails().stream()
                    .collect(Collectors.groupingBy(detail -> detail.getMoveLearnMethod().get("name")));

            // 遍历这个技能的每一种学习方式
            detailsByMethod.forEach((method, details) -> {

                // 在当前技能、当前学习方式的所有游戏版本记录中，找到最新版本的那一个
                // 通过解析URL中的版本ID来判断版本新旧
                Optional<PokemonApiDto.VersionGroupDetail> latestDetailOpt = details.stream()
                        .max(Comparator.comparingInt(detail -> {
                            String[] urlParts = detail.getVersionGroup().get("url").split("/");
                            return Integer.parseInt(urlParts[urlParts.length - 1]);
                        }));

                // 如果找到了最新版本的记录，就为这个【技能+学习方式】的组合保存一条记录
                latestDetailOpt.ifPresent(latestDetail -> {
                    // 获取技能数据，如果不存在则同步
                    String moveUrl = moveEntry.getMove().get("url");
                    Move move = getOrSyncMove(moveUrl);
                    if (move != null) {
                        // 创建并保存宝可梦与技能的关联记录
                        PokemonMove pokemonMove = new PokemonMove();
                        pokemonMove.setPokemonId(pokemonDto.getId());
                        pokemonMove.setMoveId(move.getId());
                        pokemonMove.setLearnMethod(method);
                        pokemonMove.setLastLearnGeneration(latestDetail.getVersionGroup().get("name"));
                        // 如果是通过升级学习的技能，还需要记录习得等级
                        if ("level-up".equals(method)) {
                            pokemonMove.setLevelLearnedAt(latestDetail.getLevelLearnedAt());
                        }
                        pokemonMoveService.save(pokemonMove);
                    }
                });
            });
        }
    }

    /**
     * 获取或同步技能数据
     * 根据技能URL，获取已存在的技能数据或从API同步新的技能数据
     * 
     * @param moveUrl 技能API的URL
     * @return 技能实体，如果同步失败则返回null
     */
    private Move getOrSyncMove(String moveUrl) {
        // 从URL中提取技能ID
        String[] urlParts = moveUrl.split("/");
        int moveId = Integer.parseInt(urlParts[urlParts.length - 1]);
        
        // 尝试从数据库获取已存在的技能数据
        Move existingMove = moveService.getById(moveId);

        // 如果技能已存在且所有关键信息都已完整，则无需再次同步
        if (existingMove != null && existingMove.getDamageClass() != null && !existingMove.getDamageClass().isEmpty()
                && existingMove.getFlavorText() != null && !existingMove.getFlavorText().isEmpty()) {
            return existingMove;
        }

        try {
            // 从API获取技能详细数据
            MoveApiDto moveDto = restTemplate.getForObject(moveUrl, MoveApiDto.class);
            if (moveDto == null) return null;

            // 使用已有实体或创建新实体
            final Move move = (existingMove != null) ? existingMove : new Move();

            // 如果是新技能，设置基础属性
            if (existingMove == null) {
                move.setId(moveDto.getId());
                move.setNameEn(moveDto.getName());
                move.setPower(moveDto.getPower());
                move.setPp(moveDto.getPp());
                move.setAccuracy(moveDto.getAccuracy());
                String typeEn = moveDto.getType().get("name");
                move.setType(typeEn);
                move.setTypeCn(TYPE_TRANSLATION_MAP.getOrDefault(typeEn, typeEn));
                // 设置中文名称
                moveDto.getNames().stream()
                        .filter(n -> "zh-Hans".equals(n.getLanguage().get("name")))
                        .findFirst().ifPresent(n -> move.setNameCn(n.getName()));
            }

            // 技能描述处理逻辑：优先使用中文描述，找不到则使用英文描述作为备选
            if (move.getFlavorText() == null || move.getFlavorText().isEmpty()) {
                // 首先尝试查找中文描述
                Optional<String> flavorTextOpt = moveDto.getFlavorTextEntries().stream()
                        .filter(ft -> ft.getLanguage() != null && "zh-Hans".equals(ft.getLanguage().get("name")))
                        .map(ft -> ft.getFlavorText().replace('\n', ' ').replace('\f', ' '))
                        .findFirst();

                // 如果没有中文描述，则使用英文描述作为备选
                if (flavorTextOpt.isEmpty()) {
                    flavorTextOpt = moveDto.getFlavorTextEntries().stream()
                            .filter(ft -> ft.getLanguage() != null && "en".equals(ft.getLanguage().get("name")))
                            .map(ft -> ft.getFlavorText().replace('\n', ' ').replace('\f', ' '))
                            .findFirst();
                }

                flavorTextOpt.ifPresent(move::setFlavorText);
            }

            // 设置伤害类型(物理、特殊或变化)
            if (move.getDamageClass() == null || move.getDamageClass().isEmpty()) {
                if (moveDto.getDamageClass() != null) {
                    String damageClassEn = moveDto.getDamageClass().get("name");
                    move.setDamageClass(damageClassEn);
                    move.setDamageClassCn(DAMAGE_CLASS_TRANSLATION_MAP.getOrDefault(damageClassEn, damageClassEn));
                }
            }

            // 保存或更新技能数据
            moveService.saveOrUpdate(move);

            // 记录日志
            if (existingMove == null) {
                log.info("  -> 新技能入库: #{} {}", move.getId(), move.getNameCn());
            } else {
                log.info("  -> 技能数据补全: #{} {}", move.getId(), move.getNameCn());
            }

            return move;
        } catch (Exception e) {
            log.error("同步技能失败: {}", moveUrl, e);
            return null;
        }
    }


    /**
     * 同步宝可梦进化链数据
     * 从外部API获取并保存完整的进化链数据
     * 
     * @param evolutionChainUrl 进化链API的URL
     */
    private void syncEvolutionChain(String evolutionChainUrl) {
        try {
            // 从API获取进化链数据
            EvolutionChainApiDto chainDto = restTemplate.getForObject(evolutionChainUrl, EvolutionChainApiDto.class);
            if (chainDto == null) return;
            
            log.info("-> 开始同步进化链, ID: {}", chainDto.getId());
            // 递归处理进化链结构
            parseAndSaveEvolutionLink(chainDto.getChain(), chainDto.getId());
        } catch (Exception e) {
            log.error("同步进化链失败: {}", evolutionChainUrl, e);
        }
    }

    /**
     * 解析并保存进化链中的单个进化环节
     * 递归处理进化链的树形结构，保存每个进化步骤的详细信息
     * 
     * @param link 当前进化环节
     * @param chainId 所属进化链ID
     */
    private void parseAndSaveEvolutionLink(EvolutionChainApiDto.ChainLink link, int chainId) {
        // 如果链接无效或没有进化到的宝可梦，则终止递归
        if (link == null || link.getEvolvesTo() == null || link.getEvolvesTo().isEmpty()) {
            return;
        }
        
        // 解析当前宝可梦ID
        String[] fromUrlParts = link.getSpecies().get("url").split("/");
        int fromId = Integer.parseInt(fromUrlParts[fromUrlParts.length - 1]);
        
        // 遍历所有进化后的形态
        for (EvolutionChainApiDto.ChainLink nextLink : link.getEvolvesTo()) {
            // 解析进化后宝可梦ID
            String[] toUrlParts = nextLink.getSpecies().get("url").split("/");
            int toId = Integer.parseInt(toUrlParts[toUrlParts.length - 1]);
            
            // 如果存在进化详情，则创建进化链记录
            if (!nextLink.getEvolutionDetails().isEmpty()) {
                // 获取第一个进化条件（通常只有一个）
                EvolutionChainApiDto.EvolutionDetail detail = nextLink.getEvolutionDetails().get(0);
                
                // 创建进化链实体并设置属性
                EvolutionChain evolution = new EvolutionChain();
                evolution.setChainId(chainId);                           // 设置所属进化链ID
                evolution.setFromPokemonId(fromId);                      // 进化前宝可梦ID
                evolution.setToPokemonId(toId);                          // 进化后宝可梦ID
                evolution.setTriggerMethod(detail.getTrigger().get("name")); // 进化触发方式
                evolution.setMinLevel(detail.getMinLevel());             // 进化所需最低等级
                
                // 如果进化需要道具，设置道具名称
                if(detail.getItem() != null) {
                    evolution.setTriggerItem(detail.getItem().get("name"));
                }
                
                // 保存进化链记录
                evolutionChainService.save(evolution);
            }
            
            // 递归处理下一级进化
            parseAndSaveEvolutionLink(nextLink, chainId);
        }
    }

    /**
     * 构建宝可梦实体对象
     * 根据API返回的宝可梦基础数据和物种数据，构建完整的宝可梦实体
     * 
     * @param pokemonDto 宝可梦基础数据DTO
     * @param speciesDto 宝可梦物种数据DTO
     * @return 构建好的宝可梦实体
     */
    private Pokemon buildPokemonEntity(PokemonApiDto pokemonDto, PokemonSpeciesApiDto speciesDto) {
        Pokemon pokemon = new Pokemon();
        pokemon.setId(pokemonDto.getId());

        // 处理宝可梦是否为默认形态和特殊形态名称
        pokemon.setIsDefault(pokemonDto.isDefault());
        String pokemonName = pokemonDto.getName();
        pokemon.setNameEn(pokemonName);
        
        // 如果名称包含连字符，说明是特殊形态
        // 例如："pikachu-gmax"，提取"gmax"作为形态名
        if (pokemonName.contains("-")) {
            pokemon.setFormName(pokemonName.substring(pokemonName.indexOf("-") + 1));
        }

        // 设置中文名称
        speciesDto.getNames().stream()
                .filter(nameEntry -> "zh-Hans".equals(nameEntry.getLanguage().get("name")))
                .findFirst()
                .ifPresent(nameEntry -> pokemon.setNameCn(nameEntry.getName()));

        // 设置图鉴描述，优先使用最新版本的中文描述
        Optional<String> flavorTextOpt = speciesDto.getFlavorTextEntries().stream()
                // 优先查找最新版本的中文描述
                .filter(ft -> ft.getLanguage() != null && "zh-Hans".equals(ft.getLanguage().get("name"))
                        && ft.getVersion() != null && LATEST_VERSION_GROUP.equals(ft.getVersion().get("name")))
                // 处理换行符和分页符，确保文本格式一致
                .map(ft -> ft.getFlavorText().replace("\n", " ").replace("\f", " "))
                .findFirst();

        // 如果没有找到最新版本的中文描述，则使用任何中文描述
        if (flavorTextOpt.isEmpty()) {
            flavorTextOpt = speciesDto.getFlavorTextEntries().stream()
                    .filter(ft -> ft.getLanguage() != null && "zh-Hans".equals(ft.getLanguage().get("name")))
                    .map(ft -> ft.getFlavorText().replace("\n", " ").replace("\f", " "))
                    .findFirst();
        }
        
        // 设置描述文本
        flavorTextOpt.ifPresent(pokemon::setFlavorText);

        // 处理宝可梦属性类型
        pokemonDto.getTypes().forEach(typeEntry -> {
            String typeEn = typeEntry.getType().get("name");
            String typeCn = TYPE_TRANSLATION_MAP.getOrDefault(typeEn, typeEn);
            
            // 设置主属性和副属性
            if (typeEntry.getSlot() == 1) {
                pokemon.setType1(typeEn);
                pokemon.setType1Cn(typeCn);
            } else if (typeEntry.getSlot() == 2) {
                pokemon.setType2(typeEn);
                pokemon.setType2Cn(typeCn);
            }
        });
        
        // 处理宝可梦种族值
        pokemonDto.getStats().forEach(statEntry -> {
            switch (statEntry.getStat().get("name")) {
                case "hp" -> pokemon.setHp(statEntry.getBaseStat());
                case "attack" -> pokemon.setAttack(statEntry.getBaseStat());
                case "defense" -> pokemon.setDefense(statEntry.getBaseStat());
                case "special-attack" -> pokemon.setSpecialAttack(statEntry.getBaseStat());
                case "special-defense" -> pokemon.setSpecialDefense(statEntry.getBaseStat());
                case "speed" -> pokemon.setSpeed(statEntry.getBaseStat());
            }
        });
        
        return pokemon;
    }

    /**
     * 同步宝可梦特殊形态数据
     * 根据形态ID和已有的物种数据，同步宝可梦的特殊形态
     * 例如：阿罗拉形态、伽勒尔形态、超极巨化形态等
     *
     * @param formId 特殊形态ID
     * @param speciesDto 物种数据DTO，包含名称和描述等共享信息
     */
    @Override
    @Transactional
    public void syncPokemonForm(int formId, PokemonSpeciesApiDto speciesDto) {
        // 对于特殊形态，只需要请求pokemon基础数据，物种数据可以直接复用传入的参数
        PokemonApiDto pokemonDto = restTemplate.getForObject("https://pokeapi.co/api/v2/pokemon/" + formId, PokemonApiDto.class);
        if (pokemonDto == null || speciesDto == null) return;

        // 复用buildPokemonEntity方法来构建宝可梦实体
        Pokemon pokemon = buildPokemonEntity(pokemonDto, speciesDto);
        
        // 特殊形态数据都是全新的，直接保存
        this.save(pokemon); 
        log.info("成功同步特殊形态: #{} {}", pokemon.getId(), pokemon.getNameCn());

        // 同步该特殊形态可学习的技能
        syncMovesForPokemon(pokemonDto);

        // 特殊形态与其基础形态共享同一个进化链，无需单独同步进化链数据
    }
}