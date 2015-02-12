package com.kariqu.suppliercenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.suppliercenter.domain.Supplier;
import com.kariqu.suppliercenter.domain.SupplierAccount;

import java.util.List;

/**
 * 商家仓库类
 * @author:Wendy
 * @since:1.0.0 Date: 13-1-23
 * Time: 上午11:12
 */
public interface SupplierRepository {

    void createSupplier(Supplier supplier);

    void updateSupplier(Supplier supplier);

    void deleteSupplierById(int id);

    Supplier querySupplierById(int id);

    Supplier querySupplierByName(String name);

    List<Supplier> queryAllSupplier();

    Page<Supplier> querySupplierByPage(Page<Supplier> page);



    /*商家账户操作*/

    void insertSupplierAccount(SupplierAccount supplierAccount);

    void updateSupplierAccount(SupplierAccount supplierAccount);

    void deleteSupplierAccountById(int id);

    void deleteSupplierAccountByCustomerId(int customerId);

    SupplierAccount querySupplierAccountByName(String accountName,int supplierId);

    SupplierAccount getSupplierAccountById(int id);

    Page<SupplierAccount> querySupplierAccountByPage(int customerId, Page<SupplierAccount> page);

    List<SupplierAccount> querySupplierAccountBySupplierId(int supplierId,String search);
}
