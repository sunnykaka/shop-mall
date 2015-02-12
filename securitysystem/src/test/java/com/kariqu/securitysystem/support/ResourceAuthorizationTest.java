package com.kariqu.securitysystem.support;

import com.kariqu.securitysystem.domain.*;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-11-16
 * Time: 下午11:20
 */
public class ResourceAuthorizationTest {

    @Test
    public void testResourceAuthorization() {
        TestModel testModel = new TestModel();
        Role role = new Role();
        role.setRoleName("类目管理员");
        UrlPermission urlPermission = new UrlPermission();
        urlPermission.setResource("类目");
        urlPermission.setPath("/category/delete");
        urlPermission.setPermissionName("删除类目");
        role.addUrlPermission(urlPermission);
        RoleScope roleScope = new RoleScope();
        roleScope.setResource("类目");
        roleScope.setResourceAuthScript("def resourceAuth(resource, account,role,roleScope) { role.getRoleName().equals(\"类目管理员\") }");
        roleScope.setUiAuthScript("def uiAuth(resource, account,role,roleScope) { role.getRoleScope(resource.getResourceName()).getScopeValue().equals(resource.getCategoryName()) }");
        roleScope.setScopeValue("厨房用品");
        role.addRoleScope(roleScope);
        ResourceAuthorization<TestResource> resourceAuthorization = new ResourceAuthorization<TestResource>();
        assertEquals(true, resourceAuthorization.isAuthenticated(new TestResource(), testModel, role, ResourceAuthorization.AuthType.Resource));
        assertEquals(true, resourceAuthorization.isAuthenticated(new TestResource(), testModel, role, ResourceAuthorization.AuthType.Ui));

    }

    private class TestModel {

    }


    private class TestResource implements SecurityResource {
        @Override
        public String getResourceName() {
            return "类目";
        }

        public String getCategoryName() {
            return "厨房用品";
        }
    }


}
