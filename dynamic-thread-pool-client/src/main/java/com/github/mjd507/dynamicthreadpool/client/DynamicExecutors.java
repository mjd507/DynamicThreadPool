package com.github.mjd507.dynamicthreadpool.client;

import com.github.mjd507.dynamicthreadpool.client.config.ConfigGateway;
import com.github.mjd507.dynamicthreadpool.client.config.PoolConfig;
import com.github.mjd507.dynamicthreadpool.client.enums.QueueType;
import com.github.mjd507.dynamicthreadpool.client.queue.ResizeableCapacityLinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by mjd on 2020/6/10 19:28
 */
public class DynamicExecutors {

    private static Logger LOGGER = LoggerFactory.getLogger(DynamicExecutors.class);

    private DynamicExecutors() {
    }

    private static final ExecutorServiceManager manager = ExecutorServiceManager.getInstance();
    private static final ConfigGateway configGateWay = ConfigGateway.getInstance();

    interface ExecutorServiceProvider {
        ExecutorService provide(String name);
    }

    private static ExecutorService newExecutorService(String name, ExecutorServiceProvider executorServiceProvider) {
        ExecutorService executorService = manager.getExecutorService(name);
        if (executorService != null) {
            return executorService;
        }
        synchronized (manager) {
            executorService = manager.getExecutorService(name);
            if (executorService == null) {
                executorService = executorServiceProvider.provide(name);
            }
            manager.registerExecutorService(name, executorService);
        }
        return executorService;
    }

    public static ExecutorService newSingleThreadPool(String name) {
        return newExecutorService(name, poolName -> new DynamicThreadPoolExecutor(name, 1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()));
    }

    public static ExecutorService newFixedThreadPool(String name, final int nThreads) {
        return newExecutorService(name, poolName -> {
            PoolConfig config = configGateWay.get(poolName);
            if (config == null) {
                LOGGER.info(">>>>>>线程池[{}]没有获取到配置,使用默认值", name);
                return new DynamicThreadPoolExecutor(name, nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
            }
            LOGGER.info(">>>>>>线程池[{}]获取到配置,使用动态配置:{}", poolName, PoolConfig.encode(config));
            return new DynamicThreadPoolExecutor(name, config.getCoreSize(), config.getMaxSize(), 0L, TimeUnit.MILLISECONDS, getBlockingQueue(config));
        });
    }

    public static ExecutorService newCachedThreadPool(String name) {
        return newExecutorService(name, poolName -> {
            PoolConfig config = configGateWay.get(poolName);
            if (config == null) {
                LOGGER.info(">>>>>>线程池[{}]没有获取到配置,使用默认值", poolName);
                return new DynamicThreadPoolExecutor(name, 0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
            }
            LOGGER.info(">>>>>>线程池[{}]获取到配置,使用动态配置:{}", poolName, PoolConfig.encode(config));
            return new DynamicThreadPoolExecutor(name, config.getCoreSize(), config.getMaxSize(), 60L, TimeUnit.SECONDS, getBlockingQueue(config));
        });
    }

    public static ExecutorService newScheduledThreadPool(String name, final int corePoolSize) {
        return newExecutorService(name, poolName -> new ScheduledThreadPoolExecutor(corePoolSize, DynamicThreadPoolExecutor.namedThreadFactory(poolName), DynamicThreadPoolExecutor.countRejectHandler));
    }

    public static ExecutorService newThreadPoolExecutor(String name) {
        return newExecutorService(name, poolName -> {
            PoolConfig config = configGateWay.get(poolName);
            if (config == null) {
                config = PoolConfig.defaultConfig();
                LOGGER.warn(String.format("获取线程池配置失败, 线程池名: %s, 使用默认参数: %s", poolName, PoolConfig.encode(config)));
            }
            return new DynamicThreadPoolExecutor(poolName, config.getCoreSize(), config.getMaxSize(), 60L, TimeUnit.SECONDS, getBlockingQueue(config));
        });
    }

    public static ExecutorService newThreadPoolExecutor(String name,
                                                        final int corePoolSize,
                                                        final int maximumPoolSize,
                                                        final long keepAliveTime,
                                                        final TimeUnit unit,
                                                        final BlockingQueue<Runnable> workQueue) {
        return newExecutorService(name, poolName -> {
            PoolConfig config = configGateWay.get(poolName);
            if (config == null) {
                LOGGER.info(">>>>>>线程池[{}]没有获取到配置,使用默认值", name);
                return new DynamicThreadPoolExecutor(name, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
            }
            LOGGER.info(">>>>>>线程池[{}]获取到配置,使用动态配置:{}", poolName, PoolConfig.encode(config));
            return new DynamicThreadPoolExecutor(name, config.getCoreSize(), config.getMaxSize(), keepAliveTime, unit, getBlockingQueue(config));
        });
    }

    private static BlockingQueue<Runnable> getBlockingQueue(PoolConfig config) {
        if (QueueType.SYNCHRONOUSQUEUE.getType().equals(config.getQueueType())) {
            return new SynchronousQueue<>();
        } else {
            return new ResizeableCapacityLinkedBlockingQueue<>(config.getQueueCapacity());
        }
    }

}
