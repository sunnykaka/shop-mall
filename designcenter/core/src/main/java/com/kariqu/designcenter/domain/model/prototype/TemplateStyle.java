package com.kariqu.designcenter.domain.model.prototype;

import java.io.Serializable;

/**
 * 模板风格领域对象，一个模板可以有多个风格
 * 
 * @author Tiger
 * @since 2010-12-16 下午12:30:14
 * @version 1.0
 */
public class TemplateStyle implements Serializable {

    private static final long serialVersionUID = 8052791493715084206L;

    private int id;

    private String name;

    private String description;

    /**
     * 本风格对应的css文件资源的id
     */
    private int styleResourceId;
        
    private int templateVersionId;
    

    /**
     * @return the styleResourceId
     */
    public int getStyleResourceId() {
        return styleResourceId;
    }

    /**
     * @param styleResourceId the styleResourceId to set
     */
    public void setStyleResourceId(int styleResourceId) {
        this.styleResourceId = styleResourceId;
    }

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

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
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

    /**
     * @return the templateVersionId
     */
    public int getTemplateVersionId() {
        return templateVersionId;
    }

    /**
     * @param templateVersionId the templateVersionId to set
     */
    public void setTemplateVersionId(int templateVersionId) {
        this.templateVersionId = templateVersionId;
    }
    
    
    
  
}
