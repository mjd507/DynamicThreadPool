package com.github.mjd507.dynamicthreadpool.admin.service;


import com.github.mjd507.configcenter.client.core.CuratorManager;
import com.github.mjd507.configcenter.client.listener.ConfigChangeListener;
import com.github.mjd507.dynamicthreadpool.admin.aop.CheckParam;
import com.github.mjd507.dynamicthreadpool.admin.aop.CheckRead;
import com.github.mjd507.dynamicthreadpool.admin.aop.CheckWrite;
import com.github.mjd507.dynamicthreadpool.admin.vo.request.ConfigReq;
import com.github.mjd507.dynamicthreadpool.client.config.PoolConfig;
import com.github.mjd507.dynamicthreadpool.client.enums.QueueType;
import com.github.mjd507.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created by mjd on 2020/4/17 11:41
 */
@Service
@Slf4j
public class ConfigService {

    @Autowired
    private CuratorManager configManager;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() {
        String watchKey = "key1";
        configManager.registerConfigChangeListener(new ConfigChangeListener(watchKey) {
            @Override
            public void onConfigChange(Object newVal) {
                log.info("监听到配置改变，key: {}, val:{}", watchKey, newVal);
            }
        });
    }

    @CheckRead
    public Map<String, String> listAppConfig(@CheckParam Integer appId) {
        String appName = userService.getAppName(appId);
        return configManager.getAllByAppKey(appName);
    }

    @CheckWrite
    public boolean writeConfig(ConfigReq configReq, @CheckParam Integer appId) {
        if (!checkReq(configReq)) throw new BusinessException("参数缺失");
        String appName = userService.getAppName(appId);
        boolean add = configReq.getIsAdd();
        String fullPath = appName + "/" + configReq.getPoolName();
        String val = generateConfig(configReq);
        if (add) {
            return configManager.add(fullPath, val);
        } else {
            return configManager.update(fullPath, val);
        }
    }

    private String generateConfig(ConfigReq configReq) {
        PoolConfig config = new PoolConfig();
        config.setCoreSize(configReq.getCoreSize());
        config.setMaxSize(configReq.getMaxSize());
        config.setQueueType(configReq.getQueueType());
        config.setQueueCapacity(configReq.getQueueCapacity());
        PoolConfig.WarnRules warnRules = new PoolConfig.WarnRules();
        warnRules.setActive(configReq.getIsActive());
        warnRules.setQueueSizeThreshold(configReq.getQueueSizeThreshold());
        warnRules.setActiveCountThreshold(configReq.getActiveCountThreshold());
        config.setWarnRules(warnRules);
        return PoolConfig.encode(config);
    }

    private boolean checkReq(ConfigReq configReq) {
        if (configReq == null) return false;
        if (configReq.getPoolName() == null) return false;
        if (configReq.getCoreSize() == null) return false;
        if (configReq.getMaxSize() == null) return false;
        if (configReq.getQueueType() == null) return false;
        if (!(configReq.getQueueType().equals(QueueType.LINKEDBLOCKINGQUEUE.getType()) ||
                configReq.getQueueType().equals(QueueType.SYNCHRONOUSQUEUE.getType())))
            return false;
        if (configReq.getQueueCapacity() == null) return false;
        if (configReq.getIsActive() == null) return false;
        if (configReq.getQueueSizeThreshold() == null) return false;
        if (configReq.getActiveCountThreshold() == null) return false;
        return true;
    }

    @CheckWrite
    public boolean deleteConfig(@CheckParam Integer appId, String key) {
        String appName = userService.getAppName(appId);
        String fullPath = appName + "/" + key;
        return configManager.delete(fullPath);
    }
}
