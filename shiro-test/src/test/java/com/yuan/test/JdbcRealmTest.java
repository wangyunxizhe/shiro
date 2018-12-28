package com.yuan.test;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * Created by wangy on 2018/10/13.
 */
public class JdbcRealmTest {

    DruidDataSource dataSource = new DruidDataSource();

    {
        dataSource.setUrl("jdbc:mysql://localhost:3306/test?characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("870814");
    }

    /**
     * 测试shrio连接数据库定义权限功能
     * <p>
     * 默认去数据库中查询名为“users”的表中“username，password”字段有无传入的值：有则认证成功
     * 注：shrio默认sql语句中的表名：1，roles_permissions。2，user_roles。3，users。
     */
    @Test
    public void testAuthentication() {
        JdbcRealm jdbcRealm = new JdbcRealm();
        jdbcRealm.setDataSource(dataSource);
        jdbcRealm.setPermissionsLookupEnabled(true);//默认false，不去查权限数据。如要使用权限数据，需要设置为true

        //使用自定义sql语句
        String sql = "select password from test_user where user_name = ?";
        jdbcRealm.setAuthenticationQuery(sql);
//        String sql1 = "select role_name from user_roles where username = ?";
//        jdbcRealm.setUserRolesQuery(sql1);//设置角色权限sql
        //使用自定义sql时的登录信息
        UsernamePasswordToken token = new UsernamePasswordToken("yuanwang", "110948");

        //1，构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(jdbcRealm);
        //2，主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();
//        UsernamePasswordToken token = new UsernamePasswordToken("wangyuan", "870814");
        subject.login(token);//登录
        System.out.println("是否认证：" + subject.isAuthenticated());
        subject.checkRole("admin");//查看用户是否有admin角色
        subject.checkRoles("admin", "user");//查看用户是否有admin以及user角色
        subject.checkPermission("user:select");//查看用户是否有user:select的权限
    }

}
