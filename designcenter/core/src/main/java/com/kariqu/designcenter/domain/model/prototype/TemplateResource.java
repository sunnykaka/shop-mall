package com.kariqu.designcenter.domain.model.prototype;

import java.io.Serializable;

/**
 * 模板资源文件，主要包括图片，css,js
 * @author Tiger
 * @since 2011-4-7 下午08:13:58 
 * @version 1.0.0
 */
public class TemplateResource implements Serializable{
    
    private static final long serialVersionUID = 5197504079185158473L;

    private int id;
    
    /**
     * 模板资源所属
     */
    private int templateVersionId;
    
    private String name;
    
    private String description;
    
    private String content;
    
    private byte[] byteData;
    
    private ResourceType resourceType;
    
    private String resourceKey;
    

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
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the resourceType
     */
    public ResourceType getResourceType() {
        return resourceType;
    }

    /**
     * @param resourceType the resourceType to set
     */
    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * @return the resourceKey
     */
    public String getResourceKey() {
        return resourceKey;
    }

    /**
     * @param resourceKey the resourceKey to set
     */
    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * @return the byteData
     */
    public byte[] getByteData() {
        return byteData;
    }

    /**
     * @param byteData the byteData to set
     */
    public void setByteData(byte[] byteData) {
        this.byteData = byteData;
    }

    
    
    
    
}
