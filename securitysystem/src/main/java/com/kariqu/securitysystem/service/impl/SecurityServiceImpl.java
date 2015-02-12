package com.kariqu.securitysystem.service.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.securitysystem.domain.Role;
import com.kariqu.securitysystem.domain.RoleScope;
import com.kariqu.securitysystem.domain.UrlPermission;
import com.kariqu.securitysystem.repository.RoleRepository;
import com.kariqu.securitysystem.repository.RoleScopeRepository;
import com.kariqu.securitysystem.repository.UrlPermissionRepository;
import com.kariqu.securitysystem.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

/**
 * User: Asion
 * Date: 11-11-17
 * Time: 下午9:27
 */
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UrlPermissionRepository urlPermissionRepository;

    @Autowired
    private RoleScopeRepository roleScopeRepository;

    @Override
    public List<Role> queryAllRole() {
        return roleRepository.queryAllRole();
    }

    @Override
    public Role getRole(int id) {
        Role role = roleRepository.getRole(id);
        if (role != null) {
            List<Integer> permissionIdList = roleRepository.queryRolePermission(id);
            List<UrlPermission> permissionList = new LinkedList<UrlPermission>();
            for (Integer permissionId : permissionIdList) {
                UrlPermission permission = urlPermissionRepository.getPermission(permissionId);
                permissionList.add(permission);
            }
            role.setUrlPermissions(permissionList);
            List<RoleScope> roleScopes = roleScopeRepository.queryRoleScopeByRoleId(id);
            role.setRoleScopes(roleScopes);
        }
        return role;
    }

    @Override
    public Role getRoleByName(String roleName) {
        return roleRepository.getRoleByName(roleName);
    }

    @Override
    public void createRole(Role role) {
        roleRepository.createRole(role);
    }

    @Override
    public void updateRole(Role role) {
        roleRepository.updateRole(role);
    }

    @Override
    public void deleteRole(int id) {
        roleRepository.deleteRole(id);
    }

    @Override
    public List<UrlPermission> queryALlPermission() {
        return urlPermissionRepository.queryALlPermission();
    }

    @Override
    public UrlPermission getPermission(int id) {
        return urlPermissionRepository.getPermission(id);
    }

    @Override
    public UrlPermission getPermissionByName(String permissionName) {
        return urlPermissionRepository.getPermissionByName(permissionName);
    }

    @Override
    public void createPermission(UrlPermission permission) {
        urlPermissionRepository.createPermission(permission);
    }

    @Override
    public void updatePermission(UrlPermission permission) {
        urlPermissionRepository.updatePermission(permission);
    }

    @Override
    public void deletePermission(int id) {
        urlPermissionRepository.deletePermission(id);
    }

    @Override
    public RoleScope getRoleScope(int id) {
        return roleScopeRepository.getRoleScope(id);
    }

    @Override
    public void createRoleScope(RoleScope roleScope) {
        roleScopeRepository.createRoleScope(roleScope);
    }

    @Override
    public List<RoleScope> queryAllRoleScope() {
        return roleScopeRepository.queryAllRoleScope();
    }

    @Override
    public void updateRoleScope(RoleScope roleScope) {
        roleScopeRepository.updateRoleScope(roleScope);
    }

    @Override
    public void deleteRoleScope(int id) {
        roleScopeRepository.deleteRoleScope(id);
    }

    @Override
    public void createAccountRole(int userId, int roleId) {
        roleRepository.createAccountRole(userId, roleId);
    }

    @Override
    public void createRolePermission(int roleId, int permissionId) {
        roleRepository.createRolePermission(roleId, permissionId);
    }

    @Override
    public void deleteAccountRoleByUserId(int userId) {
        roleRepository.deleteAccountRoleByUserId(userId);
    }

    @Override
    public void deleteAccountRoleByRoleId(int roleId) {
        roleRepository.deleteAccountRoleByRoleId(roleId);
    }

    @Override
    public void deleteRolePermissionByRoleId(int roleId) {
        roleRepository.deleteRolePermissionByRoleId(roleId);
    }

    @Override
    public void deleteRolePermissionByPermissionId(int permissionId) {
        roleRepository.deleteRolePermissionByPermissionId(permissionId);
    }

    @Override
    public List<Integer> queryAccountRole(int userId) {
        return roleRepository.queryAccountRole(userId);
    }

    @Override
    public List<Integer> queryRolePermission(int roleId) {
        return roleRepository.queryRolePermission(roleId);
    }

    @Override
    public List<RoleScope> queryRoleScopeByRoleId(int roleId) {
        return roleScopeRepository.queryRoleScopeByRoleId(roleId);
    }

    @Override
    public RoleScope queryRoleScopeByRoleIdAndResourceName(int roleId, String resourceName) {
        return roleScopeRepository.queryRoleScopeByRoleIdAndResourceName(roleId, resourceName);
    }

    @Override
    public void deleteRoleScopeByRoleId(int roleId) {
        roleScopeRepository.deleteRoleScopeByRoleId(roleId);
    }

    @Override
    public Page<Role> queryRoleByPage(Page<Role> page) {
        return roleRepository.queryRoleByPage(page);
    }

    @Override
    public Page<RoleScope> queryRoleScopeByPage(Page<RoleScope> page) {
        return roleScopeRepository.queryRoleScopeByPage(page);
    }

    @Override
    public Page<UrlPermission> queryPermissionByPage(Page<UrlPermission> page) {
        return urlPermissionRepository.queryPermissionByPage(page);
    }
}
