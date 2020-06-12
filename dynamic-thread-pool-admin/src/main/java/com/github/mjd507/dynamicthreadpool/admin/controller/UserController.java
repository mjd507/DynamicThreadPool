package com.github.mjd507.dynamicthreadpool.admin.controller;


import com.github.mjd507.dynamicthreadpool.admin.entity.UserAppRole;
import com.github.mjd507.dynamicthreadpool.admin.service.UserService;
import com.github.mjd507.dynamicthreadpool.admin.util.UserUtils;
import com.github.mjd507.dynamicthreadpool.admin.vo.request.LoginReq;
import com.github.mjd507.util.http.ApiResponse;
import com.github.mjd507.util.util.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by mjd on 2020/4/12 22:49
 */
@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("login")
    public ApiResponse login(@RequestBody LoginReq loginReq) {
        String name = loginReq.getName();
        String pwd = loginReq.getPwd();
        String token = userService.login(name, pwd);
        return ApiResponse.ok(MapUtil.newMap().put("access-token", token));
    }

    @GetMapping("info")
    public ApiResponse info() {
        return ApiResponse.ok(MapUtil.newMap().put("name", UserUtils.getUserName()));
    }

    @GetMapping("apps_with_role")
    public ApiResponse listApps() {
        List<UserAppRole> apps = userService.listUserAppsWithRole(UserUtils.getUid());
        return ApiResponse.ok(apps);
    }

}
