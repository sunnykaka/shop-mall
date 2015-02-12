package com.kariqu.suppliercenter.service.impl;

import com.kariqu.categorycenter.domain.model.Value;
import com.kariqu.categorycenter.domain.service.CategoryPropertyService;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.suppliercenter.domain.*;
import com.kariqu.suppliercenter.repository.BrandRepository;
import com.kariqu.suppliercenter.repository.SupplierRepository;
import com.kariqu.suppliercenter.repository.ProductStorageRepository;
import com.kariqu.suppliercenter.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: Asion
 * Date: 12-6-20
 * Time: 下午2:32
 */

public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductStorageRepository productStorageRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryPropertyService categoryPropertyService;


    @Override
    public void createCustomer(Supplier customer) {
        supplierRepository.createSupplier(customer);
    }

    @Override
    public void updateCustomer(Supplier customer) {
        supplierRepository.updateSupplier(customer);
    }

    @Override
    public Supplier queryCustomerById(int id) {
        return supplierRepository.querySupplierById(id);
    }

    @Override
    public void deleteCustomerById(int id) {
        supplierRepository.deleteSupplierById(id);
        productStorageRepository.deleteProductStorageByCustomerId(id);
        brandRepository.deleteBrandByCustomerId(id);
        supplierRepository.deleteSupplierAccountByCustomerId(id);
    }

    @Override
    public void createProductStorage(ProductStorage productStorage) {
        productStorageRepository.createProductStorage(productStorage);
    }

    @Override
    public void updateProductStorage(ProductStorage productStorage) {
        productStorageRepository.updateProductStorage(productStorage);
    }

    @Override
    public ProductStorage queryProductStorageById(int id) {
        return productStorageRepository.queryProductStorageById(id);
    }

    @Override
    public void deleteProductStorageById(int id) {
        productStorageRepository.deleteProductStorageById(id);
    }

    @Override
    public List<Supplier> queryAllCustomer() {
        return supplierRepository.queryAllSupplier();
    }

    @Override
    public List<ProductStorage> queryProductStorageByCustomerId(int customerId) {
        return productStorageRepository.queryProductStorageByCustomerId(customerId);
    }


    @Override
    public void createBrand(Brand brand) {
        Value value = new Value();
        value.setValueName(brand.getName());
        int brandId = categoryPropertyService.createValueIfNotExist(value);
        brand.setId(brandId);
        brandRepository.createBrand(brand);
    }

    @Override
    public void updateBrand(Brand brand) {
        brandRepository.updateBrand(brand);
    }

    @Override
    public Brand queryBrandById(int id) {
        return brandRepository.queryBrandById(id);
    }

    @Override
    public void deleteBrandById(int id) {
        brandRepository.deleteBrandById(id);
    }

    @Override
    public List<Brand> queryBrandByCustomerId(int customerId) {
        return brandRepository.queryBrandByCustomerId(customerId);
    }

    @Override
    public Page<Supplier> queryCustomerByPage(Page<Supplier> page) {
        return supplierRepository.querySupplierByPage(page);
    }

    @Override
    public Page<Brand> queryBrandByPage(int customerId, Page<Brand> page){
          return brandRepository.queryBrandByPage(customerId, page);
    }

    @Override
    public Page<ProductStorage> queryProductStorageByPage(int customerId, Page<ProductStorage> page){
         return productStorageRepository.queryProductStorageByPage(customerId, page);
    }

    @Override
    public int queryProductStorageNumber(int customerId){
        return  productStorageRepository.queryProductStorageNumber(customerId);
    }

    @Override
    public int querySkuCountByProductStorageId(int productStorageId){
        return  productStorageRepository.querySkuCountByProductStorageId(productStorageId);
    }

    @Override
    public Supplier queryCustomerByName(String name){
        return supplierRepository.querySupplierByName(name);
    }

    @Override
    public Brand queryBrandByName(String name){
        return brandRepository.queryBrandByName(name);
    }

    @Override
    public ProductStorage queryProductStorageByNameAndCustomerId(String name, int customerId){
        return productStorageRepository.queryProductStorageByNameAndCustomerId(name, customerId);
    }

    @Override
    public void createSupplierAccount(SupplierAccount supplierAccount) {
        supplierRepository.insertSupplierAccount(supplierAccount);
    }

    @Override
    public Page<SupplierAccount> querySupplierAccountByPage(int customerId, Page<SupplierAccount> page) {
        return supplierRepository.querySupplierAccountByPage(customerId, page);
    }

    @Override
    public List<SupplierAccount> querySupplierAccountBySupplierId(int supplierId,String search) {
        return supplierRepository.querySupplierAccountBySupplierId(supplierId,search);
    }

    @Override
    public SupplierAccount querySupplierAccountByName(String accountName,int supplierId) {
        return supplierRepository.querySupplierAccountByName(accountName, supplierId);
    }

    @Override
    public SupplierAccount getSupplierAccountById(int id) {
        return supplierRepository.getSupplierAccountById(id);
    }

    @Override
    public void updateSupplierAccount(SupplierAccount supplierAccount) {
        supplierRepository.updateSupplierAccount(supplierAccount);
    }

    @Override
    public void deleteSupplierAccountById(int id) {
        supplierRepository.deleteSupplierAccountById(id);
    }

}
