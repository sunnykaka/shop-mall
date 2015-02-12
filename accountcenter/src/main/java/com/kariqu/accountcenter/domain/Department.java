package com.kariqu.accountcenter.domain;

/**
 * 部门
 * User: Asion
 * Date: 12-3-12
 * Time: 下午1:52
 */
public class Department {
    
    private int id;

    /**
     * 部门名称
     */
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
