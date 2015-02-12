package com.kariqu.designcenter.domain.model;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * 模块toolbar，主要用来对模块进行个性的控制，比如
 * 是否容许移动，删除等
 *
 * @Author: Tiger
 * @Since: 11-5-10 下午10:25
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class Toolbar implements Serializable {

    private static final String TOOLBAR_PATTERN = "data-id=\"{0}\" data-prototypeId=\"{1}\" data-isDeleteable=\"{2}\" data-isEditable=\"{3}\" data-url-edit=\"{4}\" data-url=\"{5}\" data-module-name=\"{6}\" ";

    private boolean isDelete;

    private boolean isEdit;

    private String editUrl;

    private String dataUrl;

    /**
     * 模块实例ID
     */
    private String moduleId;

    /**
     * 模块原型ID
     */
    private int prototypeId;

    private String moduleName = "";


    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public void setEditUrl(String editUrl) {
        this.editUrl = editUrl;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public void setPrototypeId(int prototypeId) {
        this.prototypeId = prototypeId;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public String toString() {
        MessageFormat messageFormat = new MessageFormat(TOOLBAR_PATTERN);
        return messageFormat.format(new Object[]{this.moduleId, this.prototypeId, isDelete, isEdit, editUrl, dataUrl, moduleName});
    }
}
