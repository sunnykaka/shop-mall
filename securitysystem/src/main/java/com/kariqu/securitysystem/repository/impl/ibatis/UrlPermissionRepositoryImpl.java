package com.kariqu.securitysystem.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.securitysystem.domain.Permission;
import com.kariqu.securitysystem.domain.UrlPermission;
import com.kariqu.securitysystem.repository.UrlPermissionRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-11-17
 * Time: 下午12:50
 */
public class UrlPermissionRepositoryImpl extends SqlMapClientDaoSupport implements UrlPermissionRepository {

    @Override
    public List<UrlPermission> queryALlPermission() {
        return getSqlMapClientTemplate().queryForList("selectAllUrlPermissions");
    }

    @Override
    public UrlPermission getPermission(int id) {
        return (UrlPermission) getSqlMapClientTemplate().queryForObject("selectUrlPermission", id);
    }

    @Override
    public Page<UrlPermission> queryPermissionByPage(Page<UrlPermission> page) {
        Map param = new HashMap();
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<UrlPermission> queryPermissionByPage = getSqlMapClientTemplate().queryForList("queryUrlPermissionByPage", param);
        page.setResult(queryPermissionByPage);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("selectCountForUrlPermission"));
        return page;
    }

    @Override
    public UrlPermission getPermissionByName(String permissionName) {
        return (UrlPermission) getSqlMapClientTemplate().queryForObject("getPermissionByName", permissionName);
    }

    @Override
    public void createPermission(UrlPermission permission) {
        getSqlMapClientTemplate().insert("insertUrlPermission", permission);
    }

    @Override
    public void updatePermission(UrlPermission permission) {
        getSqlMapClientTemplate().update("updateUrlPermission", permission);
    }

    @Override
    public void deletePermission(int id) {
        getSqlMapClientTemplate().delete("deleteUrlPermission", id);
    }
}
