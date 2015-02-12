package com.kariqu.designcenter.client.container;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端jar包嵌入式容器
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-1-12 下午02:53:13
 */
public class DesignCenterEmbedClientSpringContainer implements ApplicationContextAware, InitializingBean {

    /**
     * embed application context
     */
    public static ApplicationContext applicationContext;

    /**
     * parent applicationContext
     */
    private ApplicationContext parentApplicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.parentApplicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<String> configurations = new ArrayList<String>();
        configurations.add("applicationContext-client-render.xml");
        configurations.add("applicationContext-client-service.xml");
        applicationContext = new ClassPathXmlApplicationContext(configurations
            .toArray(new String[configurations.size()]), parentApplicationContext);
    }

    public static Object getBean(String beanName) {
        return applicationContext == null ? null : applicationContext.getBean(beanName);
    }

}
