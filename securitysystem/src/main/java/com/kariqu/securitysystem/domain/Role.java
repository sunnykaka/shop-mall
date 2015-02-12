package com.kariqu.securitysystem.domain;


import com.kariqu.common.uri.UrlHelper;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 角色，具备一些权限的集合
 * 权限包括功能权限和资源权限
 * User: Asion
 * Date: 11-11-15
 * Time: 下午4:10
 */
public class Role implements Serializable {

    private int id;

    private String roleName;


    /**
     * 功能集合，这个字段表达的意思可用于UI层做菜单显示
     */
    private String functionSet;

    /**
     * 通过URL表达的功能权限
     */
    private List<UrlPermission> urlPermissions = new LinkedList<UrlPermission>();

    /**
     * 通过服务接口表达的功能权限
     */
    private List<ServicePermission> servicePermissions = new LinkedList<ServicePermission>();


    /**
     * 角色作用在资源上的范围设定
     * 用于对资源进行授权，比如高级经理和普通经理两个角色，在对合同进行审批的时候
     * 他们都具备了审批合同这个功能权限，但是普通经理只能审批50万以下的合同，而高级经理可以审批100万以下的合同。
     * 这个时候就对各自角色设定对资源的操作范围
     */
    private List<RoleScope> roleScopes = new LinkedList<RoleScope>();


    /**
     * 判断给定的URL是否可授权，如果发现权限路径中有*则使用通配,比如account/*表示account下的所有路径都授权
     * 这通常是对每个应用进行粗粒度的授权，如果没有就精确匹配
     *
     * @param url
     * @return
     */
    public boolean hasPermission(String url) {
        url = UrlHelper.normalizedUrl(url);
        boolean permit = false;
        for (UrlPermission urlPermission : urlPermissions) {
            String path = urlPermission.getPath();
            if (path.indexOf("*") != -1) {
                Pattern pattern = Pattern.compile(path.replace("*", "(.*)"));
                Matcher matcher = pattern.matcher(url);
                if (matcher.matches()) {
                    permit = true;
                    break;
                }
            } else if (url.equals(path)) {
                permit = true;
                break;
            }
        }
        return permit;
    }

    /**
     * 通过资源名称找到资源授权范围
     *
     * @param resourceName
     * @return
     */
    public RoleScope getRoleScope(String resourceName) {
        for (RoleScope roleScope : roleScopes) {
            if (roleScope.getResource().endsWith(resourceName)) {
                return roleScope;
            }
        }
        return null;
    }


    public void addUrlPermission(UrlPermission urlPermission) {
        urlPermissions.add(urlPermission);
    }

    public void addRoleScope(RoleScope roleScope) {
        roleScopes.add(roleScope);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<UrlPermission> getUrlPermissions() {
        return urlPermissions;
    }

    public void setUrlPermissions(List<UrlPermission> urlPermissions) {
        this.urlPermissions = urlPermissions;
    }

    public List<ServicePermission> getServicePermissions() {
        return servicePermissions;
    }

    public void setServicePermissions(List<ServicePermission> servicePermissions) {
        this.servicePermissions = servicePermissions;
    }

    public List<RoleScope> getRoleScopes() {
        return roleScopes;
    }

    public void setRoleScopes(List<RoleScope> roleScopes) {
        this.roleScopes = roleScopes;
    }

    public String getFunctionSet() {
        return functionSet;
    }

    public void setFunctionSet(String functionSet) {
        this.functionSet = functionSet;
    }
}
