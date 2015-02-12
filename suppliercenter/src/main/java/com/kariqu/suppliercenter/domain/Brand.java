package com.kariqu.suppliercenter.domain;

/**
 * 品牌对象
 * User: Asion
 * Date: 12-6-20
 * Time: 下午2:39
 */
public class Brand {

    private int id;

    private String name;

    private int customerId;

    /** 品牌 logo 图 */
    private String picture;

    /** 品牌的文字描述 */
    private String desc;

    /** 品牌故事 */
    private String story;

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

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }
}
