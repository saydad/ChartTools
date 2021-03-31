package com.self.interceptor;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.self.constant.TokenState;
import com.self.service.AuthService;

/**
 * token校验
 * @author liuyong
 */
@Component
public class AuthInterceptor extends OncePerRequestFilter {
    private static final String API_PREFIX = "/api";
    private static final String USER_PREFIX = "/api/user";
    private static final String WEB_SOCKET_PREFIX = "/api/imServer";

    @Resource
    private AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();

        // 非接口操作&&用户相关操作不拦截
        if (!path.startsWith(API_PREFIX)
                || path.startsWith(USER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");
        // webSocket 操作
        if (path.startsWith(WEB_SOCKET_PREFIX)) {
            token = request.getParameter("token");
        }
        int state = authService.authToken(token);

        // 登录过期
        if (state == TokenState.EXPIRE.code) {
            //401 未授权
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        if (state == TokenState.INVALID.code) {
            // 403 访问拒绝
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }
        filterChain.doFilter(request, response);
     }
}
