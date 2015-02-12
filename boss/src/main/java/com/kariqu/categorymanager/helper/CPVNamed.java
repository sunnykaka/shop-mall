package com.kariqu.categorymanager.helper;

/**
 * 带有名字的类目属性值
 * User: Asion
 * Date: 11-7-19
 * Time: 下午3:52
 */
public class CPVNamed {

    private int id;

    private int cid;

    private int pid;

    private int vid;

    private int priority;

    private String name;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
