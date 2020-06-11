package com.github.mjd507.dynamicthreadpool.client.notify;

import com.github.mjd507.dynamicthreadpool.client.DynamicThreadPoolExecutor;
import com.github.mjd507.dynamicthreadpool.client.EnvUtil;
import com.github.mjd507.dynamicthreadpool.client.ExecutorServiceManager;
import com.github.mjd507.dynamicthreadpool.client.config.ConfigGateway;
import com.github.mjd507.dynamicthreadpool.client.config.PoolConfig;
import com.github.mjd507.dynamicthreadpool.client.metric.ThreadPoolMetric;
import com.github.mjd507.util.util.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by mjd on 2020/6/10 16:57
 */
public class ThreadPoolMonitor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolMonitor.class);

    /* 线程睡眠时间 */
    private static final long THREAD_SLEEP_THRESHOLD = 1000L;

    /* 告警间隔 5分钟(300000ms)*/
    private static final long WARN_INTERNAL = 5 * 60 * 1000;

    /* 当前应用线程池名称-告警时间map */
    private static final Map<String, Long> POOL_NAME_2_DATE = new HashMap<>();

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(THREAD_SLEEP_THRESHOLD);
            } catch (InterruptedException e) {
                LOGGER.error("ThreadPoolListener.Thread.sleep时发生中断!", e);
            }
            sendNotify();
        }
    }

    private void sendNotify() {
        Map<String, ExecutorService> executorServiceMap = ExecutorServiceManager.getInstance().getExecutorServiceMap();
        for (Map.Entry<String, ExecutorService> entry : executorServiceMap.entrySet()) {
            ExecutorService executorService = entry.getValue();
            if (!(executorService instanceof DynamicThreadPoolExecutor)) {
                continue;
            }
            ThreadPoolMetric threadPoolMetric = ThreadPoolMetric.build(entry.getKey(), entry.getValue());
            HashMap<String, Object> map = buildWarnMsg(threadPoolMetric);
            if (map.get("needWarn") != null) {
                long currentTime = System.currentTimeMillis();
                if (POOL_NAME_2_DATE.get(entry.getKey()) != null && (currentTime - POOL_NAME_2_DATE.get(entry.getKey())) < WARN_INTERNAL) {
                    continue;
                }
                POOL_NAME_2_DATE.put(entry.getKey(), currentTime);
                LOGGER.warn(String.valueOf(map.get("warnMsg")));
            }
        }
    }

    private MapUtil buildWarnMsg(ThreadPoolMetric threadPoolMetric) {
        MapUtil mapUtil = MapUtil.newMap();
        if (threadPoolMetric == null) {
            return mapUtil;
        }
        boolean needWarn = false;
        StringBuilder warnMsg = new StringBuilder();
        warnMsg.append(">>>>>>> 线程池监控报警 <<<<<<<< \n");
        warnMsg.append(String.format("[应用名: %s] \n", EnvUtil.getAppName()));
        warnMsg.append(String.format("[机器IP: %s] \n", EnvUtil.getAppName()));
        warnMsg.append("[告警原因]\n");
        PoolConfig.WarnRules defaultRules = PoolConfig.defaultConfig().getWarnRules();
        float activeCountRateThreshold = defaultRules.getActiveCountThreshold() / 100f;
        long queueSizeThreshold = defaultRules.getQueueSizeThreshold();
        boolean active = true;
        PoolConfig config = ConfigGateway.getInstance().get(threadPoolMetric.getPoolName());
        if (config != null) {
            PoolConfig.WarnRules warnRules = config.getWarnRules();
            activeCountRateThreshold = warnRules.getActiveCountThreshold() / 100f;
            queueSizeThreshold = warnRules.getQueueSizeThreshold();
            active = warnRules.isActive();
        }
        if (!active) {
            return mapUtil;
        }

        if (threadPoolMetric.getRejectCount() > 0) {
            needWarn = true;
            warnMsg.append(String.format("线程池中有 %s 个被拒绝的任务\n", threadPoolMetric.getRejectCount()));
        }

        float activeCountRate = (float) threadPoolMetric.getActiveCount() / (float) threadPoolMetric.getMaxSize();
        if (activeCountRate >= activeCountRateThreshold) {
            needWarn = true;
            warnMsg.append(String.format("activeCount/maximumPoolSize值为(%s)大于阈值(%s)\n", activeCountRate, activeCountRateThreshold));
        }

        if (!SynchronousQueue.class.getSimpleName().equals(threadPoolMetric.getQueueType())) {
            if (threadPoolMetric.getQueueCurrentSize() >= queueSizeThreshold) {
                needWarn = true;
                warnMsg.append(String.format("队列当前任务数量(%s)超过设定阈值(%s)\n", threadPoolMetric.getQueueCurrentSize(), queueSizeThreshold));
            }
        }
        if (needWarn) {
            warnMsg.append("[线程池参数]\n");
            warnMsg.append(String.format("poolName:%s", threadPoolMetric.getPoolName()));
            warnMsg.append(String.format("corePoolSize:%s", threadPoolMetric.getCoreSize()));
            warnMsg.append(String.format("maximumPoolSize:%s", threadPoolMetric.getMaxSize()));
            warnMsg.append(String.format("poolSize:%s", threadPoolMetric.getCurrentSize()));
            warnMsg.append(String.format("activeCount:%s", threadPoolMetric.getActiveCount()));
            warnMsg.append(String.format("queueType:%s", threadPoolMetric.getQueueType()));
            warnMsg.append(String.format("queueCapacity:%s", threadPoolMetric.getQueueCapacity()));
            warnMsg.append(String.format("queueSize:%s", threadPoolMetric.getQueueCurrentSize()));
            warnMsg.append(String.format("queueRemainingCapacity:%s", threadPoolMetric.getQueueRemainingCapacity()));
            warnMsg.append(String.format("completedTaskCount:%s", threadPoolMetric.getCompletedTaskCount()));
            warnMsg.append(String.format("largestPoolSize:%s", threadPoolMetric.getLargestPoolSize()));
            warnMsg.append(String.format("rejectCount:%s", threadPoolMetric.getRejectCount()));
            warnMsg.append(String.format("ipAddress:%s", threadPoolMetric.getIpAddress()));
            warnMsg.append(String.format("[线程池配置链接]:%s", "http://localhost"));
            warnMsg.append("[告警时间间隔] 5分钟");
            mapUtil.put("needWarn", true);
            mapUtil.put("warnMsg", warnMsg.toString());
        }
        return mapUtil;
    }

}
