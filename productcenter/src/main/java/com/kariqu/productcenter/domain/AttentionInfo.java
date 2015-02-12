package com.kariqu.productcenter.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * User: wendy
 * Date: 12-7-19
 * Time: 下午3:13
 */
public class AttentionInfo {

    private int id;


    private int productId;

    /**
     * 用来标识是使用注意事项（Use）还是保养注意（Maintenance）
     */
    private Type type;

    private String info;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static enum Type {
        //使用注意
        Use,
        //保养注意
        Maintenance;

        private static Map<Type, String> mapping = new HashMap<Type, String>();

        static {
            mapping.put(Use, "保养注意");
            mapping.put(Maintenance, "使用注意");
        }

        public String toDesc() {
            return mapping.get(this);
        }

    }
}
