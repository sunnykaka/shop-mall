package com.kariqu.designcenter.domain.model.prototype;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 参数对象
 *
 * @author Tiger
 * @version 1.0
 * @since 2010-12-15 下午07:24:33
 */
public class Parameter implements Serializable {

    private static final long serialVersionUID = 3658393300600265814L;

    private FormType formType;

    private DataType dataType;

    private String label;

    private String description;

    private List<Option> options = new LinkedList<Option>();

    private String name;

    private Object value;


    /**
     * 根据参数名和参数值来构造，text,textarea
     *
     * @param paramName
     * @param paramValue
     */
    public Parameter(String paramName, Object paramValue) {
        this.name = paramName;
        this.value = paramValue;
    }

    /**
     * 更具参数名和option列表来构造，radio和select
     *
     * @param name
     * @param options
     */
    public Parameter(String name, List<Option> options) {
        this.options = options;
        this.name = name;
    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        if (!options.isEmpty()) {
            for (Option option : options) {
                if (option.isSelected())
                    return option.getValue();
            }
            return "";
        }
        return value;
    }

    public FormType getFormType() {
        return formType;
    }

    public void setFormType(FormType formType) {
        this.formType = formType;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
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
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }


}
