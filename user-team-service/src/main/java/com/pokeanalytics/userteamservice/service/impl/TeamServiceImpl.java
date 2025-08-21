package com.pokeanalytics.userteamservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pokeanalytics.userteamservice.common.ResultVO;
import com.pokeanalytics.userteamservice.dto.analysis.TypeAnalysisDto;
import com.pokeanalytics.userteamservice.dto.feign.PokemonDetailFeignDto;
import com.pokeanalytics.userteamservice.dto.team.*;
import com.pokeanalytics.userteamservice.entity.Team;
import com.pokeanalytics.userteamservice.entity.TeamMember;
import com.pokeanalytics.userteamservice.entity.User;
import com.pokeanalytics.userteamservice.exception.ForbiddenAccessException;
import com.pokeanalytics.userteamservice.feign.PokedexDataClient;
import com.pokeanalytics.userteamservice.mapper.TeamMapper;
import com.pokeanalytics.userteamservice.mapper.TeamMemberMapper;
import com.pokeanalytics.userteamservice.mapper.UserMapper;
import com.pokeanalytics.userteamservice.service.TeamAnalysisService;
import com.pokeanalytics.userteamservice.service.TeamFormatService;
import com.pokeanalytics.userteamservice.service.TeamParserService;
import com.pokeanalytics.userteamservice.service.TeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class TeamServiceImpl implements TeamService {

    private final UserMapper userMapper;
    private final TeamMapper teamMapper;
    private final TeamMemberMapper teamMemberMapper;
    private final PokedexDataClient pokedexDataClient;
    private final TeamParserService teamParserService;
    private final TeamFormatService teamFormatService;
    private final TeamAnalysisService teamAnalysisService;

    public TeamServiceImpl(UserMapper userMapper, TeamMapper teamMapper, TeamMemberMapper teamMemberMapper, PokedexDataClient pokedexDataClient, TeamParserService teamParserService, TeamFormatService teamFormatService, TeamAnalysisService teamAnalysisService) {
        this.userMapper = userMapper;
        this.teamMapper = teamMapper;
        this.teamMemberMapper = teamMemberMapper;
        this.pokedexDataClient = pokedexDataClient;
        this.teamParserService = teamParserService;
        this.teamFormatService = teamFormatService;
        this.teamAnalysisService = teamAnalysisService;
    }

    /**
     * 创建宝可梦队伍
     *
     * @param requestDto 包含队伍详情的请求对象，包括队伍名称、描述、格式和队员信息
     * @param username 当前用户名
     * @return 创建成功的队伍实体
     * @throws UsernameNotFoundException 当用户不存在时抛出此异常
     */
    @Override
    @Transactional
    public Team createTeam(CreateTeamRequestDto requestDto, String username) {
        // 1. 根据用户名查找用户ID
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 2. 创建并保存Team实体
        Team team = new Team();
        team.setUserId(user.getId());
        team.setTeamName(requestDto.getTeamName());
        team.setDescription(requestDto.getDescription());
        team.setFormat(requestDto.getFormat());
        teamMapper.insert(team); // 插入后，team对象的id字段会被自动填充

        // 3. 遍历并保存TeamMember实体
        for (int i = 0; i < requestDto.getMembers().size(); i++) {
            TeamMemberDto memberDto = requestDto.getMembers().get(i);
            TeamMember teamMember = new TeamMember();

            // 使用BeanUtils复制属性，简化代码
            BeanUtils.copyProperties(memberDto, teamMember);

            teamMember.setTeamId(team.getId()); // 关联到刚刚创建的Team
            teamMember.setPosition(i); // 设置在队伍中的位置

            teamMemberMapper.insert(teamMember);
        }
        return team;
    }

    /**
     * 根据用户名获取所有宝可梦队伍摘要
     * 
     * @param username 用户名
     * @return 该用户拥有的所有队伍摘要列表
     */
    @Override
    public List<TeamSummaryDto> getTeamsByUsername(String username) {

        // 查找用户
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) { 
            return Collections.emptyList(); 
        }
        
        // 获取用户的所有队伍
        List<Team> teams = teamMapper.selectList(new QueryWrapper<Team>().eq("user_id", user.getId()).orderByDesc("updated_at"));
        if (teams.isEmpty()) { 
            return Collections.emptyList(); 
        }
        
        // 提取队伍ID
        List<Long> teamIds = teams.stream().map(Team::getId).collect(Collectors.toList());
        List<TeamMember> allMembers = teamMemberMapper.selectList(new QueryWrapper<TeamMember>().in("team_id", teamIds));
        if (allMembers.isEmpty()) { 
            return teams.stream().map(team -> {
                TeamSummaryDto dto = new TeamSummaryDto();
                BeanUtils.copyProperties(team, dto);
                dto.setMemberSprites(Collections.emptyList());
                return dto;
            }).collect(Collectors.toList());
        }

        Set<String> pokemonNamesToFetch = allMembers.stream().map(TeamMember::getPokemonNameEn).collect(Collectors.toSet());


        Map<String, PokemonDetailFeignDto> pokemonDetailsMap = new HashMap<>();
        if (!pokemonNamesToFetch.isEmpty()) {
            ResultVO<Map<String, PokemonDetailFeignDto>> result = pokedexDataClient.getPokemonListByNames(new ArrayList<>(pokemonNamesToFetch));
            if (result != null && result.getCode() == 200 && result.getData() != null) {
                pokemonDetailsMap = result.getData();
            }
        }

        final Map<String, PokemonDetailFeignDto> finalPokemonDetailsMap = pokemonDetailsMap;
        Map<Long, List<TeamMember>> membersByTeamId = allMembers.stream().collect(Collectors.groupingBy(TeamMember::getTeamId));

        return teams.stream().map(team -> {
            TeamSummaryDto dto = new TeamSummaryDto();
            BeanUtils.copyProperties(team, dto);
            List<TeamMember> currentMembers = membersByTeamId.getOrDefault(team.getId(), Collections.emptyList());
            List<String> sprites = currentMembers.stream()
                    .map(member -> {
                        PokemonDetailFeignDto details = finalPokemonDetailsMap.get(member.getPokemonNameEn());
                        return (details != null) ? details.getSpritePixel() : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            dto.setMemberSprites(sprites);
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 获取队伍详情
     * 
     * @param teamId 队伍ID
     * @param username 当前用户名
     * @return 队伍详情DTO对象，包含队伍基本信息和成员详情
     * @throws ForbiddenAccessException 当用户无权访问该队伍时抛出此异常
     */
    @Override
    public TeamDetailDto getTeamById(Long teamId, String username) {
        Team team = teamMapper.selectById(teamId);
        if (team == null) { return null; }
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (!team.getUserId().equals(user.getId())) { throw new ForbiddenAccessException("无权访问该队伍"); }
        List<TeamMember> members = teamMemberMapper.selectList(new QueryWrapper<TeamMember>().eq("team_id", teamId));

        Set<String> pokemonNamesToFetch = members.stream().map(TeamMember::getPokemonNameEn).collect(Collectors.toSet());


        Map<String, PokemonDetailFeignDto> pokemonDetailsMap = new HashMap<>();
        if (!pokemonNamesToFetch.isEmpty()) {
            ResultVO<Map<String, PokemonDetailFeignDto>> result = pokedexDataClient.getPokemonListByNames(new ArrayList<>(pokemonNamesToFetch));
            if (result != null && result.getCode() == 200 && result.getData() != null) {
                pokemonDetailsMap = result.getData();
            }
        }

        TeamDetailDto dto = new TeamDetailDto();
        BeanUtils.copyProperties(team, dto);

        final Map<String, PokemonDetailFeignDto> finalPokemonDetailsMap = pokemonDetailsMap;
        List<TeamMemberDetailDto> memberDetails = members.stream().map(member -> {
            TeamMemberDetailDto memberDto = new TeamMemberDetailDto();
            BeanUtils.copyProperties(member, memberDto);
            PokemonDetailFeignDto details = finalPokemonDetailsMap.get(member.getPokemonNameEn());
            if (details != null) {
                memberDto.setSpritePixel(details.getSpritePixel());
            }
            return memberDto;
        }).collect(Collectors.toList());

        dto.setMembers(memberDetails);
        return dto;
    }
    /**
     * 更新队伍信息
     * 
     * @param teamId 队伍ID
     * @param requestDto 包含队伍更新信息的请求对象
     * @param username 当前用户名
     * @return 更新后的队伍实体
     * @throws RuntimeException 当队伍不存在时抛出此异常
     * @throws ForbiddenAccessException 当用户无权修改该队伍时抛出此异常
     */
    @Override
    @Transactional
    public Team updateTeam(Long teamId, CreateTeamRequestDto requestDto, String username) {
        // 1. 权限校验：确保该队伍属于当前用户
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        Team team = teamMapper.selectById(teamId);

        if (team == null) {
            // 或者可以抛出一个自定义的 TeamNotFoundException
            throw new RuntimeException("队伍不存在");
        }
        if (!team.getUserId().equals(user.getId())) {
            throw new ForbiddenAccessException("无权修改该队伍");
        }

        // 2. 更新队伍本身的信息 (名称, 描述等)
        team.setTeamName(requestDto.getTeamName());
        team.setDescription(requestDto.getDescription());
        team.setFormat(requestDto.getFormat());
        teamMapper.updateById(team);

        // 先删除该队伍的所有旧成员，然后添加新成员，实现完整替换
        teamMemberMapper.delete(new QueryWrapper<TeamMember>().eq("team_id", teamId));

        // 4. 插入所有新的成员 (逻辑与createTeam类似)
        for (int i = 0; i < requestDto.getMembers().size(); i++) {
            TeamMemberDto memberDto = requestDto.getMembers().get(i);
            TeamMember teamMember = new TeamMember();
            BeanUtils.copyProperties(memberDto, teamMember);
            teamMember.setTeamId(team.getId());
            teamMember.setPosition(i);
            teamMemberMapper.insert(teamMember);
        }

        return team;
    }

    /**
     * 删除队伍
     * 
     * @param teamId 队伍ID
     * @param username 当前用户名
     * @throws RuntimeException 当队伍不存在时抛出此异常
     * @throws ForbiddenAccessException 当用户无权删除该队伍时抛出此异常
     */
    @Override
    public void deleteTeam(Long teamId, String username) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new RuntimeException("队伍不存在");
        }
        if (!team.getUserId().equals(user.getId())) {
            throw new ForbiddenAccessException("无权删除该队伍");
        }
        teamMemberMapper.delete(new QueryWrapper<TeamMember>().eq("team_id", teamId));
        teamMapper.deleteById(teamId);
    }
    /**
     * 导入队伍
     * 
     * @param requestDto 包含导入信息的请求对象，包括队伍文本、名称、描述和格式等
     * @param username 当前用户名
     * @return 导入成功的队伍实体
     */
    @Override
    public Team importTeam(ImportTeamRequestDto requestDto, String username) {
        // 1. 调用解析器服务，将文本转换为标准的CreateTeamRequestDto
        CreateTeamRequestDto createDto = teamParserService.parse(requestDto.getPasteText());

        // 2. 如果用户在导入时提供了队伍名等信息，就使用它们
        if (StringUtils.hasText(requestDto.getTeamName())) {
            createDto.setTeamName(requestDto.getTeamName());
        } else {
            // 否则，我们可以给一个默认名字
            createDto.setTeamName("导入的队伍 - " + System.currentTimeMillis());
        }
        createDto.setDescription(requestDto.getDescription());
        createDto.setFormat(requestDto.getFormat());

        // 3. 【完美复用】直接调用已有的createTeam方法完成所有数据库操作
        return this.createTeam(createDto, username);
    }
    /**
     * 导出队伍为文本格式
     * 
     * @param teamId 队伍ID
     * @param username 当前用户名
     * @return 格式化后的队伍文本
     * @throws RuntimeException 当队伍不存在或用户无权访问时抛出此异常
     */
    @Override
    public String exportTeam(Long teamId, String username) {
        // 1. 【完美复用】调用已有方法获取队伍详情并完成权限校验
        TeamDetailDto teamDto = this.getTeamById(teamId, username);
        if (teamDto == null) {
            throw new RuntimeException("队伍不存在或无权访问"); // 或者自定义TeamNotFoundException
        }

        // 2. 调用格式化服务生成文本
        return teamFormatService.formatToText(teamDto);
    }
    /**
     * 分析队伍的属性弱点
     * 
     * @param teamId 队伍ID
     * @param username 当前用户名
     * @return 队伍的属性分析结果列表
     */
    @Override
    public List<TypeAnalysisDto> analyzeTeam(Long teamId, String username) {
        // 复用方法获取队伍详情和校验权限
        TeamDetailDto teamDto = this.getTeamById(teamId, username);
        // 调用分析服务进行计算
        return teamAnalysisService.analyzeTeamTypeWeakness(teamDto);
    }
}