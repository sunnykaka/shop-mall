package com.kariqu.suppliercenter.repository;

import java.util.List;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-4-27
 * Time: 下午3:04
 */
public interface SupplierRoleRepository {

    void insertRoleSupplier(int roleId, int supplierId);

    List<Integer> queryRoleSupplierBySupplierId(int supplierId);

    void deleteRoleSupplier(int roleId, int supplierId);
}
