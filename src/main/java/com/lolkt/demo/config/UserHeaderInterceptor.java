package com.lolkt.demo.config;

/**
 * @author lolkt
 * @description UserHeaderInterceptor
 * @date 2023/9/12 17:55
 **/

import cn.hutool.core.util.StrUtil;
import com.lolkt.demo.util.DataSourceRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lolkt
 * @description UserHeaderInterceptor
 * @date 2023/9/12 17:55
 **/

@Slf4j
@RequiredArgsConstructor
public class UserHeaderInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String headerValue = request.getHeader("isTestUser");

        if (StrUtil.isNotBlank(headerValue)) {
            //说明是测试账户
            if (headerValue.equals("true")) {
                log.info("request uri:{},isTestUser:{}", request.getRequestURI(), headerValue);
                //设置走测试库
                DataSourceRouter.setTestUser(true);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        DataSourceRouter.clearTestUser();
    }
}

