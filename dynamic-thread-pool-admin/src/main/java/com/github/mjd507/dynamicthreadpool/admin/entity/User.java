package com.github.mjd507.dynamicthreadpool.admin.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {

    private Integer id;

    private String name;

    private String password;

    private Date createAt;

    private Date updateAt;
}