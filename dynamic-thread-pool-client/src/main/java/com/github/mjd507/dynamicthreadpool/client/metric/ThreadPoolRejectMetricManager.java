package com.github.mjd507.dynamicthreadpool.client.metric;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mjd on 2020/6/10 19:33
 */
public class ThreadPoolRejectMetricManager {

    private static final ConcurrentMap<String, AtomicInteger> rejectCountMap = new ConcurrentHashMap<>();

    public static void increment(String poolName) {
        AtomicInteger rejectCount = rejectCountMap.get(poolName);
        if (rejectCount == null) {
            rejectCountMap.putIfAbsent(poolName, new AtomicInteger());
        }
        rejectCount = rejectCountMap.get(poolName);
        rejectCount.incrementAndGet();
    }

    public static int getAndReset(String poolName) {
        AtomicInteger rejectCount = rejectCountMap.get(poolName);
        if (rejectCount == null) {
            return 0;
        }
        return rejectCount.getAndSet(0);
    }
}
