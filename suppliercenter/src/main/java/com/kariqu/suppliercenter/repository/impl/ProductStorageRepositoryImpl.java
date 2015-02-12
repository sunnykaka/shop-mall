package com.kariqu.suppliercenter.repository.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.suppliercenter.domain.ProductStorage;
import com.kariqu.suppliercenter.repository.ProductStorageRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProductStorageRepositoryImpl extends SqlMapClientDaoSupport implements ProductStorageRepository {

    @Override
    public void createProductStorage(ProductStorage productStorage) {
        getSqlMapClientTemplate().insert("insertProductStorage", productStorage);
    }

    @Override
    public void updateProductStorage(ProductStorage productStorage) {
        getSqlMapClientTemplate().update("updateProductStorage", productStorage);
    }

    @Override
    public ProductStorage queryProductStorageById(int id) {
        return (ProductStorage) getSqlMapClientTemplate().queryForObject("selectProductStorageById", id);
    }

    @Override
    public void deleteProductStorageById(int id) {
        getSqlMapClientTemplate().delete("deleteProductStorageById", id);
    }

    @Override
    public List<ProductStorage> queryProductStorageByCustomerId(int customerId) {
        return getSqlMapClientTemplate().queryForList("queryProductStorageByCustomerId", customerId);
    }

    @Override
    public void deleteProductStorageByCustomerId(int customerId) {
        getSqlMapClientTemplate().delete("deleteProductStorageByCustomerId", customerId);
    }

    @Override
    public Page<ProductStorage> queryProductStorageByPage(int customerId, Page<ProductStorage> page){
        Map param = new HashMap();
        param.put("customerId", customerId);
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<ProductStorage> queryProductStorageByPage = getSqlMapClientTemplate().queryForList("queryProductStorageByPage", param);
        page.setResult(queryProductStorageByPage);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("selectCountForProductStorage", customerId));
        return page;
    }

    @Override
    public int queryProductStorageNumber(int customerId){
        return (Integer)getSqlMapClientTemplate().queryForObject("selectCountForProductStorage",customerId);
    }

    @Override
    public int querySkuCountByProductStorageId(int productStorageId){
        return (Integer)getSqlMapClientTemplate().queryForObject("querySkuCountByProductStorageId", productStorageId);
    }

    @Override
    public ProductStorage queryProductStorageByNameAndCustomerId(String name, int customerId){
        Map param = new HashMap();
        param.put("name", name);
        param.put("customerId", customerId);
        return (ProductStorage)getSqlMapClientTemplate().queryForObject("queryProductStorageByNameAndCustomerId", param);
    }
}
