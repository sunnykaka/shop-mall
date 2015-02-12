package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.PropertyType;
import com.kariqu.productcenter.domain.ProductProperty;
import com.kariqu.productcenter.repository.ProductPropertyRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-11-8
 * Time: 上午11:26
 */
public class ProductPropertyRepositoryImpl extends SqlMapClientDaoSupport implements ProductPropertyRepository {

    @Override
    public void createProductProperty(ProductProperty value) {
        getSqlMapClientTemplate().insert("insertProductProperty", value);
    }

    @Override
    public ProductProperty queryProductPropertyByPropertyType(int productId, PropertyType propertyType) {
        Map map = new HashMap();
        map.put("productId", productId);
        map.put("propertyType", propertyType);
        return (ProductProperty) getSqlMapClientTemplate().queryForObject("queryProductPropertyByPropertyType", map);
    }

    @Override
    public void deleteProductPropertyByPropertyType(int productId, PropertyType propertyType) {
        Map map = new HashMap();
        map.put("productId", productId);
        map.put("propertyType", propertyType);
        getSqlMapClientTemplate().delete("deleteProductPropertyByPropertyType", map);
    }

    @Override
    public void updateProductPropertyBrandJsonByPropertyType(String json, int productId, PropertyType propertyType) {
        Map map = new HashMap();
        map.put("json", json);
        map.put("productId", productId);
        map.put("propertyType", propertyType);
        getSqlMapClientTemplate().delete("updateProductPropertyBrandJsonByPropertyType", map);
    }

    @Override
    public List<ProductProperty> queryByProductId(int productId) {
        return getSqlMapClientTemplate().queryForList("queryProductPropertyByProductId", productId);
    }

    @Override
    public ProductProperty getProductPropertyByProductId(int productId) {
        return (ProductProperty) getSqlMapClientTemplate().queryForObject("queryProductPropertyByProductId", productId);
    }

    @Override
    public void updateProductProperty(ProductProperty value) {
        throw new UnsupportedOperationException("不提供更新，直接删除和增加");
    }

    @Override
    public void deleteProductPropertyByProductId(int productId) {
        getSqlMapClientTemplate().delete("deleteProductProperty", productId);
    }

    @Override
    public List<ProductProperty> queryAllProductProperty() {
        return getSqlMapClientTemplate().queryForList("selectAllProductProperty");
    }
}
