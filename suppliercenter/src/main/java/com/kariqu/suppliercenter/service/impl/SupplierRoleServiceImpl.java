package com.kariqu.suppliercenter.service.impl;

import com.kariqu.suppliercenter.repository.SupplierRoleRepository;
import com.kariqu.suppliercenter.service.SupplierRoleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-4-27
 * Time: 下午3:03
 */
public class SupplierRoleServiceImpl implements SupplierRoleService {

    @Autowired
    private SupplierRoleRepository supplierRoleRepository;

    @Override
    public void insertRoleSupplier(int roleId, int supplierId) {
        supplierRoleRepository.insertRoleSupplier(roleId,supplierId);
    }

    @Override
    public List<Integer> queryRoleSupplierBySupplierId(int supplierId) {
        return supplierRoleRepository.queryRoleSupplierBySupplierId(supplierId);
    }

    @Override
    public void deleteRoleSupplier(int roleId, int supplierId) {
        supplierRoleRepository.deleteRoleSupplier(roleId,supplierId);
    }
}
