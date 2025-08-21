package com.pokeanalytics.userteamservice.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pokeanalytics.userteamservice.entity.User;
import com.pokeanalytics.userteamservice.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 用户详情服务实现类
 * 实现Spring Security的UserDetailsService接口，
 * 提供根据用户名加载用户详情的功能
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    public UserDetailsServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 根据用户名加载用户详情
     * 
     * @param username 用户名
     * @return Spring Security所需的UserDetails对象
     * @throws UsernameNotFoundException 当用户不存在时抛出此异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 根据用户名查询数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        // 2. 如果用户不存在，必须抛出此异常
        if (user == null) {
            throw new UsernameNotFoundException("用户 '" + username + "' 不存在");
        }

        // 3. 将我们自己的User实体，转换为Spring Security需要的UserDetails对象
        // 第三个参数是权限列表，我们暂时为空
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // 注意：这里是数据库中已加密的密码
                Collections.emptyList()
        );
    }
}