package com.kariqu.accountcenter.repository;


import com.kariqu.accountcenter.domain.Department;
import com.kariqu.common.pagenavigator.Page;

import java.util.List;

public interface DepartmentRepository {

    void createDepartment(Department department);

    void updateDepartment(Department department);

    Department queryDepartmentById(int id);

    List<Department> queryAllDepartment();

    Page<Department> queryDepartmentByPage(Page<Department> page);

    void deleteDepartmentById(int id);

}
