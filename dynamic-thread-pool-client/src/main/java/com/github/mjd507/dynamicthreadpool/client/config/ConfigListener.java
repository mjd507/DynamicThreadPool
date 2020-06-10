package com.github.mjd507.dynamicthreadpool.client.config;

/**
 * Created by mjd on 2020/6/9 23:39
 */
public interface ConfigListener {

    void changed(String poolName, PoolConfig poolConfig);
}
