package com.github.mjd507.dynamicthreadpool.admin.vo.request;

import lombok.Data;

/**
 * Created by mjd on 2020/4/17 17:16
 */
@Data
public class ConfigReq {
    private Integer appId;
    private String poolName;

    private Integer coreSize;
    private Integer maxSize;
    private String queueType;
    private Integer queueCapacity;
    private Integer queueSizeThreshold;
    private Integer activeCountThreshold;
    private Boolean isActive;
    private Boolean isAdd = false;
}
