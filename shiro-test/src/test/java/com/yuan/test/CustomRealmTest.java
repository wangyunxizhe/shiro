package com.yuan.test;

import com.yuan.shiro.realm.CustomRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * Created by wangy on 2018/10/14.
 */
public class CustomRealmTest {

    /**
     * 测试shrio自定义权限的功能
     */
    @Test
    public void testAuthentication() {
        CustomRealm customRealm = new CustomRealm();
        //1，构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(customRealm);

        //shiro加密
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("md5");//设置加密方式
        matcher.setHashIterations(1);//设置加密次数
        customRealm.setCredentialsMatcher(matcher);

        //2，主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("wangyuan", "666666");
        subject.login(token);//登录
        System.out.println("是否认证：" + subject.isAuthenticated());
        subject.checkRole("admin");
        subject.checkPermissions("del", "add");
    }

}
