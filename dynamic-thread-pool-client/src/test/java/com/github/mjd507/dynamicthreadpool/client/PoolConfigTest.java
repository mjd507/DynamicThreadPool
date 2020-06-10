package com.github.mjd507.dynamicthreadpool.client;

import com.github.mjd507.dynamicthreadpool.client.config.PoolConfig;
import org.junit.Test;

/**
 * Created by mjd on 2020/6/9 23:02
 */
public class PoolConfigTest {

    @Test
    public void test() {
        PoolConfig defaultConfig = PoolConfig.defaultConfig();
        String encode = PoolConfig.encode(defaultConfig);
        System.out.println(encode);
        System.out.println(PoolConfig.decode(encode));
    }
}
