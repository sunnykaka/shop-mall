package com.kariqu.accountcenter.service.impl;


import com.kariqu.accountcenter.domain.Department;
import com.kariqu.accountcenter.repository.DepartmentRepository;
import com.kariqu.accountcenter.service.DepartmentService;
import com.kariqu.common.pagenavigator.Page;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: Asion
 * Date: 12-3-12
 * Time: 下午3:42
 */
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void createDepartment(Department department) {
        departmentRepository.createDepartment(department);
    }

    @Override
    public void updateDepartment(Department department) {
        departmentRepository.updateDepartment(department);
    }

    @Override
    public List<Department> queryAllDepartment() {
        return departmentRepository.queryAllDepartment();
    }

    @Override
    public Department queryDepartmentById(int id) {
        return departmentRepository.queryDepartmentById(id);
    }

    @Override
    public void deleteDepartmentById(int id) {
        departmentRepository.deleteDepartmentById(id);
    }

    @Override
    public Page<Department> queryDepartmentByPage(Page<Department> page) {
        return departmentRepository.queryDepartmentByPage(page);
    }
}
