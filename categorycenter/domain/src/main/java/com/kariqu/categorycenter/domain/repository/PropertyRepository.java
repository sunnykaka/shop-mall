package com.kariqu.categorycenter.domain.repository;

import com.kariqu.categorycenter.domain.model.Property;

import java.util.List;

/**
 * User: Asion
 * Date: 11-6-25
 * Time: 下午2:36
 */
public interface PropertyRepository{

    Property getPropertyByName(String name);

    void createProperty(Property property);

    Property getPropertyById(int id);

    void updateProperty(Property property);

    void deleteAllProperty();

    List<Property> queryAllProperties();

    void deletePropertyById(int id);
}
