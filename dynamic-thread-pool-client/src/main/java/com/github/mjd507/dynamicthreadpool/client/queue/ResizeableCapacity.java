package com.github.mjd507.dynamicthreadpool.client.queue;

/**
 * Created by mjd on 2020/6/10 09:26
 */
public interface ResizeableCapacity {

    void setCapacity(int capacity);

    int getCapacity();
}
