package com.github.mjd507.dynamicthreadpool.admin.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by mjd on 2020/4/17 12:01
 */
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.METHOD)
public @interface CheckWrite {
}
