package com.kariqu.securitysystem.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.securitysystem.domain.RoleScope;
import com.kariqu.securitysystem.repository.RoleScopeRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-11-17
 * Time: 下午1:46
 */
public class RoleScopeRepositoryImpl extends SqlMapClientDaoSupport implements RoleScopeRepository {

    @Override
    public List<RoleScope> queryAllRoleScope() {
        return getSqlMapClientTemplate().queryForList("selectAllRoleScopes");
    }

    @Override
    public RoleScope queryRoleScopeByRoleIdAndResourceName(int roleId, String resourceName) {
        Map map  = new HashMap();
        map.put("roleId", roleId);
        map.put("resourceName", resourceName);
        return (RoleScope) getSqlMapClientTemplate().queryForObject("queryRoleScopeByRoleIdAndResourceName", map);

    }

    @Override
    public RoleScope getRoleScope(int id) {
        return (RoleScope) getSqlMapClientTemplate().queryForObject("selectRoleScope", id);
    }

    @Override
    public Page<RoleScope> queryRoleScopeByPage(Page<RoleScope> page) {
        Map param = new HashMap();
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<RoleScope> queryRoleScopeByPage = getSqlMapClientTemplate().queryForList("queryRoleScopeByPage", param);
        page.setResult(queryRoleScopeByPage);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("selectCountForRoleScope"));
        return page;
    }

    @Override
    public List<RoleScope> queryRoleScopeByRoleId(int roleId) {
        return getSqlMapClientTemplate().queryForList("queryRoleScopeByRoleId", roleId);
    }

    @Override
    public void deleteRoleScopeByRoleId(int roleId) {
        getSqlMapClientTemplate().delete("deleteRoleScopeByRoleId", roleId);
    }

    @Override
    public void createRoleScope(RoleScope roleScope) {
        getSqlMapClientTemplate().insert("insertRoleScope", roleScope);
    }

    @Override
    public void updateRoleScope(RoleScope roleScope) {
        getSqlMapClientTemplate().update("updateRoleScope", roleScope);
    }

    @Override
    public void deleteRoleScope(int id) {
        getSqlMapClientTemplate().delete("deleteRoleScope", id);
    }
}
