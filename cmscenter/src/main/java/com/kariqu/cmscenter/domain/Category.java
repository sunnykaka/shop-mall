package com.kariqu.cmscenter.domain;

import java.util.List;

/**
 * 栏目，我们只支持两极，栏目下的内容标题会当做第三级
 * 当parent是-1的时候是根
 * <p/>
 * 每个根栏目可指定一个首页模板来它的首页
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-8-23
 *        Time: 上午11:08
 */
public class Category {

    private int id;

    private String name;

    /**
     * 目录名，用来生成文件夹
     */
    private String directory;

    private int parent;

    /**
     * 类目优先级 数字越小越在前面
     */
    private int priority;

    private List<Category> children;


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

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
