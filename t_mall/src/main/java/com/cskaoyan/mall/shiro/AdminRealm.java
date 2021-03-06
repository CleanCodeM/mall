package com.cskaoyan.mall.shiro;

import com.cskaoyan.mall.bean.Admin;
import com.cskaoyan.mall.mapper.AdminMapper;
import com.cskaoyan.mall.mapper.PermissionMapper;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class AdminRealm extends AuthorizingRealm {
    @Autowired
    AdminMapper adminMapper;
    @Autowired
    PermissionMapper permissionMapper;
    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        Admin admin = adminMapper.selectAdminByName(username);
        String passwordFromDb = admin.getPassword();
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(admin, passwordFromDb, getName());
        return authenticationInfo;
    }

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Admin primaryPrincipal = (Admin) principalCollection.getPrimaryPrincipal();
        //查表获取当前用户的权限
        int[] roleIds = primaryPrincipal.getRoleIds();
        List<String> permissions = new ArrayList<>();
        for (int roleId : roleIds) {
            List<String> list = permissionMapper.selectPermissionsByRoleId(roleId);
            permissions.addAll(list);
        }
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addStringPermissions(permissions);
        authorizationInfo.addStringPermission("auth:info");
        return authorizationInfo;
    }
}
