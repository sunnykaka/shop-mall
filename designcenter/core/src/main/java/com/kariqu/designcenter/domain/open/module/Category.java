package com.kariqu.designcenter.domain.open.module;

/**
 * @author Tiger
 * @since 2011-5-3 下午08:44:36 
 * @version 1.0.0
 */
public class Category {
    
    private int id;
    
    private String name;

    /**
     * @param name
     */
    public Category(String name) {
        super();
        this.name = name;
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
}
