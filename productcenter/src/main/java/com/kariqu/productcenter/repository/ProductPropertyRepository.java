package com.kariqu.productcenter.repository;

import com.kariqu.categorycenter.domain.model.PropertyType;
import com.kariqu.productcenter.domain.ProductProperty;

import java.util.List;

/**
 * User: Asion
 * Date: 11-11-8
 * Time: 上午11:25
 */
public interface ProductPropertyRepository{

    ProductProperty queryProductPropertyByPropertyType(int productId, PropertyType type);

    void deleteProductPropertyByPropertyType(int productId, PropertyType propertyType);

    void updateProductPropertyBrandJsonByPropertyType(String json,int productId, PropertyType propertyType);

    List<ProductProperty> queryByProductId(int productId);

    ProductProperty getProductPropertyByProductId(int productId);

    void deleteProductPropertyByProductId(int productId);

    List<ProductProperty> queryAllProductProperty();

    void updateProductProperty(ProductProperty value);

    void createProductProperty(ProductProperty value);
}
