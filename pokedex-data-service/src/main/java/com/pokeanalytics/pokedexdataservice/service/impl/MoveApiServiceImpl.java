package com.pokeanalytics.pokedexdataservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pokeanalytics.pokedexdataservice.dto.projection.MoveLearnerProjection;
import com.pokeanalytics.pokedexdataservice.dto.response.*;
import com.pokeanalytics.pokedexdataservice.entity.Move;
import com.pokeanalytics.pokedexdataservice.mapper.MoveMapper;
import com.pokeanalytics.pokedexdataservice.service.MoveApiService;
import com.pokeanalytics.pokedexdataservice.util.TranslationUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 宝可梦技能服务实现类
 */
@Service
public class MoveApiServiceImpl extends ServiceImpl<MoveMapper, Move> implements MoveApiService {
    private final MoveMapper moveMapper;

    /**
     * 构造函数
     *
     * @param moveMapper 技能数据访问对象
     */
    public MoveApiServiceImpl(MoveMapper moveMapper) {
        this.moveMapper = moveMapper;
    }

    /**
     * 分页查询技能列表
     *
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键字 (可选)
     * @return 封装好的分页结果DTO
     */
    @Override
    public PageResultDto<MoveListItemDto> getMoveList(long pageNum, long pageSize, String keyword) {
        // 创建分页对象
        Page<Move> page = new Page<>(pageNum, pageSize);

        // 执行分页查询
        Page<Move> movePage = moveMapper.findMoveList(page, keyword);

        // 将实体对象转换为DTO对象
        List<MoveListItemDto> dtoList = movePage.getRecords().stream().map(item -> {
            MoveListItemDto dto = new MoveListItemDto();
            dto.setId(item.getId());
            dto.setNameCn(item.getNameCn());
            dto.setNameEn(item.getNameEn());
            dto.setTypeCn(item.getTypeCn());
            dto.setDamageClassCn(item.getDamageClassCn());
            dto.setPp(item.getPp());
            dto.setPower(item.getPower());
            dto.setAccuracy(item.getAccuracy());
            return dto;
        }).collect(Collectors.toList());

        // 封装分页结果
        PageResultDto<MoveListItemDto> pageResult = new PageResultDto<>();
        pageResult.setTotal(movePage.getTotal());
        pageResult.setTotalPages(movePage.getPages());
        pageResult.setItems(dtoList);

        return pageResult;
    }
    
    /**
     * 获取技能详情，包含能学会该技能的宝可梦列表
     *
     * @param idOrName 技能的ID或名称
     * @return 技能详情DTO
     */
    @Override
    public MoveDetailDto getMoveDetail(String idOrName) {
        // 构建查询条件
        QueryWrapper<Move> queryWrapper = new QueryWrapper<>();
        if (NumberUtils.isCreatable(idOrName)) {
            // 如果是数字，按ID查询
            queryWrapper.eq("id", Integer.parseInt(idOrName));
        } else {
            // 否则按中文名查询
            queryWrapper.eq("name_cn", idOrName);
        }
        
        // 查询技能信息
        Move move = this.getOne(queryWrapper);
        if (move == null) {
            return null;
        }

        // 使用工厂方法创建技能详情DTO
        MoveDetailDto moveDetailDto = MoveDetailDto.fromEntity(move);

        // 查询能学会此技能的宝可梦
        List<MoveLearnerProjection> learners = moveMapper.findPokemonLearnersByMoveId(move.getId());
        if (learners.isEmpty()) {
            // 如果没有宝可梦能学，返回一个空对象
            moveDetailDto.setLearnedBy(new LearnedByPokemonDto());
            return moveDetailDto;
        }

        // 按学习方式对宝可梦进行分组
        Map<String, List<MoveLearnerProjection>> groupedLearners = learners.stream()
                .collect(Collectors.groupingBy(MoveLearnerProjection::getLearnMethod));

        // 填充各种学习方式的宝可梦列表
        LearnedByPokemonDto learnedByDto = new LearnedByPokemonDto();
        learnedByDto.setByLevelUp(convertToLearnerDtoList(groupedLearners.get("level-up")));
        learnedByDto.setByMachine(convertToLearnerDtoList(groupedLearners.get("machine")));
        learnedByDto.setByTutor(convertToLearnerDtoList(groupedLearners.get("tutor")));
        learnedByDto.setByEgg(convertToLearnerDtoList(groupedLearners.get("egg")));

        moveDetailDto.setLearnedBy(learnedByDto);
        return moveDetailDto;
    }

    /**
     * 将投影对象列表转换为DTO对象列表
     * 
     * @param projections 投影对象列表
     * @return 宝可梦学习者DTO列表
     */
    private List<PokemonLearnerDto> convertToLearnerDtoList(List<MoveLearnerProjection> projections) {
        if (projections == null || projections.isEmpty()) {
            return Collections.emptyList();
        }
        return projections.stream().map(proj -> {
            PokemonLearnerDto dto = new PokemonLearnerDto();
            dto.setPokemonId(proj.getPokemonId());
            dto.setPokemonNameCn(proj.getPokemonNameCn());
            dto.setSpritePixel(proj.getSpritePixel());
            dto.setLevelLearnedAt(proj.getLevelLearnedAt());
            dto.setLastLearnGeneration(TranslationUtils.translateVersion(proj.getLastLearnGeneration()));
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 根据名称列表批量获取技能信息
     *
     * @param names 技能名称列表
     * @return 技能信息列表
     */
    @Override
    public List<MoveListItemDto> findMovesByNames(List<String> names) {
        if (names == null || names.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 根据英文名称列表查询技能
        List<Move> moves = this.list(new QueryWrapper<Move>().in("name_en", names));

        // 转换为DTO对象
        return moves.stream().map(move -> {
            MoveListItemDto dto = new MoveListItemDto();
            dto.setId(move.getId());
            dto.setNameCn(move.getNameCn());
            dto.setNameEn(move.getNameEn());
            dto.setTypeCn(move.getTypeCn());
            dto.setDamageClassCn(move.getDamageClassCn());
            dto.setPower(move.getPower());
            dto.setAccuracy(move.getAccuracy());
            dto.setPp(move.getPp());
            return dto;
        }).collect(Collectors.toList());
    }
}