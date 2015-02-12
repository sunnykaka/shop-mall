package com.kariqu.securitysystem.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.securitysystem.domain.Permission;
import com.kariqu.securitysystem.domain.UrlPermission;

import java.util.List;

/**
 * User: Asion
 * Date: 11-11-15
 * Time: 下午5:15
 */
public interface UrlPermissionRepository {

    List<UrlPermission> queryALlPermission();

    UrlPermission getPermission(int id);

    Page<UrlPermission> queryPermissionByPage(Page<UrlPermission> page);

    UrlPermission getPermissionByName(String permissionName);

    void createPermission(UrlPermission permission);

    void updatePermission(UrlPermission permission);

    void deletePermission(int id);

}
