package com.kariqu.suppliercenter.repository.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.suppliercenter.domain.Brand;
import com.kariqu.suppliercenter.repository.BrandRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BrandRepositoryImpl extends SqlMapClientDaoSupport implements BrandRepository {

    @Override
    public void createBrand(Brand brand) {
        getSqlMapClientTemplate().insert("insertBrand", brand);
    }

    @Override
    public void updateBrand(Brand brand) {
        getSqlMapClientTemplate().update("updateBrand", brand);
    }

    @Override
    public Brand queryBrandById(int id) {
        return (Brand) getSqlMapClientTemplate().queryForObject("selectBrandById", id);
    }

    @Override
    public void deleteBrandById(int id) {
        getSqlMapClientTemplate().delete("deleteBrandById", id);
    }

    @Override
    public List<Brand> queryBrandByCustomerId(int customerId) {
        return getSqlMapClientTemplate().queryForList("queryBrandByCustomerId", customerId);
    }

    @Override
    public void deleteBrandByCustomerId(int customerId) {
        getSqlMapClientTemplate().delete("deleteBrandByCustomerId", customerId);
    }

    @Override
    public Page<Brand> queryBrandByPage(int customerId, Page<Brand> page) {
        Map param = new HashMap();
        param.put("customerId", customerId);
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<Brand> queryBrandPage = getSqlMapClientTemplate().queryForList("queryBrandByPage", param);
        page.setResult(queryBrandPage);
        page.setTotalCount((Integer)getSqlMapClientTemplate().queryForObject("selectCountForBrand",customerId));
        return page;
    }

    @Override
    public List<Brand> queryAllBrand() {
        return getSqlMapClientTemplate().queryForList("selectAllBrand");
    }

    @Override
    public Brand queryBrandByName(String name){
        return (Brand)getSqlMapClientTemplate().queryForObject("queryBrandByName", name);
    }
}
