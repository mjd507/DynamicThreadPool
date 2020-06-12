package com.github.mjd507.dynamicthreadpool.admin.mapper;


import com.github.mjd507.dynamicthreadpool.admin.entity.User;
import com.github.mjd507.dynamicthreadpool.admin.entity.UserAppRole;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT id, name FROM user WHERE id = #{uid} ")
    User selectUserById(@Param("uid") int id);

    @Select("SELECT id, name FROM user WHERE name = #{name} and password = #{pwd} ")
    User selectUserByNameAndPwd(@Param("name") String name, @Param("pwd") String pwd);

    @Select("SELECT app.id app_id, app.name, role.id role_id, role.description " +
            "FROM user_app_role uar " +
            "JOIN app ON uar.app_id = app.id " +
            "JOIN role ON uar.role_id = role.id " +
            "WHERE uar.user_id = #{uid}")
    @Results(id = "UserAppRole", value = {
            @Result(property = "appId", column = "app_id"),
            @Result(property = "appName", column = "name"),
            @Result(property = "roleId", column = "role_id"),
            @Result(property = "roleDesc", column = "description")
    })
    List<UserAppRole> selectUserAppsWithRole(@Param("uid") int uid);

    @Select("SELECT role.type " +
            "FROM user_app_role uar " +
            "JOIN role ON uar.role_id = role.id " +
            "WHERE uar.user_id = #{uid} AND uar.app_id = #{appId}")
    Integer selectRoleTypeByUidAndAppId(@Param("uid") int uid, @Param("appId") int appId);

    @Select("SELECT name FROM app WHERE id = #{appId}")
    String selectAppNameByAppId(@Param("appId") int appId);
}
