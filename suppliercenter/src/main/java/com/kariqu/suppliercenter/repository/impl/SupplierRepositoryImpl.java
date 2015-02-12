package com.kariqu.suppliercenter.repository.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.suppliercenter.domain.Supplier;
import com.kariqu.suppliercenter.domain.SupplierAccount;
import com.kariqu.suppliercenter.repository.SupplierRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SupplierRepositoryImpl extends SqlMapClientDaoSupport implements SupplierRepository {

    @Override
    public void createSupplier(Supplier supplier) {
        getSqlMapClientTemplate().insert("insertCustomer", supplier);
    }

    @Override
    public Supplier querySupplierByName(String name) {
        return (Supplier) getSqlMapClientTemplate().queryForObject("selectCustomerByName", name);
    }

    @Override
    public List<Supplier> queryAllSupplier() {
        return getSqlMapClientTemplate().queryForList("queryAllCustomer");
    }

    @Override
    public void updateSupplier(Supplier customer) {
        getSqlMapClientTemplate().update("updateCustomer", customer);
    }

    @Override
    public Supplier querySupplierById(int id) {
        return (Supplier) getSqlMapClientTemplate().queryForObject("selectCustomerById", id);
    }

    @Override
    public void deleteSupplierById(int id) {
        getSqlMapClientTemplate().delete("deleteCustomerById", id);
    }

    @Override
    public Page<Supplier> querySupplierByPage(Page<Supplier> page) {
        Map param = new HashMap();
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<Supplier> queryCustomerByPage = getSqlMapClientTemplate().queryForList("queryCustomerByPage", param);
        page.setResult(queryCustomerByPage);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("selectCountForCustomer"));
        return page;
    }


    /**
     * 商家账户操作
     * @param customerId
     * @param page
     * @return
     */
    @Override
    public Page<SupplierAccount> querySupplierAccountByPage(int customerId, Page<SupplierAccount> page) {
        Map param = new HashMap();
        param.put("customerId", customerId);
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<SupplierAccount> querySupplierAccountByPage = getSqlMapClientTemplate().queryForList("querySupplierAccountByPage", param);
        page.setResult(querySupplierAccountByPage);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("selectCountForSupplierAccount", customerId));
        return page;
    }

    @Override
    public List<SupplierAccount> querySupplierAccountBySupplierId(int supplierId,String search) {
        Map map=new HashMap();
        map.put("supplierId",supplierId);
        map.put("accountName",search);
        return getSqlMapClientTemplate().queryForList("querySupplierAccountBySupplierId", map);
    }

    @Override
    public void insertSupplierAccount(SupplierAccount supplierAccount) {
        getSqlMapClientTemplate().insert("insertSupplierAccount", supplierAccount);
    }

    @Override
    public SupplierAccount querySupplierAccountByName(String accountName,int supplierId) {
        Map param=new HashMap();
        param.put("accountName",accountName);
        param.put("customerId",supplierId);
        return (SupplierAccount) getSqlMapClientTemplate().queryForObject("querySupplierAccountByName", param);
    }

    @Override
    public SupplierAccount getSupplierAccountById(int id) {
        return (SupplierAccount) getSqlMapClientTemplate().queryForObject("getSupplierAccountById", id);
    }

    @Override
    public void updateSupplierAccount(SupplierAccount supplierAccount) {
        getSqlMapClientTemplate().update("updateSupplierAccount", supplierAccount);
    }

    @Override
    public void deleteSupplierAccountById(int id) {
        getSqlMapClientTemplate().delete("deleteSupplierAccountById", id);
    }

    @Override
    public void deleteSupplierAccountByCustomerId(int customerId) {
        getSqlMapClientTemplate().delete("deleteSupplierAccountByCustomerId", customerId);
    }
}
