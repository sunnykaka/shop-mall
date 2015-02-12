package com.kariqu.securitysystem.repository;

import com.kariqu.securitysystem.domain.RoleScope;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-11-17
 * Time: 下午1:48
 */
@SpringApplicationContext({"classpath:securitySystem.xml"})
public class RoleScopeRepositoryTest extends UnitilsJUnit4 {

    @SpringBean("roleScopeRepository")
    private RoleScopeRepository roleScopeRepository;

    @Test
    public void testRoleScopeRepository() {
        RoleScope roleScope = new RoleScope();
        roleScope.setResource("resource");
        roleScope.setResourceAuthScript("resource script");
        roleScope.setScopeValue("value");
        roleScope.setUiAuthScript("ui script");
        roleScope.setRoleId(3);
        roleScopeRepository.createRoleScope(roleScope);
        assertEquals("resource", roleScopeRepository.getRoleScope(roleScope.getId()).getResource());
        assertEquals("resource", roleScopeRepository.queryRoleScopeByRoleIdAndResourceName(3,"resource").getResource());
        roleScope.setUiAuthScript("ggg");
        roleScope.setResource("ggxxx");
        roleScope.setResourceAuthScript("xege");
        roleScope.setScopeValue("edef");
        roleScope.setRoleId(22);
        roleScopeRepository.updateRoleScope(roleScope);
        List<RoleScope> roleScope1 = roleScopeRepository.queryRoleScopeByRoleId(22);
        assertEquals(1, roleScope1.size());
        roleScopeRepository.deleteRoleScope(roleScope.getId());
    }
}
