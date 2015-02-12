package com.kariqu.securitymanager.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.securitysystem.domain.RoleScope;
import com.kariqu.securitysystem.service.SecurityService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Role Scope控制器
 * User: Asion
 * Date: 11-11-18
 * Time: 下午2:28
 */
@Controller
public class RoleScopeController{

    private final Log logger = LogFactory.getLog(RoleScopeController.class);

    @Autowired
    private SecurityService securityService;


    @RequestMapping(value = "/role/scope/grid")
    public void roleScopeGrid(@RequestParam("start") int start, @RequestParam("limit") int limit,HttpServletResponse response) throws IOException {
        Page<RoleScope> roleScopePage = securityService.queryRoleScopeByPage(new Page<RoleScope>(start / limit + 1, limit));
        List<RoleScopeNamed> roleScopeNamedList = new LinkedList<RoleScopeNamed>();
        for (RoleScope roleScope : roleScopePage.getResult()) {
            RoleScopeNamed roleScopeNamed = new RoleScopeNamed();
            roleScopeNamed.setId(roleScope.getId());
            roleScopeNamed.setScopeValue(roleScope.getScopeValue());
            roleScopeNamed.setResource(roleScope.getResource());
            roleScopeNamed.setResourceAuthScript(roleScope.getResourceAuthScript());
            roleScopeNamed.setUiAuthScript(roleScope.getUiAuthScript());
            roleScopeNamed.setRoleName(securityService.getRole(roleScope.getRoleId()).getRoleName());
            roleScopeNamedList.add(roleScopeNamed);
        }
        new JsonResult(true).addData("totalCount", roleScopePage.getTotalCount()).addData("result",roleScopeNamedList).toJson(response);
    }

    @RequestMapping(value = "/role/scope/update", method = RequestMethod.POST)
    public void updateRoleScope(RoleScope roleScope, HttpServletResponse response) throws IOException {
        try{
            RoleScope currentRoleScope = securityService.getRoleScope(roleScope.getId());
            currentRoleScope.setResource(roleScope.getResource());
            currentRoleScope.setScopeValue(roleScope.getScopeValue());
            currentRoleScope.setResourceAuthScript(roleScope.getResourceAuthScript());
            currentRoleScope.setUiAuthScript(roleScope.getUiAuthScript());
            securityService.updateRoleScope(currentRoleScope);
            new JsonResult(true).toJson(response);
        }catch (Exception e){
            logger.error("角色更新错误："+ e);
            new JsonResult(false, "更新失败").toJson(response);
        }

    }

    @RequestMapping(value = "/role/scope/delete", method = RequestMethod.POST)
    public void deleteRoleScope(int id, HttpServletResponse response) throws IOException {
        try{
            securityService.deleteRoleScope(id);
            new JsonResult(true).toJson(response);
        }catch (Exception e){
            logger.error("删除资源配置错误："+ e);
            new JsonResult(false, "删除失败").toJson(response);
        }

    }

    @RequestMapping(value = "/role/scope/delete/batch", method = RequestMethod.POST)
    public void deleteRoleScope(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try{
            for (int id : ids) {
                securityService.deleteRoleScope(id);
            }
            new JsonResult(true).toJson(response);
        }catch (Exception e){
            logger.error("批量删除资源配置错误："+ e);
            new JsonResult(false, "批量删除失败").toJson(response);
        }

    }

    private class RoleScopeNamed {

        private int id;

        private String roleName;
        /**
         * 资源，对应SecurityResource的getResourceName();
         */
        private String resource;

        /**
         * 资源权限授权脚本
         */
        private String resourceAuthScript;

        /**
         * UI权限授权脚本
         */
        private String uiAuthScript;

        /**
         * 操作范围或者域，这是很灵活的，比如某个人的类目操作范围：厨房用品，锅具，奶锅
         */
        private String scopeValue;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        public String getResourceAuthScript() {
            return resourceAuthScript;
        }

        public void setResourceAuthScript(String resourceAuthScript) {
            this.resourceAuthScript = resourceAuthScript;
        }

        public String getUiAuthScript() {
            return uiAuthScript;
        }

        public void setUiAuthScript(String uiAuthScript) {
            this.uiAuthScript = uiAuthScript;
        }

        public String getScopeValue() {
            return scopeValue;
        }

        public void setScopeValue(String scopeValue) {
            this.scopeValue = scopeValue;
        }
    }


}
