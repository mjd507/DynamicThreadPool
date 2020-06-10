package com.github.mjd507.dynamicthreadpool.client;

import com.github.mjd507.dynamicthreadpool.client.config.ConfigGateway;
import com.github.mjd507.dynamicthreadpool.client.config.ConfigListener;
import com.github.mjd507.dynamicthreadpool.client.config.PoolConfig;
import com.github.mjd507.dynamicthreadpool.client.queue.ResizeableCapacity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 所以线程池服务管理
 * 1. 将所以线程池以名字区分，存进 map 里
 * 2. 当某个线程池配置改变时，更新线程池配置
 * <p>
 * Created by mjd on 2020/6/10 16:30
 */
public class ExecutorServiceManager implements ConfigListener {

    private static Logger LOGGER = LoggerFactory.getLogger(ExecutorServiceManager.class);

    private ExecutorServiceManager() {
        ConfigGateway.getInstance().registerListener(this);
    }

    private static final ExecutorServiceManager instance = new ExecutorServiceManager();

    public static ExecutorServiceManager getInstance() {
        return instance;
    }

    private final Map<String, ExecutorService> executorServiceMap = new ConcurrentHashMap<>();

    public void registerExecutorService(String name, ExecutorService executorService) {
        executorServiceMap.put(name, executorService);
    }

    public ExecutorService getExecutorService(String name) {
        if (!executorServiceMap.containsKey(name)) {
            return null;
        }
        return executorServiceMap.get(name);
    }

    public Map<String, ThreadPoolInfo> dump() {
        Map<String, ThreadPoolInfo> result = new HashMap<>();
        for (String name : executorServiceMap.keySet()) {
            ExecutorService executorService = executorServiceMap.get(name);
            result.put(name, convert2ThreadPoolInfo(executorService));
        }
        return result;
    }

    private ThreadPoolInfo convert2ThreadPoolInfo(ExecutorService executorService) {
        ThreadPoolInfo result = new ThreadPoolInfo();
        result.setActiveCount(((ThreadPoolExecutor) executorService).getActiveCount());
        result.setExecutorService(executorService);
        return result;
    }


    @Override
    public void changed(String poolName, PoolConfig config) {
        LOGGER.info(String.format("线程池配置变更, 线程池名: %s, 线程池配置: %s", poolName, PoolConfig.encode(config)));
        ExecutorService executorService = executorServiceMap.get(poolName);
        if (!(executorService instanceof ThreadPoolExecutor)) {
            return;
        }
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
        threadPoolExecutor.setCorePoolSize(config.getCoreSize());
        threadPoolExecutor.setMaximumPoolSize(config.getMaxSize());
        BlockingQueue blockingQueue = threadPoolExecutor.getQueue();
        if (blockingQueue instanceof ResizeableCapacity) {
            ((ResizeableCapacity) blockingQueue).setCapacity(config.getQueueSize());
        }
    }

}
