package com.meetchance.permission.security;

import com.meetchance.permission.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.prefix}")
    private String prefix;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader(header);
        
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(prefix.trim())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = authHeader.substring(prefix.length()).trim();
        
        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        String redisKey = "login:token:" + userId;
        String storedToken = (String) redisTemplate.opsForValue().get(redisKey);
        
        if (!token.equals(storedToken)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String username = jwtUtil.getUsernameFromToken(token);
        
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        
        filterChain.doFilter(request, response);
    }
}
