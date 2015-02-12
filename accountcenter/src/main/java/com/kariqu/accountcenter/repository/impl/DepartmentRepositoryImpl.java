package com.kariqu.accountcenter.repository.impl;


import com.kariqu.accountcenter.domain.Account;
import com.kariqu.accountcenter.domain.Department;
import com.kariqu.accountcenter.repository.DepartmentRepository;
import com.kariqu.common.pagenavigator.Page;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DepartmentRepositoryImpl extends SqlMapClientDaoSupport implements DepartmentRepository {

    @Override
    public void createDepartment(Department department) {
        getSqlMapClientTemplate().insert("insertDepartment", department);
    }

    @Override
    public void updateDepartment(Department department) {
        getSqlMapClientTemplate().update("updateDepartment", department);
    }

    @Override
    public List<Department> queryAllDepartment() {
        return getSqlMapClientTemplate().queryForList("selectAllDepartments");
    }

    @Override
    public Department queryDepartmentById(int id) {
        return (Department) getSqlMapClientTemplate().queryForObject("selectDepartmentById", id);
    }

    @Override
    public void deleteDepartmentById(int id) {
        getSqlMapClientTemplate().delete("deleteDepartmentById", id);
    }

    @Override
    public Page<Department> queryDepartmentByPage(Page<Department> page) {
        Map param = new HashMap();
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<Department> queryDepartmentByPage = getSqlMapClientTemplate().queryForList("queryDepartmentByPage", param);
        page.setResult(queryDepartmentByPage);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("selectCountForDepartment"));
        return page;
    }
}
