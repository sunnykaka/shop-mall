package com.kariqu.usercenter.domain;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-8-8
 * Time: 上午10:08
 */
public class SmsMould {
    private int id;
    private String content;
    private String description;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
