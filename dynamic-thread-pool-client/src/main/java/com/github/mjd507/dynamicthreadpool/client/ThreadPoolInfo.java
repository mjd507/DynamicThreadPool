package com.github.mjd507.dynamicthreadpool.client;

import lombok.Data;

import java.util.concurrent.ExecutorService;

/**
 * Created by mjd on 2020/6/10 16:42
 */
@Data
public class ThreadPoolInfo {

    private ExecutorService executorService;

    private int activeCount;
}
