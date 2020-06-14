package com.github.mjd507.dynamicthreadpool.admin.controller;

import com.github.mjd507.dynamicthreadpool.client.DynamicExecutors;
import com.github.mjd507.util.http.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;

/**
 * Created by mjd on 2020/6/14 21:47
 */
@RestController
@RequestMapping("test")
public class TestController {

    ExecutorService accountPool = DynamicExecutors.newFixedThreadPool("AccountPool", 3);

    @GetMapping("executor_info")
    public ApiResponse getExecutorInfo() {
        return ApiResponse.ok(accountPool.toString());
    }

    @GetMapping("execute")
    public ApiResponse exec() {
        accountPool.execute(()->{
            while (true) {

            }
        });
        return ApiResponse.ok();
    }
}
