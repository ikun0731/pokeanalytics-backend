package com.pokeanalytics.userteamservice.security;

import com.pokeanalytics.userteamservice.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 * 从请求中提取JWT令牌，验证其有效性，并设置Spring Security上下文
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 过滤器内部处理逻辑
     * 提取并验证JWT令牌，设置认证信息到安全上下文
     *
     * @param request 当前HTTP请求
     * @param response HTTP响应
     * @param filterChain 过滤器链
     * @throws ServletException 如果发生Servlet异常
     * @throws IOException 如果发生IO异常
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. 从请求头中获取 "Authorization"
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 2. 如果Header为空或不是以 "Bearer " 开头，则直接放行，让后续过滤器处理
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 提取JWT（去掉 "Bearer " 前缀）
        jwt = authHeader.substring(7);

        // 4. 从JWT中解析出用户名
        username = jwtUtils.getUsernameFromToken(jwt);

        // 5. 核心逻辑：如果用户名存在，且当前安全上下文中没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 根据用户名加载用户信息
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 验证Token是否有效
            if (jwtUtils.validateToken(jwt, userDetails)) {
                // 如果Token有效，则创建一个认证通过的Token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // 密码我们不需要，所以是null
                        userDetails.getAuthorities()
                );
                // 将请求的详细信息（如IP地址、Session ID）设置到认证对象中
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 将认证信息设置到Spring Security的上下文中
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // 6. 无论如何都放行，让请求继续前进
        filterChain.doFilter(request, response);
    }
}