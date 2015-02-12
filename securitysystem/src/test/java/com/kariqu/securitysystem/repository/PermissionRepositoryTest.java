package com.kariqu.securitysystem.repository;

import com.kariqu.securitysystem.domain.UrlPermission;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import static junit.framework.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-11-17
 * Time: 下午1:36
 */
@SpringApplicationContext({"classpath:securitySystem.xml"})
public class PermissionRepositoryTest extends UnitilsJUnit4 {

    @SpringBean("permissionRepository")
    private UrlPermissionRepository permissionRepository;

    @Test
    public void testPermissionRepository() {
        UrlPermission urlPermission = new UrlPermission();
        urlPermission.setPermissionName("删除类目");
        urlPermission.setResource("category");
        urlPermission.setPath("category/delete");
        permissionRepository.createPermission(urlPermission);
        assertEquals("category/delete", permissionRepository.getPermission(urlPermission.getId()).getPath());
        assertEquals("category/delete", permissionRepository.getPermissionByName("删除类目").getPath());
        assertEquals("删除类目", permissionRepository.getPermission(urlPermission.getId()).getPermissionName());
        urlPermission.setPermissionName("xxxx");
        urlPermission.setPath("gegeee");
        urlPermission.setResource("gege");
        permissionRepository.updatePermission(urlPermission);
        permissionRepository.deletePermission(urlPermission.getId());
    }
}
