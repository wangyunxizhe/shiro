package com.yuan.shiro.realm;

import com.yuan.dao.UserDao;
import com.yuan.entity.User;
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

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wangy on 2018/10/14.
 * <p>
 * shiro自定义权限功能
 */
public class CustomRealm extends AuthorizingRealm {

    @Resource
    private UserDao userDao;

    private Set<String> getRolesByUsername(String username) {
        System.out.println("===从数据库中获取授权数据===");
        List<String> list = userDao.getRolesByUsername(username);
        Set<String> sets = new HashSet<>(list);
        return sets;
    }

    //权限与角色方法类似，不做演示，依然使用测试数据
    private Set<String> getPermissionsByUsername(String username) {
        Set<String> sets = new HashSet<>();
        sets.add("user:select");
        sets.add("user:delete");
        return sets;
    }

    private String getPwdByUsername(String username) {
        User user = userDao.getPwdByUsername(username);
        if (user != null) {
            return user.getPwd();
        }
        return null;
    }

    /**
     * 该方法是用来做角色，授权用
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
     * 该方法是用来做认证，登录用
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
                (username, pwd, "customRealm");
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
