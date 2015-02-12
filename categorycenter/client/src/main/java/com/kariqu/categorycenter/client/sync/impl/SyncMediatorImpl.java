package com.kariqu.categorycenter.client.sync.impl;

import com.kariqu.categorycenter.client.sync.SyncMediator;
import com.kariqu.categorycenter.client.sync.SyncRequest;
import com.kariqu.categorycenter.client.sync.SyncResult;
import com.kariqu.categorycenter.client.sync.SyncType;
import com.kariqu.categorycenter.domain.model.*;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryProperty;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryPropertyValue;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.service.*;
import com.kariqu.common.file.SyncUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 同步类
 * 需要从类目服务器读取数据写到客户端的内存数据库
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-7-13 上午11:47
 */
public class SyncMediatorImpl implements SyncMediator {

    public static final String sync_file_name = "syncDate.dat";

    private static final Log logger = LogFactory.getLog(SyncMediatorImpl.class);

    @Autowired
    private ProductCategoryService productCategoryServiceClient;

    @Autowired
    private CategoryPropertyService categoryPropertyServiceClient;

    @Autowired
    private NavigateCategoryService navigateCategoryServiceClient;

    @Autowired
    private NavigateCategoryPropertyService navigateCategoryPropertyServiceClient;

    @Autowired
    private CategoryAssociationService categoryAssociationServiceClient;


    /**
     * 类目相关数据同步服务
     */
    private CategorySyncService categorySyncService;


    @Override
    public SyncResult sync(SyncRequest syncRequest) {
        SyncResult syncResult = new SyncResult();
        if (SyncType.all.equals(syncRequest.getSyncType())) {
            try {
                syncAll();
                SyncUtils.recordSyncTimeStamp(new Date(), sync_file_name);
            } catch (Exception e) {
                logger.error("推送类目数据出错", e);
                syncResult.setSuccess(false);

            }
            return syncResult;
        }

        if (SyncType.inc.equals(syncRequest.getSyncType())) {
            try {
                //获取上次更新记录的时间戳
                Date time = SyncUtils.getSyncTimeStamp(sync_file_name);
                syncNavAssociation();
                syncAddedData(time, syncResult);
                syncUpdatedData(time, syncResult);
                syncDeletedData(time, syncResult);
                SyncUtils.recordSyncTimeStamp(new Date(), sync_file_name);
            } catch (Exception e) {
                logger.error("增量推送类目数据出错", e);
                syncResult.setSuccess(false);
                return syncResult;
            }
        }
        return syncResult;

    }

    /**
     * 同步从上次同步以来所有被删除的数据
     *
     * @param time
     */
    private void syncDeletedData(Date time, SyncResult syncResult) {
        final List<ProductCategory> allProductCategories = categorySyncService.queryDeletedCategoriesFromGivingTime(time);
        syncResult.setDeletedProductCategory(allProductCategories);
        for (ProductCategory productCategory : allProductCategories) {
            productCategoryServiceClient.deleteProductCategory(productCategory.getId());
        }

        final List<CategoryProperty> allCategoryProperties = categorySyncService.queryDeletedCategoryPropertiesFromGivingTime(time);
        syncResult.setDeletedCategoryProperty(allCategoryProperties);
        for (CategoryProperty categoryProperty : allCategoryProperties) {
            categoryPropertyServiceClient.deleteCategoryProperty(categoryProperty.getId());
        }

        final List<CategoryPropertyValue> allCategoryPropertyValues = categorySyncService.queryDeletedCategoryPropertyValuesFromGivingTime(time);
        syncResult.setDeletedCategoryPropertyValue(allCategoryPropertyValues);
        for (CategoryPropertyValue categoryPropertyValue : allCategoryPropertyValues) {
            categoryPropertyServiceClient.deleteCategoryPropertyValue(categoryPropertyValue.getId());
        }

        final List<NavigateCategory> allNavigateCategories = categorySyncService.queryDeletedNavCategoriesFromGivingTime(time);
        syncResult.setDeletedNavigateCategory(allNavigateCategories);
        for (NavigateCategory navigateCategory : allNavigateCategories) {
            navigateCategoryServiceClient.deleteNavigateCategory(navigateCategory.getId());
        }

        final List<NavCategoryProperty> allNavCategoryProperties = categorySyncService.queryDeletedNavCategoryPropertiesFromGivingTime(time);
        syncResult.setDeletedNavCategoryProperty(allNavCategoryProperties);
        for (NavCategoryProperty navCategoryProperty : allNavCategoryProperties) {
            navigateCategoryPropertyServiceClient.deleteNavCategoryProperty(navCategoryProperty.getId());
        }

        final List<Property> allProperties = categorySyncService.queryDeletedPropertiesFromGivingTime(time);
        syncResult.setDeletedProperty(allProperties);
        for (Property property : allProperties) {
            categoryPropertyServiceClient.deleteProperty(property.getId());
        }

        final List<Value> allValues = categorySyncService.queryDeletedValuesFromGivingTime(time);
        syncResult.setDeletedValue(allValues);
        for (Value value : allValues) {
            categoryPropertyServiceClient.deleteValue(value.getId());
        }

        final List<PropertyValueDetail> allPropertyValueDetails = categorySyncService.queryDeletedPropertyValueDetailsFromGivingTime(time);
        syncResult.setDeletedPropertyValueDetail(allPropertyValueDetails);
        for (PropertyValueDetail propertyValueDetail : allPropertyValueDetails) {
            categoryPropertyServiceClient.deleteCategoryPropertyValueDetail(propertyValueDetail.getId());
        }

    }

