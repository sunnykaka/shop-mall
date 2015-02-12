package com.kariqu.categorycenter.domain.repository;

import com.kariqu.categorycenter.domain.model.PropertyValueDetail;

import java.util.List;

/**
 * User: Asion
 * Date: 11-6-25
 * Time: 下午2:41
 */
public interface PropertyValueDetailRepository{

    /* 通过属性ID和值ID查询 */
    PropertyValueDetail getPropertyValueDetail(int propertyId, int valueId);

    List<PropertyValueDetail> queryAllPropertyValueDetails();

    void deleteAllPropertyValueDetail();

    void deletePropertyValueDetailById(int id);

    void updatePropertyValueDetail(PropertyValueDetail propertyValueDetail);

    void createPropertyValueDetail(PropertyValueDetail propertyValueDetail);

    PropertyValueDetail getPropertyValueDetailById(int id);
}
