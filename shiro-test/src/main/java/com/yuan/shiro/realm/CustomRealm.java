package com.yuan.shiro.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangy on 2018/10/14.
 * <p>
 * shiro自定义权限功能
 */
public class CustomRealm extends AuthorizingRealm {

    Map<String, String> userMap = new HashMap<>(16);

    {
        //初始化一些测试数据
        userMap.put("wangyuan", "58f9ffe9596ca985cb3d7475948b28f1");
        super.setName("customRealm");
    }

    //模拟去数据库查询出的结果
    private Set<String> getRolesByUsername(String username) {
        //假设用户有admin，以及user两个角色
        Set<String> sets = new HashSet<>();
        sets.add("admin");
        sets.add("user");
        return sets;
    }

    //模拟去数据库查询出的结果
    private Set<String> getPermissionsByUsername(String username) {
        //假设角色有del，以及add两个权限
        Set<String> sets = new HashSet<>();
        sets.add("del");
        sets.add("add");
        return sets;
    }

    //模拟去数据库查询出的结果
    private String getPwdByUsername(String username) {
        return userMap.get(username);
    }

    /**
     * 该方法是用来做授权用
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //1，获取传来主体的认证信息中的用户名
        String username = (String) principalCollection.getPrimaryPrincipal();
        //2，从数据库或缓存中获取角色数据
        Set<String> roles = getRolesByUsername(username);
        //3，从数据库或缓存中获取角色的权限数据
        Set<String> permissions = getPermissionsByUsername(username);
        //4，将查询出的数据返回
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(roles);
        authorizationInfo.setStringPermissions(permissions);
        return authorizationInfo;
    }

    /**
     * 该方法是用来做认证用
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        //1，获取传来主体的认证信息中的用户名
        String username = (String) authenticationToken.getPrincipal();
        //2，通过用户名去数据库中获取凭证（也就是密码）
        String pwd = getPwdByUsername(username);
        if (pwd == null) {
            return null;
        }
        //3，将查询出的数据返回
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo
                ("wangyuan", pwd, "customRealm");
        //4，如果需要密码再被加盐处理，那么必须加下面的代码
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes("mlgb"));
        return authenticationInfo;
    }

    public static void main(String[] args) {
        //计算666666的密文
        //将密码传入，再加盐，双保险。也可以直接传入密码；两种方式都可以得到md5密文
        Md5Hash md5 = new Md5Hash("666666", "mlgb");
        System.out.println(md5.toString());
    }

}
