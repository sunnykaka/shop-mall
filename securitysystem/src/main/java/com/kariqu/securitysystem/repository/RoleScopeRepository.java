package com.kariqu.securitysystem.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.securitysystem.domain.RoleScope;

import java.util.List;

/**
 * User: Asion
 * Date: 11-11-17
 * Time: 下午1:31
 */
public interface RoleScopeRepository {

    List<RoleScope> queryAllRoleScope();

    RoleScope getRoleScope(int id);

    Page<RoleScope> queryRoleScopeByPage(Page<RoleScope> page);

    RoleScope queryRoleScopeByRoleIdAndResourceName(int roleId, String resourceName);

    List<RoleScope> queryRoleScopeByRoleId(int roleId);

    void deleteRoleScopeByRoleId(int roleId);

    void createRoleScope(RoleScope roleScope);

    void updateRoleScope(RoleScope roleScope);

    void deleteRoleScope(int id);

}
