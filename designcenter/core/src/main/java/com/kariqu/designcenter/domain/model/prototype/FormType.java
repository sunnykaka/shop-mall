package com.kariqu.designcenter.domain.model.prototype;

import java.io.Serializable;

/**
 * 表单输入组件类型
 *
 * @Author: Tiger
 * @Since: 2011-5-8 12:10:35
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public enum FormType implements Serializable {

    text,

    textarea,

    select,

    radio,

    checkbox;

    public boolean equals(String formType) {
        return toString().equals(formType);
    }

}
