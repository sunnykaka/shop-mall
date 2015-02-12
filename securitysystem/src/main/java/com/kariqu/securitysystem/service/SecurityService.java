package com.kariqu.securitysystem.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.securitysystem.domain.*;

import java.util.List;

/**
 * 安全服务接口
 * User: Asion
 * Date: 11-11-15
 * Time: 下午5:04
 */
public interface SecurityService {

    List<Role> queryAllRole();

    Role getRole(int id);

    Role getRoleByName(String roleName);

    Page<Role> queryRoleByPage(Page<Role> page);

    void createRole(Role role);

    void updateRole(Role role);

    void deleteRole(int id);

    List<UrlPermission> queryALlPermission();

    UrlPermission getPermission(int id);

    Page<RoleScope> queryRoleScopeByPage(Page<RoleScope> page);

    UrlPermission getPermissionByName(String permissionName);

    Page<UrlPermission> queryPermissionByPage(Page<UrlPermission> page);

    void createPermission(UrlPermission permission);

    void updatePermission(UrlPermission permission);

    void deletePermission(int id);

    RoleScope getRoleScope(int id);

    List<RoleScope> queryAllRoleScope();

    void createRoleScope(RoleScope roleScope);

    void updateRoleScope(RoleScope roleScope);

    void deleteRoleScope(int id);

    /**
     * 创建账户角色关联
     *
     * @param userId
     * @param roleId
     */
    void createAccountRole(int userId, int roleId);

    /**
     * 创建角色权限管理啊
     *
     * @param roleId
     * @param permissionId
     */
    void createRolePermission(int roleId, int permissionId);

    void deleteAccountRoleByUserId(int userId);

    void deleteAccountRoleByRoleId(int roleId);

    void deleteRolePermissionByRoleId(int roleId);

    void deleteRolePermissionByPermissionId(int permissionId);

    List<Integer> queryAccountRole(int userId);

    List<Integer> queryRolePermission(int roleId);

    List<RoleScope> queryRoleScopeByRoleId(int roleId);

    RoleScope queryRoleScopeByRoleIdAndResourceName(int roleId, String resourceName);

    void deleteRoleScopeByRoleId(int roleId);


}
