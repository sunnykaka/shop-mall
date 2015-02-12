package com.kariqu.suppliersystem.supplierManager.web;

import com.kariqu.securitysystem.domain.Role;
import com.kariqu.suppliercenter.domain.SupplierAccount;
import com.kariqu.suppliercenter.service.SupplierRoleService;
import com.kariqu.suppliersystem.common.JsonResult;
import com.kariqu.suppliersystem.supplierManager.vo.SessionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色控制器
 *
 * @author:Wendy
 * @since:1.0.0 Date: 13-4-22
 * Time: 下午4:14
 */
@Controller
public class SupplierRoleController extends PermissionVoController {

    private final Log logger = LogFactory.getLog(SupplierRoleController.class);

    @Autowired
    private SupplierRoleService supplierRoleService;


    /**
     * 查询某个商家的角色
     */
    @RequestMapping(value = "/supplier/role/list")
    public String roleList(Model model, HttpServletRequest request) throws IOException {
        SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
        List<Role> roleList = new ArrayList<Role>();
        List<Integer> roleIds = supplierRoleService.queryRoleSupplierBySupplierId(supplierAccount.getCustomerId());
        for (int roleId : roleIds) {
            Role role = securityService.getRole(roleId);
            roleList.add(role);
        }
        if (supplierAccount.getAccountName().equals("yijushang")) {
            model.addAttribute("has", "yes");
        }
        model.addAttribute("roleList", roleList);
        return "supplierRole/role";
    }


    @RequestMapping(value = "/supplier/role/addRolePageJump")
    public String addSupplierAccountPage(Model model, HttpServletRequest request) throws IOException {
        model.addAttribute("permissionVoList", genePermissionVoList());
        return "supplierRole/addRole";
    }


    @RequestMapping(value = "/supplier/role/updateRolePageJump/{id}")
    public String supplierRoleConfigPage(@PathVariable("id") int id, Model model, HttpServletRequest request) throws IOException {
        List<Integer> permissionIds = securityService.queryRolePermission(id);
        Role role = securityService.getRole(id);
        model.addAttribute("role", role);
        model.addAttribute("permissionIds", permissionIds);
        model.addAttribute("permissionVoList", genePermissionVoList());
        return "supplierRole/updateRole";
    }


    @RequestMapping(value = "/supplier/role/add", method = RequestMethod.POST)
    public void createRole(Role role, int[] permissionId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Role roleByName = securityService.getRoleByName(role.getRoleName());
            if (roleByName != null) {
                new JsonResult(false, "该角色已经存在").toJson(response);
                return;
            }
            securityService.createRole(role);
            SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
            supplierRoleService.insertRoleSupplier(role.getId(), supplierAccount.getCustomerId());
            if (permissionId != null) {
                for (int permission : permissionId) {
                    securityService.createRolePermission(role.getId(), permission);
                }
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("添加角色错误：" + e);
            new JsonResult(false, "添加失败").toJson(response);
        }
    }


    /**
     * 更新角色，注意名字不能改，只改权限
     *
     * @param role
     * @param permissionId
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/supplier/role/update", method = RequestMethod.POST)
    public void updateRole(Role role, int[] permissionId, HttpServletResponse response) throws IOException {
        try {
            Role currentRole = securityService.getRole(role.getId());
            currentRole.setFunctionSet(role.getFunctionSet());
            securityService.updateRole(currentRole);
            //先清除原来的权限关联
            securityService.deleteRolePermissionByRoleId(role.getId());
            if (permissionId != null) {
                for (int permission : permissionId) {
                    securityService.createRolePermission(role.getId(), permission);
                }
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("角色更新错误：" + e);
            new JsonResult(false, "更新失败").toJson(response);
        }
    }


    @RequestMapping(value = "/supplier/role/delete", method = RequestMethod.POST)
    public void deleteRole(@RequestParam("ids") int[] ids, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
            for (int id : ids) {
                securityService.deleteRole(id);
                securityService.deleteAccountRoleByRoleId(id);
                securityService.deleteRolePermissionByRoleId(id);
                supplierRoleService.deleteRoleSupplier(id, supplierAccount.getCustomerId());
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("批量删除角色错误：" + e);
            new JsonResult(false, "批量删除失败").toJson(response);
        }

    }
}
