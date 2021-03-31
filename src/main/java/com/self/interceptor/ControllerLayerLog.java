package com.self.interceptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class ControllerLayerLog {

    @Pointcut("execution(*  com.self.controller..*.*(..)) ")
    public void execController() {
    }

    @Around(value = "execController()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            return proceedingJoinPoint.proceed();
        } finally {
            stopWatch.stop();
            requestLog(proceedingJoinPoint, stopWatch.getTime(TimeUnit.MICROSECONDS));
        }
    }

    private void requestLog(ProceedingJoinPoint proceedingJoinPoint, long costTime) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String method = request.getMethod();
            String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            log.info("url: {}, method: {}, cost: {}, headers: {}, cookies: {}, args: {}",
                    path, request.getMethod(), costTime, getHeaderListNoCookie(request), getCookieList(request), getArgMap(proceedingJoinPoint));
        }
    }

    private Map<String, Object> getArgMap(ProceedingJoinPoint proceedingJoinPoint) {
        String[] names = ((MethodSignature) proceedingJoinPoint.getSignature()).getParameterNames();
        Object[] values = proceedingJoinPoint.getArgs();
        Map<String, Object> argMap = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            argMap.put(names[i], values[i]);
        }
        return argMap;
    }

    private List<String> getHeaderListNoCookie(HttpServletRequest request) {
        List<String> headerList = new ArrayList<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            if ("cookie".equals(name.toLowerCase())) {
                continue;
            }
            headerList.add(String.format("%s: %s", name, StringUtils.isBlank(value) ? "" : value));
        }
        return headerList;
    }

    private List<String> getCookieList(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return cookiesToStrList(cookies);
        }
        return Collections.emptyList();
    }

    private List<String> cookiesToStrList(Cookie[] cookies) {
        List<String> cookieList = new ArrayList<>();
        for (Cookie cookie : cookies) {
            try {
                cookieList.add(JSON.toJSONString(cookie));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return cookieList;
    }
}
