package com.kariqu.usercenter.domain;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-8-8
 * Time: 上午10:06
 */
public class SmsCharacter {
    private int id;
    private String value;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
