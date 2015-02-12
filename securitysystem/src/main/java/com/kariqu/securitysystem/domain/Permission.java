package com.kariqu.securitysystem.domain;

import java.io.Serializable;

/**
 * 权限
 * 对Resource进行某种操作，比如对模板进行删除，对商品进行修改
 * User: Asion
 * Date: 11-11-15
 * Time: 下午4:54
 */
public abstract class Permission implements Serializable {

    private int id;

    /**
     * 资源
     */
    private String resource;

    /**
     * 权限名称
     */
    private String permissionName;


    /**
     * 分类，用于组织权限列表，以便于角色选择定位
     */
    private String category;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
