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

    /**
     * 线程池名字
     */
    private String poolName;
    /**
     * 核心线程数
     */
    private int corePoolSize;
    /**
     * 最大线程数
     */
    private int maximumPoolSize;
    /**
     * 当前线程池中的线程数
     */
    private int poolSize;
    /**
     * 当前正在执行任务的线程数(估计值)
     */
    private int activeCount;
    /**
     * 队列类型
     */
    private String queueType;
    /**
     * 队列容量
     */
    private int queueCapacity;
    /**
     * 当前队列任务数
     */
    private int queueSize;
    /**
     * 剩余队列大小
     */
    private int queueRemainingCapacity;

    /**
     * 已经处理任务数
     */
    private long completedTaskCount;

    /**
     * 最大poolSize
     */
    private int largestPoolSize;

    /**
     * 线程拒绝执行次数
     */
    private int rejectCount;

    /**
     * 机器IP地址
     */
    private String ipAddress;

    public static ThreadPoolMetric build(String poolName, ExecutorService executorService) {
        if (!(executorService instanceof DynamicThreadPoolExecutor)) {
            return null;
        }
        DynamicThreadPoolExecutor dynamicThreadPoolExecutor = (DynamicThreadPoolExecutor) executorService;
        ThreadPoolMetric threadPoolMetric = new ThreadPoolMetric();
        threadPoolMetric.setPoolName(poolName);
        threadPoolMetric.setCorePoolSize(dynamicThreadPoolExecutor.getCorePoolSize());
        threadPoolMetric.setMaximumPoolSize(dynamicThreadPoolExecutor.getMaximumPoolSize());
        threadPoolMetric.setPoolSize(dynamicThreadPoolExecutor.getPoolSize());
        threadPoolMetric.setActiveCount(dynamicThreadPoolExecutor.getActiveCount());
        threadPoolMetric.setRejectCount(ThreadPoolRejectMetricManager.getAndReset(poolName));

        String ipAddress = HostUtil.getHostIp();

        threadPoolMetric.setIpAddress(ipAddress);
        setQueueInfo(threadPoolMetric, dynamicThreadPoolExecutor);

        threadPoolMetric.setCompletedTaskCount(dynamicThreadPoolExecutor.getCompletedTaskCount());
        threadPoolMetric.setLargestPoolSize(dynamicThreadPoolExecutor.getLargestPoolSize());
        return threadPoolMetric;
    }

    private static void setQueueInfo(ThreadPoolMetric threadPoolMetric, DynamicThreadPoolExecutor dynamicThreadPoolExecutor) {
        BlockingQueue blockingQueue = dynamicThreadPoolExecutor.getQueue();
        threadPoolMetric.setQueueType(blockingQueue.getClass().getSimpleName());
        threadPoolMetric.setQueueRemainingCapacity(blockingQueue.remainingCapacity());
        threadPoolMetric.setQueueSize(blockingQueue.size());
        threadPoolMetric.setQueueCapacity(blockingQueue.size() + blockingQueue.remainingCapacity());
    }
}
