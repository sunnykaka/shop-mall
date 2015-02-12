package com.kariqu.accountcenter.repository.impl;


import com.kariqu.accountcenter.domain.Department;
import com.kariqu.accountcenter.repository.DepartmentRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static junit.framework.Assert.assertEquals;


@ContextConfiguration(locations = {"/accountCenter.xml"})
public class DepartmentRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private DepartmentRepository departmentRepository;


    @Test
    @Rollback(false)
    public void testDepartmentRepository() {
        Department department = new Department();
        department.setName("部门经理");
        departmentRepository.createDepartment(department);
        assertEquals("部门经理", departmentRepository.queryDepartmentById(department.getId()).getName());
        assertEquals(1,departmentRepository.queryAllDepartment().size());
    }


}
