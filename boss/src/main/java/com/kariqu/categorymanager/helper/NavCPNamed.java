package com.kariqu.categorymanager.helper;

/**
 * 带有名字的导航类目属性
 * User: Asion
 * Date: 11-11-7
 * Time: 下午4:06
 */
public class NavCPNamed {

    private int id;    //类目属性ID，用它来更新数据库中的记录

    private String name;

    private int priority;

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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
