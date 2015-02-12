package com.kariqu.categorycenter.domain.model;

import com.kariqu.securitysystem.domain.SecurityResource;

import java.util.LinkedList;
import java.util.List;

/**
 * 产品类目领域对象
 * 类目是一个树形结构，树的叶子上挂接商品，商品的属性和值来自于叶子类目
 * 类目：商品 可以类比为面向对象编程的 Class : Object
 * Class将一些Object归类，抽取出类的属性，在用Class构造对象的时候将属性值写入
 * 类目系统是将商品归类，抽取出类目属性和值，之所以有值，是因为电子商务系统中的商品的属性值是通过
 * 运营管理起来的，这样在一个类目上发布商品的时候，选择类目上设定的属性和值传递给商品即可。
 * <p/>
 * ProductCategory 1 * CategoryProperty
 * ProductCategory 1  CategoryProperty 1 * CategoryPropertyValue
 * 一个类目上可能有多个属性，一个属性又可能有多个值
 * <p/>
 * 商品最终只包含ProductCategory的引用和本就属于自己的Property和Value,他们用pid:vid表示，pid是Property的id,vid是Value的id
 *
 * @Author: Tiger
 * @Since: 11-6-25 下午1:03
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class ProductCategory extends DescendantManager implements SecurityResource {

    private int id;

    private String name;

    private ProductCategory parent;//这个类目的父亲

    private List<ProductCategory> children = new LinkedList<ProductCategory>(); //这个类目的儿子

    private String description = "";

    @Override
    public String getResourceName() {
        return "Category";
    }

    /**
     * 数据库返回的对象是不加载children的，所以这里在递归加载的时候set进来
     *
     * @param children
     */
    public void setChildren(List<ProductCategory> children) {
        this.children = children;
        for (ProductCategory child : children) {
            this.addDescendantCategoryId(child.getId());
            child.setParent(this); //设置父亲节点
        }
        if (this.getParent() != null) {
            //加到父亲的后代ID列表
            this.getParent().addDescendantCategoryId(this.getDescendantIds());
        }
    }


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

    public ProductCategory getParent() {
        return parent;
    }

    public void setParent(ProductCategory parent) {
        this.parent = parent;
    }

    public List<ProductCategory> getChildren() {
        return children;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
