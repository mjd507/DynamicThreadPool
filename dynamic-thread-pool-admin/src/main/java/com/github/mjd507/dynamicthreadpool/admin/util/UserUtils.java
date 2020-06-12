package com.github.mjd507.dynamicthreadpool.admin.util;


import com.github.mjd507.dynamicthreadpool.admin.entity.User;

/**
 * Created by mjd on 2020/4/17 09:54
 */
public class UserUtils {

    private UserUtils() {
    }

    private static final ThreadLocal<User> threadLocal = new ThreadLocal<>();

    public static void setUser(User user) {
        threadLocal.set(user);
    }

    public static User getUser() {
        return threadLocal.get();
    }

    public static int getUid() {
        return getUser().getId();
    }

    public static String getUserName() {
        return getUser().getName();
    }

    public static void clear() {
        threadLocal.remove();
    }

}
