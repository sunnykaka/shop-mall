package com.kariqu.securitysystem.domain;


import java.io.Serializable;

/**
 * 角色数据或者资源权限，角色具备一个权限集合，权限包含资源，RoleScope表示某个角色在它所操纵的资源上的范围
 * 比如高级经理可审批100万合同，普通经理可审批50万合同，他们都具备了审批合同这个功能权限
 * 角色和资源可以唯一确定一条记录
 * User: Asion
 * Date: 11-11-16
 * Time: 下午1:24
 */
public class RoleScope implements Serializable {

    private int id;

    /**
     * 角色
     */
    private int roleId;

    /**
     * 资源，对应SecurityResource的getResourceName();
     */
    private String resource;

    /**
     * 资源权限授权脚本
     */
    private String resourceAuthScript;

    /**
     * UI权限授权脚本
     */
    private String uiAuthScript;

    /**
     * 操作范围或者域，这是很灵活的，比如某个人的类目操作范围：厨房用品，锅具，奶锅，这个值和脚本配合进行授权
     */
    private String scopeValue;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getScopeValue() {
        return scopeValue;
    }

    public void setScopeValue(String scopeValue) {
        this.scopeValue = scopeValue;
    }

    public String getResourceAuthScript() {
        return resourceAuthScript;
    }

    public void setResourceAuthScript(String resourceAuthScript) {
        this.resourceAuthScript = resourceAuthScript;
    }

    public String getUiAuthScript() {
        return uiAuthScript;
    }

    public void setUiAuthScript(String uiAuthScript) {
        this.uiAuthScript = uiAuthScript;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}
