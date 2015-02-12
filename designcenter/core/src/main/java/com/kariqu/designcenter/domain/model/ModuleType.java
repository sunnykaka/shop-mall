package com.kariqu.designcenter.domain.model;

import java.io.Serializable;

/**
 * 模块的类型，有公共模块和模板模块
 * @author Asion
 * @since 2011-4-16 下午07:02:04
 * @version 1.0.0
 */
public enum ModuleType implements Serializable{
    
    template,
    
    common;
    
    public boolean equals(String type){
        return this.toString().equals(type);
    }
}
