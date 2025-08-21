package com.pokeanalytics.userteamservice.interceptor;

import com.pokeanalytics.userteamservice.util.JwtUtils;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求速率限制拦截器
 * 用于控制用户对AI分析接口的访问频率，防止滥用
 */
@Slf4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();

    public RateLimitInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * 请求预处理方法，在控制器方法执行前调用
     * 检查用户的请求频率是否超过限制
     *
     * @param request 当前HTTP请求
     * @param response HTTP响应
     * @param handler 当前处理程序
     * @return 如果请求未超过频率限制则返回true，否则返回false
     * @throws Exception 如果处理过程中发生异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        // 如果token不存在或格式不正确，让Spring Security处理认证逻辑
        if (token == null || !token.startsWith("Bearer ")) {
            return true; // 放行，让 Spring Security 决定是否拦截
        }

        String jwt = token.substring(7);
        String username = jwtUtils.getUsernameFromToken(jwt);

        return checkRateLimit(username, response);
    }

    /**
     * 检查用户请求是否超过速率限制
     *
     * @param key 用户标识（通常是用户名）
     * @param response HTTP响应，用于返回限制信息
     * @return 如果未超过限制返回true，否则返回false
     * @throws IOException 如果写入响应时发生IO异常
     */
    private boolean checkRateLimit(String key, HttpServletResponse response) throws IOException {
        // 根据用户名获取或创建对应的令牌桶
        Bucket bucket = userBuckets.computeIfAbsent(key, this::createNewDailyBucket);

        // 尝试从桶中消费一个令牌
        if (bucket.tryConsume(1)) {
            // 成功消费，放行请求
            return true;
        } else {
            // 令牌不足，拒绝请求并返回明确的错误信息
            log.warn("Daily rate limit exceeded for user: {}", key);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 返回 429 状态码
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\": \"AI分析请求已达今日上限 (20次)，请明天再试。\"}");
            return false;
        }
    }

    /**
     * 为用户创建新的限流令牌桶
     * 
     * @param key 用户标识（通常是用户名）
     * @return 配置为每天20次请求限制的令牌桶
     */
    private Bucket createNewDailyBucket(String key) {
        // 设置速率限制为每天20次请求
        // greedy模式表示每24小时一次性补充所有令牌
        Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofDays(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}