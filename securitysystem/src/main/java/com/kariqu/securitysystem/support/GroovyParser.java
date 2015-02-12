package com.kariqu.securitysystem.support;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * Groovy脚本解析工具
 * User: Asion
 * Date: 11-11-16
 * Time: 下午4:19
 */
public class GroovyParser {

    public static Script parseGroovy(String scriptCode,Binding binding) {
        if (StringUtils.isBlank(scriptCode)) {
            throw new RuntimeException("脚本文件为空，请检查");
        }
        if (binding == null) {
            binding = new Binding();
        }
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
        Class scriptClass = groovyClassLoader.parseClass(scriptCode);
        return InvokerHelper.createScript(scriptClass, binding);
    }
}
