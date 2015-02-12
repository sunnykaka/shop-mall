package com.kariqu.securitysystem.repository;

import com.kariqu.securitysystem.domain.Role;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-11-17
 * Time: 下午1:25
 */
@SpringApplicationContext({"classpath:securitySystem.xml"})
public class RoleRepositoryTest extends UnitilsJUnit4 {

    @SpringBean("roleRepository")
    private RoleRepository roleRepository;

    @Test
    public void testRoleRepository() {
        Role role = new Role();
        role.setRoleName("系统管理员");
        roleRepository.createRole(role);
        role.setRoleName("版主");
        roleRepository.updateRole(role);
        assertEquals("版主", roleRepository.getRole(role.getId()).getRoleName());
        assertEquals("版主", roleRepository.getRoleByName("版主").getRoleName());

        roleRepository.deleteRole(role.getId());

        roleRepository.createAccountRole(44,55);
        roleRepository.createRolePermission(55,56);

        assertEquals(1, roleRepository.queryAccountRole(44).size());
        assertEquals(1, roleRepository.queryRolePermission(55).size());
    }


}