    /**
     * 同步从上次同步以来所有更新的数据
     *
     * @param time
     */
    private void syncUpdatedData(Date time, SyncResult syncResult) {
        final List<ProductCategory> allProductCategories = categorySyncService.queryUpdatedCategoriesFromGivingTime(time);
        syncResult.setUpdatedProductCategory(allProductCategories);
        for (ProductCategory productCategory : allProductCategories) {
            productCategoryServiceClient.updateProductCategory(productCategory);
        }

        final List<CategoryProperty> allCategoryProperties = categorySyncService.queryUpdatedCategoryPropertiesFromGivingTime(time);
        syncResult.setUpdatedCategoryProperty(allCategoryProperties);
        for (CategoryProperty categoryProperty : allCategoryProperties) {
            categoryPropertyServiceClient.updateCategoryProperty(categoryProperty);
        }

        final List<CategoryPropertyValue> allCategoryPropertyValues = categorySyncService.queryUpdatedCategoryPropertyValuesFromGivingTime(time);
        syncResult.setUpdatedCategoryPropertyValue(allCategoryPropertyValues);
        for (CategoryPropertyValue categoryPropertyValue : allCategoryPropertyValues) {
            categoryPropertyServiceClient.updateCategoryPropertyValue(categoryPropertyValue);
        }

        final List<NavigateCategory> allNavigateCategories = categorySyncService.queryUpdatedNavCategoriesFromGivingTime(time);
        syncResult.setUpdatedNavigateCategory(allNavigateCategories);
        for (NavigateCategory navigateCategory : allNavigateCategories) {
            navigateCategoryServiceClient.updateNavigateCategory(navigateCategory);
        }

        final List<NavCategoryProperty> allNavCategoryProperties = categorySyncService.queryUpdatedNavCategoryPropertiesFromGivingTime(time);
        syncResult.setUpdatedNavCategoryProperty(allNavCategoryProperties);
        for (NavCategoryProperty navCategoryProperty : allNavCategoryProperties) {
            navigateCategoryPropertyServiceClient.updateNavCategoryProperty(navCategoryProperty);
        }

        final List<Property> allProperties = categorySyncService.queryUpdatedPropertiesFromGivingTime(time);
        syncResult.setUpdatedProperty(allProperties);
        for (Property property : allProperties) {
            categoryPropertyServiceClient.updateProperty(property);
        }

        final List<Value> allValues = categorySyncService.queryUpdatedValuesFromGivingTime(time);
        syncResult.setUpdatedValue(allValues);
        for (Value value : allValues) {
            categoryPropertyServiceClient.updateValue(value);
        }

        final List<PropertyValueDetail> allPropertyValueDetails = categorySyncService.queryUpdatedPropertyValueDetailsFromGivingTime(time);
        syncResult.setUpdatedPropertyValueDetail(allPropertyValueDetails);
        for (PropertyValueDetail propertyValueDetail : allPropertyValueDetails) {
            categoryPropertyServiceClient.updateCategoryPropertyValueDetail(propertyValueDetail);
        }

    }

