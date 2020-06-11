package com.github.mjd507.dynamicthreadpool.client.metric;

import com.github.mjd507.dynamicthreadpool.client.DynamicThreadPoolExecutor;
import com.github.mjd507.util.util.HostUtil;
import lombok.Data;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * Created by mjd on 2020/6/11 10:16
 */
@Data
public class ThreadPoolMetric {

    private String poolName;
    private int coreSize;
    private int maxSize;
    private int currentSize;
    private int activeCount;
    private String queueType;
    private int queueCapacity;
    private int queueCurrentSize;
    private int queueRemainingCapacity;
    private long completedTaskCount;
    private int largestPoolSize;
    private int rejectCount;
    private String ipAddress;

    public static ThreadPoolMetric build(String poolName, ExecutorService executorService) {
        if (!(executorService instanceof DynamicThreadPoolExecutor)) {
            return null;
        }
        DynamicThreadPoolExecutor dynamicThreadPoolExecutor = (DynamicThreadPoolExecutor) executorService;
        ThreadPoolMetric threadPoolMetric = new ThreadPoolMetric();
        threadPoolMetric.setPoolName(poolName);
        threadPoolMetric.setCoreSize(dynamicThreadPoolExecutor.getCorePoolSize());
        threadPoolMetric.setMaxSize(dynamicThreadPoolExecutor.getMaximumPoolSize());
        threadPoolMetric.setCurrentSize(dynamicThreadPoolExecutor.getPoolSize());
        threadPoolMetric.setActiveCount(dynamicThreadPoolExecutor.getActiveCount());
        threadPoolMetric.setRejectCount(ThreadPoolRejectMetricManager.getAndReset(poolName));
        threadPoolMetric.setIpAddress(HostUtil.getHostIp());
        setQueueInfo(threadPoolMetric, dynamicThreadPoolExecutor);

        threadPoolMetric.setCompletedTaskCount(dynamicThreadPoolExecutor.getCompletedTaskCount());
        threadPoolMetric.setLargestPoolSize(dynamicThreadPoolExecutor.getLargestPoolSize());
        return threadPoolMetric;
    }

    private static void setQueueInfo(ThreadPoolMetric threadPoolMetric, DynamicThreadPoolExecutor dynamicThreadPoolExecutor) {
        BlockingQueue<Runnable> blockingQueue = dynamicThreadPoolExecutor.getQueue();
        threadPoolMetric.setQueueType(blockingQueue.getClass().getSimpleName());
        threadPoolMetric.setQueueRemainingCapacity(blockingQueue.remainingCapacity());
        threadPoolMetric.setQueueCurrentSize(blockingQueue.size());
        threadPoolMetric.setQueueCapacity(blockingQueue.size() + blockingQueue.remainingCapacity());
    }
}
