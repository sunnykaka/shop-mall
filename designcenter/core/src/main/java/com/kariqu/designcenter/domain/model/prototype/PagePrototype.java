package com.kariqu.designcenter.domain.model.prototype;

import java.io.Serializable;

/**
 * 页面原型，用户自定义页面可以通过页面原型来生成
 *
 * @author Tiger
 * @version 1.0
 * @since 12-4-18 下午1:53
 */
public class PagePrototype implements Serializable {
    /**
     * 页面原型id
     */
    private int id;

    /**
     * 页面原型名称
     */
    private String name;

    /**
     * 页面原型描述
     */
    private String description;

    /**
     * 页面原型的vm代码
     */
    private String pageCode;

    /**
     * 页面原型区域类型
     */
    private AreaType areaType;

    /**
     * 页面原型的xml配置文件内容
     */
    private String configContent;

    private PrototypeState prototypeState = PrototypeState.DEBUG;

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

    public String getPageCode() {
        return pageCode;
    }

    public void setPageCode(String pageCode) {
        this.pageCode = pageCode;
    }

    public String getConfigContent() {
        return configContent;
    }

    public void setConfigContent(String configContent) {
        this.configContent = configContent;
    }

    public AreaType getAreaType() {
        return areaType;
    }

    public void setAreaType(AreaType areaType) {
        this.areaType = areaType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PrototypeState getPrototypeState() {
        return prototypeState;
    }

    public void setPrototypeState(PrototypeState prototypeState) {
        this.prototypeState = prototypeState;
    }
}
