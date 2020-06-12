package com.github.mjd507.dynamicthreadpool.admin.config;

import com.github.mjd507.configcenter.client.core.CuratorManager;
import com.github.mjd507.util.util.ResourceUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mjd on 2020/6/12 15:09
 */
@Configuration
@Getter
@Setter
public class CuratorConfig {

    @Value("${configcenter.connect-string}")
    private String connectString;

    @Value("${configcenter.is-admin}")
    private Boolean isAdmin;

    @Bean(name = "curatorManager", destroyMethod = "close")
    public CuratorManager curatorManager() {
        CuratorManager curatorManager = new CuratorManager();
        curatorManager.setNameSpace(ResourceUtil.getAppName());
        curatorManager.setIsAdmin(isAdmin);
        curatorManager.setConnectString(connectString);
        curatorManager.init();
        return curatorManager;
    }
}
