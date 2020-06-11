package com.github.mjd507.dynamicthreadpool.client.config;

import com.github.mjd507.dynamicthreadpool.client.enums.QueueType;
import com.github.mjd507.util.util.JsonUtil;
import lombok.Data;

/**
 * 线程池配置
 * Created by mjd on 2020/6/9 22:09
 */
@Data
public class PoolConfig {
    private int coreSize;
    private int maxSize;
    private String queueType;
    private int queueSize;
    private WarnRules warnRules;

    /**
     * 告警规则
     */
    @Data
    public static class WarnRules {
        private long queueSizeThreshold;
        private int activeCountThreshold;
        private boolean isActive;
    }

    public static PoolConfig defaultConfig() {
        PoolConfig poolConfig = new PoolConfig();
        poolConfig.setCoreSize(3);
        poolConfig.setMaxSize(5);
        poolConfig.setQueueType(QueueType.LINKEDBLOCKINGQUEUE.getType());
        poolConfig.setQueueSize(100);
        WarnRules warnRules = new WarnRules();
        warnRules.setActive(true);
        warnRules.setActiveCountThreshold(80);
        warnRules.setQueueSizeThreshold(100);
        poolConfig.setWarnRules(warnRules);
        return poolConfig;
    }

    public static String encode(PoolConfig config) {
        return JsonUtil.toJsonStr(config);
    }

    public static PoolConfig decode(String config) {
        return JsonUtil.toObj(config, PoolConfig.class);
    }

}
