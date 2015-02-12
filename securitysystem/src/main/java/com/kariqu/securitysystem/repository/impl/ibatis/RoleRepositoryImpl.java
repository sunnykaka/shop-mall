package com.kariqu.securitysystem.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.securitysystem.domain.Role;
import com.kariqu.securitysystem.repository.RoleRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-11-17
 * Time: 下午12:53
 */
public class RoleRepositoryImpl extends SqlMapClientDaoSupport implements RoleRepository {

    @Override
    public void createAccountRole(int userId, int roleId) {
        Map map = new HashMap();
        map.put("userId", userId);
        map.put("roleId", roleId);
        getSqlMapClientTemplate().insert("createAccountRole", map);
    }

    @Override
    public void createRolePermission(int roleId, int permissionId) {
        Map map = new HashMap();
        map.put("permissionId", permissionId);
        map.put("roleId", roleId);
        getSqlMapClientTemplate().insert("createRolePermission", map);
    }

    @Override
    public void deleteAccountRoleByUserId(int userId) {
        getSqlMapClientTemplate().delete("deleteAccountRoleByUserId", userId);
    }

    @Override
    public void deleteAccountRoleByRoleId(int roleId) {
        getSqlMapClientTemplate().delete("deleteAccountRoleByRoleId", roleId);
    }

    @Override
    public void deleteRolePermissionByRoleId(int roleId) {
        getSqlMapClientTemplate().delete("deleteRolePermissionByRoleId", roleId);
    }

    @Override
    public void deleteRolePermissionByPermissionId(int permissionId) {
        getSqlMapClientTemplate().delete("deleteRolePermissionByPermissionId", permissionId);
    }

    @Override
    public List<Role> queryAllRole() {
        return getSqlMapClientTemplate().queryForList("selectAllRoles");
    }

    @Override
    public Role getRole(int id) {
        return (Role) getSqlMapClientTemplate().queryForObject("selectRole", id);
    }

    @Override
    public Page<Role> queryRoleByPage(Page<Role> page) {
        Map param = new HashMap();
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<Role> queryRoleByPage = getSqlMapClientTemplate().queryForList("queryRoleByPage", param);
        page.setResult(queryRoleByPage);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("selectCountForRole"));
        return page;
    }

    @Override
    public Role getRoleByName(String roleName) {
        return (Role) getSqlMapClientTemplate().queryForObject("getRoleByName", roleName);
    }

    @Override
    public void createRole(Role role) {
        getSqlMapClientTemplate().insert("insertRole", role);
    }

    @Override
    public void updateRole(Role role) {
        getSqlMapClientTemplate().update("updateRole", role);
    }

    @Override
    public void deleteRole(int id) {
        getSqlMapClientTemplate().delete("deleteRole", id);
    }

    @Override
    public List<Integer> queryAccountRole(int userId) {
        return getSqlMapClientTemplate().queryForList("queryAccountRole", userId);
    }

    @Override
    public List<Integer> queryRolePermission(int roleId) {
        return getSqlMapClientTemplate().queryForList("queryRolePermission", roleId);
    }
}
