package com.yuan.test;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by wangy on 2018/10/13.
 */
public class AuthenticationTest {

    //演示认证功能，用于添加基础用户
    SimpleAccountRealm simpleAccountRealm = new SimpleAccountRealm();

    @Before
    public void add() {
        //初始添加一个用户
        simpleAccountRealm.addAccount("tony", "666666");
        //添加一个具有admin角色的用户
        simpleAccountRealm.addAccount("boss", "888888", "admin");
    }

    /**
     * 测试shrio认证功能
     */
    @Test
    public void testAuthentication() {
        //1，构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(simpleAccountRealm);
        //2，主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("tony", "666666");
        subject.login(token);//登录
        System.out.println("是否认证：" + subject.isAuthenticated());
        subject.logout();//退出
        System.out.println("是否认证：" + subject.isAuthenticated());
    }

    /**
     * 测试shrio授权功能
     */
    @Test
    public void testRole() {
        //1，构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(simpleAccountRealm);
        //2，主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("boss", "888888");
        subject.login(token);//登录
        subject.checkRole("admin");//检查boss是否拥有admin这个角色
    }

}
