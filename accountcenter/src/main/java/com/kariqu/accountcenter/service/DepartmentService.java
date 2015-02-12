package com.kariqu.accountcenter.service;


import com.kariqu.accountcenter.domain.Department;
import com.kariqu.common.pagenavigator.Page;

import java.util.List;

/**
 * User: Asion
 * Date: 12-3-12
 * Time: 下午3:39
 */
public interface DepartmentService {

    void createDepartment(Department department);

    void updateDepartment(Department department);

    Department queryDepartmentById(int id);

    void deleteDepartmentById(int id);


    List<Department> queryAllDepartment();


    Page<Department> queryDepartmentByPage(Page<Department> page);

}
