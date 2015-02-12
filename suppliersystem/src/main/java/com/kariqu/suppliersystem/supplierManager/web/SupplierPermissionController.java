package com.kariqu.suppliersystem.supplierManager.web;
import com.kariqu.securitysystem.domain.UrlPermission;
import com.kariqu.suppliersystem.common.JsonResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 权限控制器
 *
 * @author:Wendy
 * @since:1.0.0 Date: 13-4-22
 * Time: 下午4:14
 */
@Controller
public class SupplierPermissionController extends PermissionVoController {

    private final Log logger = LogFactory.getLog(SupplierPermissionController.class);

    /**
     * 查询权限列表
     */
    @RequestMapping(value = "/supplier/permission/list")
    public String permissionList(Model model, HttpServletRequest request) throws IOException {
        model.addAttribute("permissionVoList", genePermissionVoList());
        return "supplierPermission/permission";
    }


    @RequestMapping(value = "/supplier/permission/addPermissionPageJump")
    public String addSupplierAccountPage(Model model, HttpServletRequest request) throws IOException {
        return "supplierPermission/addPermission";
    }


    @RequestMapping(value = "/supplier/permission/updatePermissionPageJump/{id}")
    public String supplierRoleConfigPage(@PathVariable("id") int id, Model model, HttpServletRequest request) throws IOException {
        UrlPermission permission = securityService.getPermission(id);
        model.addAttribute("permission", permission);
        return "supplierPermission/updatePermission";
    }


    @RequestMapping(value = "/supplier/permission/add", method = RequestMethod.POST)
    public void createPermission(UrlPermission urlPermission, Model model, HttpServletResponse response) throws IOException {
        try {
            UrlPermission permissionByName = securityService.getPermissionByName(urlPermission.getPermissionName());
            if (permissionByName != null) {
                new JsonResult(false, "该权限存在").toJson(response);
            }
            securityService.createPermission(urlPermission);
        } catch (Exception e) {
            logger.error("添加权限管理错误：" + e);
            new JsonResult(false, "添加失败").toJson(response);
        }
        new JsonResult(true).toJson(response);

    }


    @RequestMapping(value = "/supplier/permission/update", method = RequestMethod.POST)
    public void updatePermission(UrlPermission urlPermission, Model model, HttpServletResponse response) throws IOException {
        try {
            UrlPermission permission = securityService.getPermission(urlPermission.getId());
            permission.setPath(urlPermission.getPath());
            permission.setResource(urlPermission.getResource());
            permission.setCategory(urlPermission.getCategory());
            securityService.updatePermission(permission);
        } catch (Exception e) {
            logger.error("修改权限错误：" + e);
            new JsonResult(false, "修改失败").toJson(response);
        }
        new JsonResult(true).toJson(response);
    }


    @RequestMapping(value = "/supplier/permission/delete", method = RequestMethod.POST)
    public void deletePermission(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids) {
                securityService.deletePermission(id);
                securityService.deleteRolePermissionByPermissionId(id);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("删除权限错误：" + e);
            new JsonResult(false, "删除失败").toJson(response);
        }
    }

}
