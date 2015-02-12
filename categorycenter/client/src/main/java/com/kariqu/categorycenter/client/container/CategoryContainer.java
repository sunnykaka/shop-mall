package com.kariqu.categorycenter.client.container;

import com.kariqu.categorycenter.client.service.CategoryPropertyQueryService;
import com.kariqu.categorycenter.client.service.CategoryTree;
import com.kariqu.categorycenter.client.sync.SyncMediator;
import com.kariqu.categorycenter.domain.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 类目客户端的Spring容器
 * <p/>
 * 这是一个独立的spring容器，他提供了访问内存数据库hsql的服务
 *
 * @Author: Tiger
 * @Since: 11-6-30 下午10:36
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class CategoryContainer implements ApplicationContextAware, InitializingBean {

    protected static final Log logger = LogFactory.getLog(CategoryContainer.class);

    /**
     * 子容器
     */
    public static ApplicationContext applicationContext;

    /**
     * 父容器
     */
    private ApplicationContext parentApplicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.parentApplicationContext = applicationContext;
    }

    public static CategoryPropertyQueryService getCategoryPropertyQueryService() {
        return applicationContext.getBean(CategoryPropertyQueryService.class);
    }

    public static SyncMediator getSyncMediator() {
        return applicationContext.getBean(SyncMediator.class);
    }

    public static CategoryTree getCategoryTree() {
        return applicationContext.getBean(CategoryTree.class);
    }

    public static ProductCategoryService getProductCategoryService() {
        return applicationContext.getBean(ProductCategoryService.class);
    }

    public static NavigateCategoryService getNavigateCategoryService() {
        return applicationContext.getBean(NavigateCategoryService.class);
    }

    public static CategoryPropertyService getCategoryPropertyService() {
        return applicationContext.getBean(CategoryPropertyService.class);
    }

    public static NavigateCategoryPropertyService getNavigateCategoryPropertyService() {
        return applicationContext.getBean(NavigateCategoryPropertyService.class);
    }

    public static CategoryAssociationService getCategoryAssociationService() {
        return applicationContext.getBean(CategoryAssociationService.class);

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //容器独立启动
        applicationContext = new ClassPathXmlApplicationContext("category-client.xml");
        SyncMediator syncMediator = getSyncMediator();
        //将父容器中的mysql类目同步服务注入
        syncMediator.injectDomainCategorySyncService(parentApplicationContext.getBean(CategorySyncService.class));
    }

}
