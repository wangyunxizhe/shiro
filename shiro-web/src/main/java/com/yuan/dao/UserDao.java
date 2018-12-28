package com.yuan.dao;

import com.yuan.entity.User;

import java.util.List;

/**
 * Created by wangy on 2018/10/14.
 */
public interface UserDao {
    User getPwdByUsername(String username);

    List<String> getRolesByUsername(String username);
}