    /**
     * 同步新增的数据
     *
     * @param time
     * @throws ParseException
     * @throws IOException
     */
    private void syncAddedData(Date time, SyncResult syncResult) throws ParseException, IOException {
        final List<ProductCategory> allProductCategories = categorySyncService.queryAddedCategoriesFromGivingTime(time);
        syncResult.setAddedProductCategory(allProductCategories);
        for (ProductCategory productCategory : allProductCategories) {
            productCategoryServiceClient.createProductCategory(productCategory);
        }

        final List<CategoryProperty> allCategoryProperties = categorySyncService.queryAddedCategoryPropertiesFromGivingTime(time);
        syncResult.setAddedCategoryProperty(allCategoryProperties);
        for (CategoryProperty categoryProperty : allCategoryProperties) {
            categoryPropertyServiceClient.createCategoryProperty(categoryProperty);
        }

        final List<CategoryPropertyValue> allCategoryPropertyValues = categorySyncService.queryAddedCategoryPropertyValuesFromGivingTime(time);
        syncResult.setAddedCategoryPropertyValue(allCategoryPropertyValues);
        for (CategoryPropertyValue categoryPropertyValue : allCategoryPropertyValues) {
            categoryPropertyServiceClient.createCategoryPropertyValue(categoryPropertyValue);
        }

        final List<NavigateCategory> allNavigateCategories = categorySyncService.queryAddedNavCategoriesFromGivingTime(time);
        syncResult.setAddedNavigateCategory(allNavigateCategories);
        for (NavigateCategory navigateCategory : allNavigateCategories) {
            navigateCategoryServiceClient.createNavigateCategory(navigateCategory);
        }

        final List<NavCategoryProperty> allNavCategoryProperties = categorySyncService.queryAddedNavCategoryPropertiesFromGivingTime(time);
        syncResult.setAddedNavCategoryProperty(allNavCategoryProperties);
        for (NavCategoryProperty navCategoryProperty : allNavCategoryProperties) {
            navigateCategoryPropertyServiceClient.createNavCategoryProperty(navCategoryProperty);
        }

        final List<Property> allProperties = categorySyncService.queryAddedPropertiesFromGivingTime(time);
        syncResult.setAddedProperty(allProperties);
        for (Property property : allProperties) {
            categoryPropertyServiceClient.createProperty(property);
        }

        final List<Value> allValues = categorySyncService.queryAddedValuesFromGivingTime(time);
        syncResult.setAddedValue(allValues);
        for (Value value : allValues) {
            categoryPropertyServiceClient.createValue(value);
        }

        final List<PropertyValueDetail> allPropertyValueDetails = categorySyncService.queryAddedPropertyValueDetailsFromGivingTime(time);
        syncResult.setAddedPropertyValueDetail(allPropertyValueDetails);
        for (PropertyValueDetail propertyValueDetail : allPropertyValueDetails) {
            categoryPropertyServiceClient.createCategoryPropertyValueDetail(propertyValueDetail);
        }
    }

    private void syncAll() throws IOException {
        //同步类目数据字典
        syncProperties();
        syncValues();

        //同步后台类目
        syncProductCategories();
        syncCategoryProperties();
        syncCategoryPropertyValues();
        syncPropertyValueDetails();

        //同步前台导航类目
        syncNavCategories();
        syncNavAssociation();
        syncNavCategoryProperties();
        syncNavCategoryPropertyValues();

        //记录同步的时间标识 用于增量同步时的查询条件
        SyncUtils.recordSyncTimeStamp(new Date(), sync_file_name);
    }

    /**
     * 同步前台类目与后台类目的关联关系表
     */
    private void syncNavAssociation() {
        categoryAssociationServiceClient.deleteAllAssociation();
        Map<Integer, List<Integer>> map = categorySyncService.queryAllAssociation();
        for (Integer navId : map.keySet()) {
            categoryAssociationServiceClient.createNavigateCategoryAssociation(navId, map.get(navId));
        }
    }

