package com.kariqu.securitysystem.domain;

import java.util.LinkedList;
import java.util.List;

/**
 * 用户组
 * User: Asion
 * Date: 11-11-15
 * Time: 下午4:55
 */
public class UserGroup {

    private int id;

    private String groupName;

    private String groupDesc;

    private List<Role> roles = new LinkedList<Role>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
