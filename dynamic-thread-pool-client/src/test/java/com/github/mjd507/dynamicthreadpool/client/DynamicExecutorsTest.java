package com.github.mjd507.dynamicthreadpool.client;

import org.junit.Test;

import java.util.concurrent.ExecutorService;

/**
 * Created by mjd on 2020/6/11 09:44
 */
public class DynamicExecutorsTest {

    @Test
    public void test() {
        ExecutorService executorService = DynamicExecutors.newFixedThreadPool("task1", 3);
        System.out.println(executorService);
    }
}
