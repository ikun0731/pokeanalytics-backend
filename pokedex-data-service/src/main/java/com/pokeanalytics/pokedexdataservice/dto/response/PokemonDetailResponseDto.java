package com.pokeanalytics.pokedexdataservice.dto.response;

import com.pokeanalytics.pokedexdataservice.entity.Pokemon;
import com.pokeanalytics.pokedexdataservice.util.TranslationUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 宝可梦详细信息数据传输对象
 * 包含宝可梦的完整详细信息
 */
@Data
public class PokemonDetailResponseDto {
    /**
     * 宝可梦ID
     */
    private Integer id;
    
    /**
     * 宝可梦中文名称
     */
    private String nameCn;
    
    /**
     * 宝可梦英文名称
     */
    private String nameEn;
    
    /**
     * 宝可梦形态名称
     */
    private String formName;
    
    /**
     * 宝可梦属性列表
     */
    private List<String> types;
    
    /**
     * 宝可梦描述文本
     */
    private String flavorText;
    
    /**
     * 宝可梦图片资源
     */
    private PokemonImagesDto sprites;
    
    /**
     * 宝可梦基础能力值列表
     * 顺序为：[HP, 攻击, 防御, 特攻, 特防, 速度]
     */
    private List<Integer> baseStats;
    
    /**
     * 宝可梦详细资料
     */
    private PokemonProfileDto profile;
    
    /**
     * 宝可梦进化链
     */
    private List<EvolutionStepDto> evolutionChain;
    
    /**
     * 宝可梦其他形态列表
     */
    private List<RelatedFormDto> otherForms;

    /**
     * 将宝可梦实体转换为详情DTO
     *
     * @param pokemon 宝可梦实体
     * @return 宝可梦详情DTO
     */
    public static PokemonDetailResponseDto fromEntity(Pokemon pokemon) {
        if (pokemon == null) {
            return null;
        }

        PokemonDetailResponseDto dto = new PokemonDetailResponseDto();
        dto.setId(pokemon.getId());
        dto.setNameCn(pokemon.getNameCn());
        dto.setNameEn(pokemon.getNameEn());
        dto.setFormName(pokemon.getFormName());
        dto.setFlavorText(pokemon.getFlavorText());

        // 设置宝可梦属性
        dto.setTypes(buildTypesList(pokemon));
        
        // 设置宝可梦图片
        dto.setSprites(buildImagesDto(pokemon));
        
        // 设置基础能力值
        dto.setBaseStats(buildBaseStats(pokemon));

        // 设置宝可梦详细资料
        PokemonProfileDto profile = buildProfileDto(pokemon);
        dto.setProfile(profile);

        return dto;
    }
    
    /**
     * 构建宝可梦属性列表
     *
     * @param pokemon 宝可梦实体
     * @return 属性列表
     */
    private static List<String> buildTypesList(Pokemon pokemon) {
        if (pokemon.getType2() != null && !pokemon.getType2().isEmpty()) {
            return Arrays.asList(pokemon.getType1Cn(), pokemon.getType2Cn());
        } else {
            return Collections.singletonList(pokemon.getType1Cn());
        }
    }
    
    /**
     * 构建宝可梦图片DTO
     *
     * @param pokemon 宝可梦实体
     * @return 图片DTO
     */
    private static PokemonImagesDto buildImagesDto(Pokemon pokemon) {
        PokemonImagesDto images = new PokemonImagesDto();
        images.setHd(pokemon.getSpriteHd());
        images.setHdShiny(pokemon.getSpriteHdShiny());
        images.setPixel(pokemon.getSpritePixel());
        images.setPixelShiny(pokemon.getSpritePixelShiny());
        return images;
    }
    
    /**
     * 构建宝可梦基础能力值列表
     *
     * @param pokemon 宝可梦实体
     * @return 基础能力值列表
     */
    private static List<Integer> buildBaseStats(Pokemon pokemon) {
        return Arrays.asList(
                pokemon.getHp(),
                pokemon.getAttack(),
                pokemon.getDefense(),
                pokemon.getSpecialAttack(),
                pokemon.getSpecialDefense(),
                pokemon.getSpeed()
        );
    }
    
    /**
     * 构建宝可梦详细资料DTO
     *
     * @param pokemon 宝可梦实体
     * @return 详细资料DTO
     */
    private static PokemonProfileDto buildProfileDto(Pokemon pokemon) {
        PokemonProfileDto profile = new PokemonProfileDto();
        
        // 设置基本数据
        profile.setHeight(pokemon.getHeight().doubleValue());
        profile.setWeight(pokemon.getWeight().doubleValue());
        profile.setCaptureRate(pokemon.getCaptureRate());
        profile.setHatchSteps(pokemon.getHatchSteps());
        profile.setBaseExperience(pokemon.getBaseExperience());
        
        // 设置特性列表占位符
        profile.setAbilities(Collections.emptyList());
        
        // 设置蛋组
        profile.setEggGroups(buildEggGroups(pokemon));
        
        // 设置努力值
        profile.setEvYield(TranslationUtils.formatEvYield(pokemon.getEvYield()));
        
        // 设置性别比例
        profile.setGenderRatio(formatGenderRatio(pokemon.getGenderRate()));
        
        return profile;
    }
    
    /**
     * 构建宝可梦蛋组列表
     *
     * @param pokemon 宝可梦实体
     * @return 蛋组列表
     */
    private static List<String> buildEggGroups(Pokemon pokemon) {
        List<String> eggGroups = new ArrayList<>();
        if (pokemon.getEggGroup1() != null) {
            eggGroups.add(TranslationUtils.translateEggGroup(pokemon.getEggGroup1()));
        }
        if (pokemon.getEggGroup2() != null) {
            eggGroups.add(TranslationUtils.translateEggGroup(pokemon.getEggGroup2()));
        }
        return eggGroups;
    }
    
    /**
     * 格式化性别比例
     *
     * @param genderRate 性别比例值
     * @return 格式化后的性别比例字符串
     */
    private static String formatGenderRatio(int genderRate) {
        if (genderRate == -1) {
            return "无性别";
        }
        if (genderRate == 8) {
            return "仅雌性";
        }
        if (genderRate == 0) {
            return "仅雄性";
        }
        // genderRate表示雌性概率为1/8的倍数
        double femalePercentage = (double) genderRate / 8 * 100;
        double malePercentage = 100 - femalePercentage;
        return String.format("♂ %.1f%% / ♀ %.1f%%", malePercentage, femalePercentage);
    }
}