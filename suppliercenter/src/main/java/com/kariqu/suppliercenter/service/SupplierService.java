package com.kariqu.suppliercenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.suppliercenter.domain.*;

import java.util.List;

/**
 * 商家对象，商家账户对象，商品库存对象，品牌对象
 * @author:Wendy
 * @since:1.0.0 Date: 13-1-23
 * Time: 上午11:12
 */
public interface SupplierService {

    /* 商品信息操作*/
    void createCustomer(Supplier customer);

    void updateCustomer(Supplier customer);

    Supplier queryCustomerById(int id);

    Supplier queryCustomerByName(String name);

    void deleteCustomerById(int id);

    /* 商品品牌信息操作*/
    void updateBrand(Brand brand);

    Brand queryBrandById(int id);

    void deleteBrandById(int id);

    Brand queryBrandByName(String name);
    /**
     * 创建一个品牌，品牌ID会和类目中心的品牌ID保持一致
     * @param brand
     */
    void createBrand(Brand brand);

    /**
     * 查询所有的商家
     * @return
     */
    List<Supplier> queryAllCustomer();

    /**
     * 查询某个商家下的所有仓库
     * @param customerId
     * @return
     */
    List<ProductStorage> queryProductStorageByCustomerId(int customerId);


    /**
     * 查询某个商家的品牌
     * @param customerId
     * @return
     */
    List<Brand> queryBrandByCustomerId(int customerId);

    /**
     * 商家分页查询
     *
     * @param page
     * @return
     */
    Page<Supplier> queryCustomerByPage(Page<Supplier> page);

    /**
     * 商品品牌分页查询
     * @param page
     * @return
     */
    Page<Brand> queryBrandByPage(int customerId, Page<Brand> page);

    /**
     * 商品库存分页查询
     * @param page
     * @return
     */
    Page<ProductStorage> queryProductStorageByPage(int customerId, Page<ProductStorage> page);

    /**
     *商家账号分页查询
     * @param page
     * @return
     */
    Page<SupplierAccount> querySupplierAccountByPage(int customerId, Page<SupplierAccount> page);

    List<SupplierAccount> querySupplierAccountBySupplierId(int supplierId,String search);

    /*商品库存信息操作*/
    int queryProductStorageNumber(int customerId);

    int querySkuCountByProductStorageId(int productStorageId);

    ProductStorage queryProductStorageByNameAndCustomerId(String name, int customerId);

    void createProductStorage(ProductStorage productStorage);

    void updateProductStorage(ProductStorage productStorage);

    ProductStorage queryProductStorageById(int id);

    void deleteProductStorageById(int id);

    /*商家账号操作*/
    void createSupplierAccount(SupplierAccount supplierAccount);

    SupplierAccount querySupplierAccountByName(String accountName,int supplierId);

    SupplierAccount getSupplierAccountById(int id);

    void updateSupplierAccount(SupplierAccount supplierAccount);

    void deleteSupplierAccountById(int id);

}