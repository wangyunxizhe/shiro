package com.yuan.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created by wangy on 2018/10/14.
 * <p>
 * 自定义shiro过滤器
 */
public class RolesOrFilter extends AuthorizationFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response,
                                      Object o) throws Exception {
        Subject subject = getSubject(request, response);//获取登录主体
        String[] roles = (String[]) o;//角色信息数组
        if (roles == null || roles.length == 0) {//无角色限制的情况，直接放行
            return true;
        }
        for (String role : roles) {
            if (subject.hasRole(role)) {//如果当前主体拥有角色数组中的任意一个角色，返回true
                return true;
            }
        }
        return false;
    }
}
