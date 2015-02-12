package com.kariqu.securitysystem.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.securitysystem.domain.Role;

import java.awt.print.Pageable;
import java.util.List;

/**
 * User: Asion
 * Date: 11-11-15
 * Time: 下午5:15
 */
public interface RoleRepository {

    List<Role> queryAllRole();

    Role getRole(int id);

    Page<Role> queryRoleByPage(Page<Role> page);

    Role getRoleByName(String roleName);

    void deleteAccountRoleByUserId(int userId);

    void deleteAccountRoleByRoleId(int roleId);

    /**
     * 创建账户角色关联
     *
     * @param userId
     * @param roleId
     */
    void createAccountRole(int userId, int roleId);

    List<Integer> queryAccountRole(int userId);

    List<Integer> queryRolePermission(int roleId);

    /**
     * 创建角色权限管理啊
     *
     * @param roleId
     * @param permissionId
     */
    void createRolePermission(int roleId, int permissionId);

    void deleteRolePermissionByRoleId(int roleId);

    void deleteRolePermissionByPermissionId(int permissionId);


    void createRole(Role role);

    void updateRole(Role role);

    void deleteRole(int id);

}
