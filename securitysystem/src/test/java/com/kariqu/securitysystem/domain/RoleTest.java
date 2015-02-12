package com.kariqu.securitysystem.domain;

import org.junit.Test;

import java.util.LinkedList;

import static junit.framework.Assert.assertEquals;

/**
 * User: Asion
 * Date: 12-6-25
 * Time: 上午10:10
 */
public class RoleTest {

    @Test
    public void test() {
        Role role = new Role();
        final UrlPermission urlPermission = new UrlPermission();
        urlPermission.setPath("/crm/*");
        role.setUrlPermissions(new LinkedList<UrlPermission>() {
            {
                add(urlPermission);
            }
        });
        assertEquals(true, role.hasPermission("/crm/customer/add"));
    }
}
