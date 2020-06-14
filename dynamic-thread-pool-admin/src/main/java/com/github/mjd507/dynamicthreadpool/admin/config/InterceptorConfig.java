package com.github.mjd507.dynamicthreadpool.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by mjd on 2020/4/13 09:25
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public UserInterceptor userInterceptor() {
        return new UserInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/test/**")
        ;
    }
}
