package com.yuan.test;

import com.yuan.shiro.realm.CustomRealmForAES;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * Created by wangy on 2018/10/14.
 */
public class CustomRealmForAESTest {

    /**
     * shiro aes算法加密
     */
    @Test
    public void testAuthentication() {
        CustomRealmForAES customRealm = new CustomRealmForAES();
        //1，构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(customRealm);

        //2，主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();
        String pwd = "666666";
        UsernamePasswordToken token = new UsernamePasswordToken("wangyuan", pwd);
        subject.login(token);//登录
        System.out.println("是否认证：" + subject.isAuthenticated());
        subject.checkRole("admin");
        subject.checkPermissions("del", "add");
    }

}
