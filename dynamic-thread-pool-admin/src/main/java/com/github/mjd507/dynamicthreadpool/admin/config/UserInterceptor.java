package com.github.mjd507.dynamicthreadpool.admin.config;

import com.github.mjd507.dynamicthreadpool.admin.entity.User;
import com.github.mjd507.dynamicthreadpool.admin.service.UserService;
import com.github.mjd507.dynamicthreadpool.admin.util.UserUtils;
import com.github.mjd507.util.http.ApiCode;
import com.github.mjd507.util.http.ApiResponse;
import com.github.mjd507.util.spring.RedisOperation;
import com.github.mjd507.util.util.JsonUtil;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by mjd on 2020/4/13 09:21
 */
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisOperation redisOperation;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setContentType("application/json, charset=utf-8");
        String accessToken = request.getHeader("access-token");
        if (Strings.isNullOrEmpty(accessToken)) {
            response.getWriter().write(JsonUtil.toJsonStr(ApiResponse.error(ApiCode.UNAUTHORISED)));
            return false;
        }
        Integer uId = redisOperation.getInteger(accessToken);
        if (uId == null || uId <= 0) {
            response.getWriter().write(JsonUtil.toJsonStr(ApiResponse.error(ApiCode.UNAUTHORISED)));
            return false;
        }
        User user = userService.getUserById(uId);
        if (user == null) {
            response.getWriter().write(JsonUtil.toJsonStr(ApiResponse.error(ApiCode.UNAUTHORISED)));
            return false;
        }
        UserUtils.setUser(user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserUtils.clear();
    }
}
