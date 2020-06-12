package com.github.mjd507.dynamicthreadpool.admin.util;

/**
 * Created by mjd on 2020/4/17 10:53
 */
public enum Role {

    ADMIN(1, "admin"),
    PM(2, "pm"),
    RD(3, "rd");

    private int type;
    private String desc;

    Role(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean verifyRead(int type) {
        return true;
    }

    public static boolean verifyWrite(int type) {
        return type == ADMIN.type || type == RD.type;
    }


}