    /**
     * 同步前台类目属性关系表 nc_p
     */
    private void syncNavCategoryProperties() {
        navigateCategoryPropertyServiceClient.deleteAllNavCategoryProperty();
        final List<NavCategoryProperty> allNavCategoryProperties = categorySyncService.queryAllNavCategoryProperties();
        for (NavCategoryProperty navCategoryProperty : allNavCategoryProperties) {
            navigateCategoryPropertyServiceClient.createNavCategoryProperty(navCategoryProperty);
        }
    }

    /**
     * 同步前台类目属性值关系表 nc_p_v
     */
    private void syncNavCategoryPropertyValues() {
        navigateCategoryPropertyServiceClient.deleteAllNavCategoryPropertyValue();
        List<NavCategoryPropertyValue> navCategoryPropertyValues = categorySyncService.queryAllNavCategoryPropertyValues();
        for (NavCategoryPropertyValue navCategoryPropertyValue : navCategoryPropertyValues) {
            navigateCategoryPropertyServiceClient.createNavCategoryPropertyValue(navCategoryPropertyValue);
        }
    }

    /**
     * 同步前台类目 nc
     */
    private void syncNavCategories() {
        navigateCategoryServiceClient.deleteAllNavigateCategory();
        final List<NavigateCategory> allNavigateCategories = categorySyncService.queryAllNavCategories();
        for (NavigateCategory navigateCategory : allNavigateCategories) {
            navigateCategoryServiceClient.createNavigateCategory(navigateCategory);
        }
    }

    //====================以下两个就是同步类目的数据字典(属性名：例如颜色；属性值：例如红色)=======================

    /**
     * 同步属性值
     */
    private void syncValues() {
        categoryPropertyServiceClient.deleteAllValue();
        final List<Value> allValues = categorySyncService.queryAllValues();
        for (Value value : allValues) {
            categoryPropertyServiceClient.createValue(value);
        }
    }

    /**
     * 同步属性名
     */
    private void syncProperties() {
        categoryPropertyServiceClient.deleteAllProperty();
        final List<Property> allProperties = categorySyncService.queryAllProperties();
        for (Property property : allProperties) {
            categoryPropertyServiceClient.createProperty(property);
        }
    }

    /**
     * 同步属性值的描述 p_v_d 比如：图片、介绍
     */
    private void syncPropertyValueDetails() {
        categoryPropertyServiceClient.deleteAllCategoryPropertyValueDetail();
        final List<PropertyValueDetail> allPropertyValueDetails = categorySyncService.queryAllCategoryPropertyValueDetails();
        for (PropertyValueDetail propertyValueDetail : allPropertyValueDetails) {
            categoryPropertyServiceClient.createCategoryPropertyValueDetail(propertyValueDetail);
        }
    }

    /**
     * 同步后台类目属性值关系表 c_p_v
     */
    private void syncCategoryPropertyValues() {
        categoryPropertyServiceClient.deleteAllCategoryPropertyValue();
        final List<CategoryPropertyValue> allCategoryPropertyValues = categorySyncService.queryAllCategoryPropertyValues();
        for (CategoryPropertyValue categoryPropertyValue : allCategoryPropertyValues) {
            categoryPropertyServiceClient.createCategoryPropertyValue(categoryPropertyValue);
        }
    }

    /**
     * 同步后台类目属性关系表 c_p
     */
    private void syncCategoryProperties() {
        categoryPropertyServiceClient.deleteAllCategoryProperty();
        final List<CategoryProperty> allCategoryProperties = categorySyncService.queryAllCategoryProperties();
        for (CategoryProperty categoryProperty : allCategoryProperties) {
            categoryPropertyServiceClient.createCategoryProperty(categoryProperty);
        }
    }

    /**
     * 同步后台类目表 c
     */
    private void syncProductCategories() {
        productCategoryServiceClient.deleteAllProductCategory();
        final List<ProductCategory> allProductCategories = categorySyncService.queryAllProductCategories();
        for (ProductCategory productCategory : allProductCategories) {
            productCategoryServiceClient.createProductCategory(productCategory);
        }
    }

    /**
     * 手动注入的
     * @param syncService
     */
    @Override
    public void injectDomainCategorySyncService(CategorySyncService syncService) {
        this.categorySyncService = syncService;
    }
}
