package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.ProductCategory;
import com.kariqu.categorycenter.domain.repository.ProductCategoryRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-6-25
 * Time: 下午3:19
 */
public class ProductCategoryRepositoryImpl extends SqlMapClientDaoSupport implements ProductCategoryRepository {

    @Override
    public List<ProductCategory> querySubCategoryTree(int parentId) {
        return getSqlMapClientTemplate().queryForList("querySubCategories", parentId);
    }

    @Override
    public void create(ProductCategory productCategory) {
        getSqlMapClientTemplate().insert("insertProductCategory", productCategory);
    }

    @Override
    public ProductCategory getProductCategoryById(int id) {
        return (ProductCategory) getSqlMapClientTemplate().queryForObject("selectProductCategory", id);
    }

    @Override
    public void updateProductCategory(ProductCategory productCategory) {
        getSqlMapClientTemplate().update("updateProductCategory", productCategory);
    }

    @Override
    public void deleteProductCategoryById(int id) {
        getSqlMapClientTemplate().delete("deleteProductCategory", id);
    }

    @Override
    public void deleteAllProductCategory() {
        getSqlMapClientTemplate().delete("deleteAllProductCategory",new Date());
    }

    @Override
    public List<ProductCategory> queryAllProductCategories() {
        return getSqlMapClientTemplate().queryForList("selectAllProductCategory");
    }

    @Override
    public int queryProductCategoryByNameAndParentId(String name, int parentId) {
        Map param = new HashMap();
        param.put("name", name);
        param.put("parentId", parentId);
        return (Integer)getSqlMapClientTemplate().queryForObject("queryProductCategoryByNameAndParentId", param);
    }
}
