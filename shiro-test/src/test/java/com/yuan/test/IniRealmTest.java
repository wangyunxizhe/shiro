package com.yuan.test;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * Created by wangy on 2018/10/13.
 */
public class IniRealmTest {

    /**
     * 测试shrio配置文件定义权限功能
     */
    @Test
    public void testAuthentication() {
        IniRealm iniRealm = new IniRealm("classpath:user.ini");
        //1，构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(iniRealm);
        //2，主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("wangyuan", "870814");
        subject.login(token);//登录
        System.out.println("是否认证：" + subject.isAuthenticated());
        subject.checkRole("baba");//检查用户是否有baba角色
        subject.checkPermission("user:delete");//检查用户是否拥有user:delete的权限
        subject.checkPermission("kill");//检查用户是否拥有kill的权限
    }

}
