package com.github.mjd507.dynamicthreadpool.client;

import com.github.mjd507.dynamicthreadpool.client.metric.ThreadPoolRejectMetricManager;
import com.github.mjd507.dynamicthreadpool.client.queue.ResizeableCapacity;
import com.github.mjd507.util.util.JsonUtil;
import com.github.mjd507.util.util.MapUtil;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 动态线程池构造
 * Created by mjd on 2020/6/10 19:40
 */
public class DynamicThreadPoolExecutor extends ThreadPoolExecutor {

    private String poolName;

    public DynamicThreadPoolExecutor(String poolName, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        this(poolName, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, namedThreadFactory(poolName));
    }

    public DynamicThreadPoolExecutor(String poolName, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        this(poolName, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, countRejectHandler);
    }

    public DynamicThreadPoolExecutor(String poolName, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        this(poolName, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, namedThreadFactory(poolName), handler);
    }

    public DynamicThreadPoolExecutor(String poolName, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.poolName = poolName;
    }

    public String getPoolName() {
        return poolName;
    }

    @Override
    public String toString() {
        Map<String, Object> metaInfo = MapUtil.newMap();
        metaInfo.put("poolName", getPoolName());
        metaInfo.put("corePoolSize", getCorePoolSize());
        metaInfo.put("maximumPoolSize", getMaximumPoolSize());
        metaInfo.put("activeCount", getActiveCount());
        metaInfo.put("poolSize", getPoolSize());
        metaInfo.put("completedTaskCount", getCompletedTaskCount());
        if (getQueue() instanceof ResizeableCapacity) {
            metaInfo.put("queueCapacity", ((ResizeableCapacity) getQueue()).getCapacity());
        }
        metaInfo.put("queueSize", getQueue().size());
        metaInfo.put("queueRemainingCapacity", getQueue().remainingCapacity());
        return JsonUtil.toJsonStr(metaInfo);
    }

    public static ThreadFactory namedThreadFactory(String name) {
        return new NamedThreadFactory(name);
    }

    private static class NamedThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        NamedThreadFactory(String biz) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "dynamic-pool-" + biz + "-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }


    private static final RejectedExecutionHandler defaultRejectHandler = new ThreadPoolExecutor.AbortPolicy();
    public static final RejectedExecutionHandler countRejectHandler = new CountRejectedHandler();

    /**
     * reject handler
     */
    static class CountRejectedHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (executor instanceof DynamicThreadPoolExecutor) {
                DynamicThreadPoolExecutor namedThreadPoolExecutor = (DynamicThreadPoolExecutor) executor;
                ThreadPoolRejectMetricManager.increment(namedThreadPoolExecutor.getPoolName());
            }
            defaultRejectHandler.rejectedExecution(r, executor);
        }
    }
}
