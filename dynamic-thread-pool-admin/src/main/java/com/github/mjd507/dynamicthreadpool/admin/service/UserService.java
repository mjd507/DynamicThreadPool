package com.github.mjd507.dynamicthreadpool.admin.service;


import com.github.mjd507.dynamicthreadpool.admin.entity.User;
import com.github.mjd507.dynamicthreadpool.admin.entity.UserAppRole;
import com.github.mjd507.dynamicthreadpool.admin.mapper.UserMapper;
import com.github.mjd507.util.spring.RedisOperation;
import com.github.mjd507.util.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Created by mjd on 2020/4/16 16:51
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisOperation redisOperation;

    public String login(String name, String pwd) {
        User user = userMapper.selectUserByNameAndPwd(name, Md5Util.digist(pwd));
        if (user == null) {
            return null;
        }
        String accessToken = UUID.randomUUID().toString().replace("-", "");
        redisOperation.set(accessToken, user.getId() + "", 30 * 24 * 60 * 60);
        return accessToken;
    }

    public User getUserById(Integer uId) {
        return userMapper.selectUserById(uId);
    }

    public List<UserAppRole> listUserAppsWithRole(int uid) {
        return userMapper.selectUserAppsWithRole(uid);
    }

    public Integer getRoleTypeByUidAndAppId(int uid, int appId) {
        return userMapper.selectRoleTypeByUidAndAppId(uid, appId);
    }

    public String getAppName(int appId) {
        return userMapper.selectAppNameByAppId(appId);
    }
}
