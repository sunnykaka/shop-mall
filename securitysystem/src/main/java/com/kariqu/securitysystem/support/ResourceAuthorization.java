package com.kariqu.securitysystem.support;

import com.kariqu.securitysystem.domain.SecurityResource;
import com.kariqu.securitysystem.domain.Role;
import com.kariqu.securitysystem.domain.RoleScope;
import groovy.lang.Binding;
import groovy.lang.Script;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 资源授权组件
 * 会根据授权类型来执行相应的授权脚本
 *
 * R表示当前操作的资源对象，比如Account,Order等
 * User: Asion
 * Date: 11-11-16
 * Time: 下午4:29
 */
public class ResourceAuthorization<R extends SecurityResource> {

    private static final String RESOURCE_AUTH_METHOD = "resourceAuth";

    private static final String UI_AUTH_METHOD = "uiAuth";

    @Autowired
    private Binding binding;

    public static enum AuthType {
        Resource,

        Ui
    }

    /**
     * 用动态语言来授权
     *
     * @param resource
     * @param account
     * @return
     */
    public boolean isAuthenticated(R resource, Object account, Role role, AuthType authType) {
        List<RoleScope> roleScopes = role.getRoleScopes();
        for (RoleScope roleScope : roleScopes) {
            if (roleScope.getResource().equals(resource.getResourceName())) {
                String resourceAuthScript = roleScope.getResourceAuthScript();
                String uiAuthScript = roleScope.getUiAuthScript();
                if (StringUtils.isNotBlank(resourceAuthScript) && authType == AuthType.Resource) {
                    Script script = GroovyParser.parseGroovy(resourceAuthScript, binding);
                    return (Boolean) script.invokeMethod(RESOURCE_AUTH_METHOD, new Object[]{resource, account, role, roleScope});
                } else if (StringUtils.isNotBlank(uiAuthScript) && authType == AuthType.Ui) {
                    Script script = GroovyParser.parseGroovy(uiAuthScript, binding);
                    return (Boolean) script.invokeMethod(UI_AUTH_METHOD, new Object[]{resource, account, role, roleScope});
                }
            }
        }
        return false;
    }

    public Binding getBinding() {
        return binding;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    }
}
