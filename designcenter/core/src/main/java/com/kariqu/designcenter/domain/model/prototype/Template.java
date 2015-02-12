package com.kariqu.designcenter.domain.model.prototype;

import java.io.Serializable;


/**
 * 模板
 * 
 * @author Tiger
 * @since 2010-12-15 下午07:30:16
 * @version 1.0
 */
public class Template implements Serializable {

    private static final long serialVersionUID = -5641682484818601582L;

    /**
     * 模板ID
     */
    private int id;

    /**
     * 模板名称
     */
    private String name;
    
    /**
     * 模板描述
     */
    private String description;

    /**
     * 模板类型，表示模板采用的语言，jsp,vm,php模板等
     */
    private TemplateType templateType;


    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }


 
    public TemplateType getTemplateType() {
        return templateType;
    }

    public void setTemplateType(TemplateType templateType) {
        this.templateType = templateType;
    }
    
    

   
}
