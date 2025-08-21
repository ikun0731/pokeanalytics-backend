package com.pokeanalytics.userteamservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pokeanalytics.userteamservice.dto.request.*;
import com.pokeanalytics.userteamservice.dto.response.UserResponseDto;
import com.pokeanalytics.userteamservice.entity.User;
import com.pokeanalytics.userteamservice.exception.InvalidCodeException;
import com.pokeanalytics.userteamservice.exception.InvalidPasswordException;
import com.pokeanalytics.userteamservice.exception.UserAlreadyExistsException;
import com.pokeanalytics.userteamservice.mapper.UserMapper;
import com.pokeanalytics.userteamservice.service.EmailService;
import com.pokeanalytics.userteamservice.service.UserService;
import com.pokeanalytics.userteamservice.util.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String LOGIN_CODE_PREFIX = "login-code:";
    private static final String PASSWORD_RESET_CODE_PREFIX = "password-reset-code:";
    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, AuthenticationManager authenticationManager, EmailService emailService, RedisTemplate<String, Object> redisTemplate) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
    }
    /**
     * 用户注册服务
     * 
     * @param requestDto 包含用户注册信息的请求对象，包括用户名、邮箱和密码
     * @throws UserAlreadyExistsException 当用户名已被注册时抛出此异常
     */
    @Override
    public void register(RegisterRequestDto requestDto) {
        // 1. 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", requestDto.getUsername());
        if (userMapper.exists(queryWrapper)) {
            throw new UserAlreadyExistsException("用户名 '" + requestDto.getUsername() + "' 已被注册");
        }

        // 2. 创建实体对象
        User user = new User();
        user.setUsername(requestDto.getUsername());
        user.setEmail(requestDto.getEmail());

        // 对密码进行加密
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        // 4. 插入数据库
        userMapper.insert(user);
    }

    /**
     * 用户登录服务
     * 
     * @param requestDto 包含用户登录信息的请求对象，包括用户名和密码
     * @return 生成的JWT令牌
     */
    @Override
    public String login(LoginRequestDto requestDto) {
        // 使用AuthenticationManager进行认证
        // 这会触发UserDetailsServiceImpl.loadUserByUsername()并比对密码
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword()));
        // 2. 如果认证成功，会返回一个包含UserDetails的Authentication对象
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // 3. 使用jwtUtils为该用户生成JWT
        return jwtUtils.generateToken(userDetails);
    }
    /**
     * 请求密码重置服务
     * 
     * @param email 用户注册的邮箱地址，用于发送重置验证码
     */
    @Override
    public void requestPasswordReset(String email) {
        // 1. 检查邮箱是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User user = userMapper.selectOne(queryWrapper);

        // 安全措施：无论邮箱是否存在，都不给前端明确提示，防止用户枚举攻击
        if (user != null) {
            // 生成6位随机验证码(100000-999999)
            String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

            // 3. 将验证码存入Redis，并设置10分钟有效期
            String redisKey = PASSWORD_RESET_CODE_PREFIX + email;
            redisTemplate.opsForValue().set(redisKey, code, 10, TimeUnit.MINUTES);

            // 4. 发送邮件
            String subject = "【PokeAnalytics】密码重置验证码";
            String text = "您好, " + user.getUsername() + "！\n\n您的密码重置验证码是：" + code + "\n\n该验证码将在10分钟后失效，请尽快使用。如非本人操作，请忽略此邮件。\n\nPokeAnalytics团队";
            emailService.sendSimpleMail(email, subject, text);
        }
    }
    /**
     * 执行密码重置
     * 
     * @param requestDto 包含执行密码重置所需信息的请求对象，包括邮箱、验证码和新密码
     * @throws InvalidCodeException 当验证码无效或过期时抛出此异常
     * @throws UsernameNotFoundException 当用户不存在时抛出此异常
     */
    @Override
    public void performPasswordReset(PerformResetRequestDto requestDto) {
        String email = requestDto.getEmail();
        String redisKey = PASSWORD_RESET_CODE_PREFIX + email;

        // 1. 从Redis中获取验证码
        Object storedCode = redisTemplate.opsForValue().get(redisKey);

        // 2. 验证验证码
        if (storedCode == null) {
            throw new InvalidCodeException("验证码已过期，请重新请求");
        }
        if (!storedCode.toString().equals(requestDto.getCode())) {
            throw new InvalidCodeException("验证码不正确");
        }

        // 3. 验证通过，查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User user = userMapper.selectOne(queryWrapper);

        // 理论上用户一定存在，因为请求时已验证过。但为了代码健壮性，再做一次检查。
        if (user == null) {
            // 这种情况很少见，但可能发生（如用户在请求和重置之间被删除）
            throw new UsernameNotFoundException("用户不存在");
        }

        // 4. 加密新密码并更新数据库
        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        userMapper.updateById(user);

        // 从Redis中删除已使用的验证码，防止重复使用
        redisTemplate.delete(redisKey);
    }
    /**
     * 请求登录验证码
     * 
     * @param email 用户注册的邮箱地址，用于发送登录验证码
     */
    @Override
    public void requestLoginCode(String email) {
        // 复用之前的逻辑，检查用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User user = userMapper.selectOne(queryWrapper);

        // 同样，为了安全，只在用户存在时执行操作
        if (user != null) {
            // 1. 生成6位随机码
            String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

            // 2. 使用新的前缀存入Redis，有效期5分钟
            String redisKey = LOGIN_CODE_PREFIX + email;
            redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);

            // 3. 发送登录验证码邮件
            String subject = "【PokeAnalytics】登录验证码";
            String text = "您好, " + user.getUsername() + "！\n\n您的登录验证码是：" + code + "\n\n该验证码将在5分钟后失效。如非本人操作，请忽略此邮件。\n\nPokeAnalytics团队";
            emailService.sendSimpleMail(email, subject, text);
        }
    }

    /**
     * 使用验证码登录
     * 
     * @param requestDto 包含邮箱和验证码的请求对象
     * @return 生成的JWT令牌
     * @throws InvalidCodeException 当验证码无效或过期时抛出此异常
     * @throws UsernameNotFoundException 当用户不存在时抛出此异常
     */
    @Override
    public String loginWithCode(EmailCodeLoginRequestDto requestDto) {
        String email = requestDto.getEmail();
        String redisKey = LOGIN_CODE_PREFIX + email;

        // 1. 从Redis获取验证码
        Object storedCode = redisTemplate.opsForValue().get(redisKey);

        // 2. 校验验证码
        if (storedCode == null) {
            throw new InvalidCodeException("验证码已过期，请重新请求");
        }
        if (!storedCode.toString().equals(requestDto.getCode())) {
            throw new InvalidCodeException("验证码不正确");
        }

        // 3. 验证成功，查询用户信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            // 正常情况下不会发生，因为请求验证码时已确认过用户存在
            throw new UsernameNotFoundException("用户不存在");
        }

        // 直接为用户生成JWT，无需密码验证
        // 创建UserDetails对象用于生成Token
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                "", // 密码字段为空，因为我们不是用密码登录
                Collections.emptyList()
        );
        String token = jwtUtils.generateToken(userDetails);

        // 删除已使用的验证码
        redisTemplate.delete(redisKey);

        return token;
    }

    /**
     * 获取用户个人信息
     * 
     * @param username 用户名
     * @return 用户个人信息DTO对象
     * @throws UsernameNotFoundException 当用户不存在时抛出此异常
     */
    @Override
    public UserResponseDto getUserProfile(String username) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        UserResponseDto responseDto = new UserResponseDto();
        BeanUtils.copyProperties(user, responseDto);
        return responseDto;
    }


    /**
     * 检查邮箱是否已被其他用户使用
     * 
     * @param email 待检查的邮箱地址
     * @param currentUsername 当前用户的用户名，用于排除自己
     * @return 如果邮箱已被其他用户使用返回true，否则返回false
     */
    @Override
    public boolean isEmailTakenByOtherUser(String email, String currentUsername) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email)
                .ne("username", currentUsername); // 排除当前用户自身
        return userMapper.exists(queryWrapper);
    }


    /**
     * 更新用户邮箱
     * 
     * @param username 用户名
     * @param requestDto 包含当前密码和新邮箱的请求对象
     * @throws UsernameNotFoundException 当用户不存在时抛出此异常
     * @throws InvalidPasswordException 当当前密码不正确时抛出此异常
     * @throws UserAlreadyExistsException 当新邮箱已被其他用户注册时抛出此异常
     */
    @Override
    public void updateEmail(String username, UpdateEmailRequestDto requestDto) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 验证当前密码是否正确
        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("当前密码不正确，无法修改邮箱");
        }

        // 2. 检查新邮箱是否已被其他用户占用
        if (isEmailTakenByOtherUser(requestDto.getEmail(), username)) {
            throw new UserAlreadyExistsException("该邮箱已被其他用户注册");
        }

        // 3. 更新邮箱
        user.setEmail(requestDto.getEmail());
        userMapper.updateById(user);
    }


    /**
     * 修改用户密码
     * 
     * @param username 用户名
     * @param requestDto 包含旧密码和新密码的请求对象
     * @throws UsernameNotFoundException 当用户不存在时抛出此异常
     * @throws InvalidPasswordException 当旧密码不正确时抛出此异常
     */
    @Override
    public void changePassword(String username, ChangePasswordRequestDto requestDto) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 1. 验证旧密码是否正确
        if (!passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("当前密码不正确");
        }

        // 2. 加密并设置新密码
        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        userMapper.updateById(user);
    }
}
