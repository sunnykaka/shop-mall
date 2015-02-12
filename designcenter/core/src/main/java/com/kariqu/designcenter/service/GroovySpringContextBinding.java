package com.kariqu.designcenter.service;

import groovy.lang.Binding;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * 注入Spring的上下文对象到Groovy的上下文
 * 在Groovy脚本中可直接引用Spring中的bean
 *
 * @Author: Tiger
 * @Since: 11-8-6 下午4:20
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */

public class GroovySpringContextBinding extends Binding implements ApplicationContextAware, InitializingBean {

    private static final String Groovy_Logger_Name = "groovy";


    private ApplicationContext applicationContext;

    private Map<String, Object> variables = new HashMap<String, Object>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object getVariable(String name) {
        Object variable = variables.get(name);
        if (null == variable) {
            variable = null == applicationContext ? null : applicationContext.getBean(name);
        }
        return variable;
    }

    @Override
    public void setVariable(String name, Object value) {
        this.variables.put(name, value);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.variables.put("log", LogFactory.getLog(Groovy_Logger_Name));
    }

    @Override
    public Map getVariables() {
        return variables;
    }
}
