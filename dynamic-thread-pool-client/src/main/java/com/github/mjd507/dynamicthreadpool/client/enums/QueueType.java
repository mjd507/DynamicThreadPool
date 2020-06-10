package com.github.mjd507.dynamicthreadpool.client.enums;

public enum QueueType {

    SYNCHRONOUSQUEUE("同步队列", "SynchronousQueue"),
    LINKEDBLOCKINGQUEUE("非同步队列", "LinkedBlockingQueue");

    private String name;
    private String type;

    QueueType(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
    }
}