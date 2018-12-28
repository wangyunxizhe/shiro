package com.yuan.controller;

import com.yuan.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by wangy on 2018/10/14.
 */
@Controller
public class UserController {

    @RequestMapping(value = "/subLogin", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String subLogin(User user) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPwd());
        try {
            token.setRememberMe(user.isRememberMe());
            subject.login(token);//登录
        } catch (AuthenticationException e) {
            e.getMessage();
        }
        if (subject.hasRole("admin")) {
            return "有admin角色";
        }
        return "登录成功，无admin角色";
    }

    /**
     * shiro注解方式
     * <p>
     * 表示当前认证主体必须有admin角色，才可以访问下面的URL
     */
    @RequiresRoles("admin")
    @RequestMapping(value = "/testRole", method = RequestMethod.GET)
    @ResponseBody
    public String testRole() {
        return "testRole_success";
    }

    /**
     * 以下4个请求方法是shiro自带过滤器方式：详细过滤条件写在spring.xml，id为shiroFilter的bean中的
     * <property name="filterChainDefinitions">的<value>里
     */
    @RequestMapping(value = "/testRole1", method = RequestMethod.GET)
    @ResponseBody
    public String testRole1() {
        return "testRole1_success";
    }

    @RequestMapping(value = "/testRole2", method = RequestMethod.GET)
    @ResponseBody
    public String testRole2() {
        return "testRole2_success";
    }

    @RequestMapping(value = "/testPerms1", method = RequestMethod.GET)
    @ResponseBody
    public String testPerms1() {
        return "testPerms1_success";
    }

    @RequestMapping(value = "/testPerms2", method = RequestMethod.GET)
    @ResponseBody
    public String testPerms2() {
        return "testPerms2_success";
    }

    /**
     * shiro自定义过滤器方式：详细过滤条件写在spring.xml中，id为rolesOrFilter的bean就是自定义过滤器
     * <p>
     * 注意跟/testRole2请求的区别
     */
    @RequestMapping(value = "/testRoleZDY", method = RequestMethod.GET)
    @ResponseBody
    public String testRoleZDY() {
        return "testRoleZDY_success";
    }

}
