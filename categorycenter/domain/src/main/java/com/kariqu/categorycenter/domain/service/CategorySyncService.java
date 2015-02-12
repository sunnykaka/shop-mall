package com.kariqu.categorycenter.domain.service;

import com.kariqu.categorycenter.domain.model.*;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryProperty;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryPropertyValue;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类目的同步服务，提供给类目客户端查询增加，删除，更新的数据
 * 如果一个类目被增加但马上又更改了，则客户端拿到数据增加的和更新的都有个这个数据，那么
 * 客户端必须先把数据添加再更新，不然可能报错
 * @author Tiger
 * @version 1.0.0
 * @since 11-7-13 下午2:28
 */
public interface CategorySyncService {

    /**
     * 查询从指定时间以来新增加的后台类目
     *
     * @param time
     * @return
     */
    List<ProductCategory> queryAddedCategoriesFromGivingTime(Date time);

    /**
     * 获取从指定时间以来新增加的前台类目
     *
     * @param time
     * @return
     */
    List<NavigateCategory> queryAddedNavCategoriesFromGivingTime(Date time);

    /**
     * 查询指定时间以后新增的属性
     *
     * @param time
     * @return
     */
    List<Property> queryAddedPropertiesFromGivingTime(Date time);

    /**
     * 查询指定时间以来新增的值
     *
     * @param time
     * @return
     */
    List<Value> queryAddedValuesFromGivingTime(Date time);

    /**
     * 查询指定的时间以来新增的类目属性
     *
     * @param time
     * @return
     */
    List<CategoryProperty> queryAddedCategoryPropertiesFromGivingTime(Date time);

    /**
     * 查询指定时间以来新增的类目属性值
     *
     * @param time
     * @return
     */
    List<CategoryPropertyValue> queryAddedCategoryPropertyValuesFromGivingTime(Date time);

    /**
     * 查询从指定的时间以来新增的属性值详情
     *
     * @param time
     * @return
     */
    List<PropertyValueDetail> queryAddedPropertyValueDetailsFromGivingTime(Date time);

    /**
     * 查询从指定的时间以来新增加的导航类目属性
     *
     * @param time
     * @return
     */
    List<NavCategoryProperty> queryAddedNavCategoryPropertiesFromGivingTime(Date time);

    //以下是获取更新的数据接口

    /**
     * 查询从指定时间以来更新加的后台类目
     *
     * @param time
     * @return
     */
    List<ProductCategory> queryUpdatedCategoriesFromGivingTime(Date time);

    /**
     * 获取从指定时间以来更新加的前台类目
     *
     * @param time
     * @return
     */
    List<NavigateCategory> queryUpdatedNavCategoriesFromGivingTime(Date time);

    /**
     * 查询指定时间以后更新的属性
     *
     * @param time
     * @return
     */
    List<Property> queryUpdatedPropertiesFromGivingTime(Date time);

    /**
     * 查询指定时间以来更新的值
     *
     * @param time
     * @return
     */
    List<Value> queryUpdatedValuesFromGivingTime(Date time);

    /**
     * 查询指定的时间以来更新的类目属性
     *
     * @param time
     * @return
     */
    List<CategoryProperty> queryUpdatedCategoryPropertiesFromGivingTime(Date time);

    /**
     * 查询指定时间以来更新的类目属性值
     *
     * @param time
     * @return
     */
    List<CategoryPropertyValue> queryUpdatedCategoryPropertyValuesFromGivingTime(Date time);

    /**
     * 查询从指定的时间以来更新的属性值详情
     *
     * @param time
     * @return
     */
    List<PropertyValueDetail> queryUpdatedPropertyValueDetailsFromGivingTime(Date time);

    /**
     * 查询从指定的时间以来更新的导航类目属性
     *
     * @param time
     * @return
     */
    List<NavCategoryProperty> queryUpdatedNavCategoryPropertiesFromGivingTime(Date time);


    //以下是查询所有删除数据的接口
    /**
     * 查询从指定时间以来删除的后台类目
     *
     * @param time
     * @return
     */
    List<ProductCategory> queryDeletedCategoriesFromGivingTime(Date time);

    /**
     * 获取从指定时间以来删除的前台类目
     *
     * @param time
     * @return
     */
    List<NavigateCategory> queryDeletedNavCategoriesFromGivingTime(Date time);

    /**
     * 查询指定时间以后删除的属性
     *
     * @param time
     * @return
     */
    List<Property> queryDeletedPropertiesFromGivingTime(Date time);

    /**
     * 查询指定时间以来删除的值
     *
     * @param time
     * @return
     */
    List<Value> queryDeletedValuesFromGivingTime(Date time);

    /**
     * 查询指定的时间以来删除的类目属性
     *
     * @param time
     * @return
     */
    List<CategoryProperty> queryDeletedCategoryPropertiesFromGivingTime(Date time);

    /**
     * 查询指定时间以来删除的类目属性值
     *
     * @param time
     * @return
     */
    List<CategoryPropertyValue> queryDeletedCategoryPropertyValuesFromGivingTime(Date time);

    /**
     * 查询从指定的时间以来删除的属性值详情
     *
     * @param time
     * @return
     */
    List<PropertyValueDetail> queryDeletedPropertyValueDetailsFromGivingTime(Date time);

    /**
     * 查询从指定的时间以来删除加的导航类目属性
     *
     * @param time
     * @return
     */
    List<NavCategoryProperty> queryDeletedNavCategoryPropertiesFromGivingTime(Date time);


    List<ProductCategory> queryAllProductCategories();


    List<CategoryProperty> queryAllCategoryProperties();

    List<CategoryPropertyValue> queryAllCategoryPropertyValues();

    List<PropertyValueDetail> queryAllCategoryPropertyValueDetails();

    List<Property> queryAllProperties();

    List<Value> queryAllValues();

    List<NavigateCategory> queryAllNavCategories();

    List<NavCategoryPropertyValue> queryAllNavCategoryPropertyValues();

    List<NavCategoryProperty> queryAllNavCategoryProperties();

    Map<Integer,List<Integer>> queryAllAssociation();
}
