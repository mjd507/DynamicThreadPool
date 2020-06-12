package com.github.mjd507.dynamicthreadpool.admin.controller;


import com.github.mjd507.dynamicthreadpool.admin.service.ConfigService;
import com.github.mjd507.dynamicthreadpool.admin.vo.request.ConfigReq;
import com.github.mjd507.util.http.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by mjd on 2020/4/17 11:39
 */
@RestController
@RequestMapping("config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @GetMapping("list")
    public ApiResponse listAppConfig(@RequestParam Integer appId) {
        Map<String, String> configs = configService.listAppConfig(appId);
        return ApiResponse.ok(configs);
    }

    @PostMapping("write")
    public ApiResponse writeConfig(@RequestBody ConfigReq configReq) {
        boolean success = configService.writeConfig(configReq, configReq.getAppId());
        return ApiResponse.ok(success);
    }

    @GetMapping("delete")
    public ApiResponse writeConfig(@RequestParam Integer appId, @RequestParam String key) {
        boolean success = configService.deleteConfig(appId, key);
        return ApiResponse.ok(success);
    }

}
