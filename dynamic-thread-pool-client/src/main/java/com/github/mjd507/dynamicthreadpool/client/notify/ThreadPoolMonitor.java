package com.github.mjd507.dynamicthreadpool.client.notify;

import com.github.mjd507.dynamicthreadpool.client.DynamicThreadPoolExecutor;
import com.github.mjd507.dynamicthreadpool.client.ExecutorServiceManager;
import com.github.mjd507.dynamicthreadpool.client.config.ConfigGateway;
import com.github.mjd507.dynamicthreadpool.client.config.PoolConfig;
import com.github.mjd507.dynamicthreadpool.client.metric.ThreadPoolMetric;
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

    private static Logger LOG = LoggerFactory.getLogger(ThreadPoolMonitor.class);

    /**
     * 线程睡眠时间,即每隔多长时间查看当前线程池信息
     */
    private static final long THREAD_SLEEP_THRESHOLD = 1000L;

    /**
     * 告警间隔 5分钟(300000ms)
     */
    private static final long WARN_INTERNAL = 5 * 60 * 1000;

    /**
     * 同步队列告警阈值
     */
    private static final float WARN_THRESHOLD = 80f;

    /**
     * 有界队列容量告警阈值
     */
    private static final long QUEUE_SIZE_THRESHOLD = 100;

    /**
     * 当前应用线程池名称-告警时间map
     */
    private static Map<String, Long> POOL_NAME_2_DATE = new HashMap();


    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(THREAD_SLEEP_THRESHOLD);
            } catch (InterruptedException e) {
                LOG.error("ThreadPoolListener.Thread.sleep时发生中断!", e);
            }
            sendToDxIfNeedWarn();
        }
    }


    private void sendToDxIfNeedWarn() {

        Map<String, ExecutorService> executorServiceMap = ExecutorServiceManager.getInstance().getExecutorServiceMap();
        for (Map.Entry<String, ExecutorService> entry : executorServiceMap.entrySet()) {
            ExecutorService executorService = entry.getValue();
            if (!(executorService instanceof DynamicThreadPoolExecutor)) {
                continue;
            }
            ThreadPoolMetric threadPoolMetric = ThreadPoolMetric.build(entry.getKey(), entry.getValue());
            //线程池报警间隔为WARN_INTERNAL
            if (isNeedWarn(threadPoolMetric)) {
                long currentTime = System.currentTimeMillis();
                if (POOL_NAME_2_DATE.get(entry.getKey()) != null
                        && (currentTime - POOL_NAME_2_DATE.get(entry.getKey())) < WARN_INTERNAL) {
                    continue;
                }
                POOL_NAME_2_DATE.put(entry.getKey(), currentTime);
            }
        }
    }

    private boolean isNeedWarn(ThreadPoolMetric threadPoolMetric) {
        float activeCountRateThreshold = WARN_THRESHOLD;
        long queueSizeThreshold = QUEUE_SIZE_THRESHOLD;
        boolean active = true;
        PoolConfig config = ConfigGateway.getInstance().get(threadPoolMetric.getPoolName());
        if (config != null) {
            PoolConfig.WarnRules warnRules = config.getWarnRules();
            activeCountRateThreshold = warnRules.getActiveCountThreshold() / 100f;
            queueSizeThreshold = warnRules.getQueueSizeThreshold();
            active = warnRules.isActive();
        }
        if (!active) {
            return false;
        }

        if (threadPoolMetric.getRejectCount() > 0) {
            return true;
        }

        float activeCountRate = (float) threadPoolMetric.getActiveCount() / (float) threadPoolMetric.getMaximumPoolSize();
        if (activeCountRate >= activeCountRateThreshold) {
            return true;
        }

        if (!SynchronousQueue.class.getSimpleName().equals(threadPoolMetric.getQueueType())) {
            if (threadPoolMetric.getQueueSize() >= queueSizeThreshold) {
                return true;
            }
        }

        return false;
    }

}
