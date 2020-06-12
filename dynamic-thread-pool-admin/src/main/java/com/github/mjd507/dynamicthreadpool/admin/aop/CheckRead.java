package com.github.mjd507.dynamicthreadpool.admin.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 检验是否有读权限
 * 条件：方法上需有 @CheckRead 注解，
 * 方法参数里必须有且只有一个 @CheckParam 注解，修饰 Integer 类型参数。
 *
 * Created by mjd on 2020/4/17 11:45
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CheckRead {
}
