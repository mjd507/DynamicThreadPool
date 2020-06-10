package com.github.mjd507.dynamicthreadpool.client.config;

import com.github.mjd507.configcenter.client.core.CuratorManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * 配置网关
 * <p>
 * 1. 从配置中心拿数据
 * 2. 接受配置中心数据改变通知
 * Created by mjd on 2020/6/9 23:09
 */
public class ConfigGateway {

    private ConfigGateway() {
    }

    private static final ConfigGateway instance = new ConfigGateway();

    public static ConfigGateway getInstance() {
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigGateway.class);

    private static final CuratorManager configManager;

    private static final Set<String> configKeys = new HashSet<>();

    private static final Set<ConfigListener> configListeners = new HashSet<>();

    static {
        configManager = new CuratorManager();
        configManager.setNameSpace("thread.pool");
        configManager.setConnectString("localhost:2181");
        configManager.init();
    }

    static {
        configManager.setGlobalChangeListener((poolName, newVal) -> {
            if (!configKeys.contains(poolName)) {
                return;
            }
            LOGGER.info(String.format("全局监听到线程池配置变更, 线程池名: %s, 新值: %s", poolName, newVal));
            PoolConfig poolConfig = PoolConfig.decode((String) newVal);
            if (poolConfig == null) {
                throw new RuntimeException(String.format("线程池配置获取失败, 线程池名: %s", poolName));
            }
            for (ConfigListener configListener : configListeners) {
                configListener.changed(poolName, poolConfig);
            }
        });
    }

    /**
     * 从配置中心获取指定线程池配置
     */
    public PoolConfig get(String poolName) {
        configKeys.add(poolName);
        if (StringUtils.isBlank(poolName)) {
            throw new IllegalArgumentException(String.format("线程池名非法, poolName: %s", poolName));
        }
        String configJson = configManager.get(poolName);
        if (StringUtils.isBlank(configJson)) {
            return null;
        }
        return PoolConfig.decode(configJson);
    }

    /**
     * 注册监听器，监听线程改变通知
     */
    public void registerListener(ConfigListener configListener) {
        configListeners.add(configListener);
    }
}
