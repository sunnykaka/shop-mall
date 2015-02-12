package com.kariqu.securitymanager.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.securitysystem.domain.UrlPermission;
import com.kariqu.securitysystem.service.SecurityService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * User: Asion
 * Date: 11-11-17
 * Time: 下午9:36
 */
@Controller
public class PermissionController {

    private final Log logger = LogFactory.getLog(PermissionController.class);

    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = "/permission/grid")
    public void permissionGrid(@RequestParam("start") int start, @RequestParam("limit") int limit, HttpServletResponse response) throws IOException {
        Page<UrlPermission> urlPermissionPage = securityService.queryPermissionByPage(new Page<UrlPermission>(start / limit + 1, limit));
        new JsonResult(true).addData("totalCount", urlPermissionPage.getTotalCount()).addData("result", urlPermissionPage.getResult()).toJson(response);
    }

    @RequestMapping(value = "/permission/list")
    public
    @ResponseBody
    List<UrlPermission> permissionList() {
        List<UrlPermission> urlPermissions = securityService.queryALlPermission();
        return urlPermissions;
    }

    @RequestMapping(value = "/permission/add", method = RequestMethod.POST)
    public void createPermission(UrlPermission urlPermission, HttpServletResponse response) throws IOException {
        try {
            UrlPermission permissionByName = securityService.getPermissionByName(urlPermission.getPermissionName());
            if (permissionByName != null) {
                new JsonResult(false, "该权限已经存在").toJson(response);
                return;
            }
            securityService.createPermission(urlPermission);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("添加权限管理错误：" + e);
            new JsonResult(false, "添加失败").toJson(response);
        }


    }

    @RequestMapping(value = "/permission/update", method = RequestMethod.POST)
    public void updatePermission(UrlPermission urlPermission, HttpServletResponse response) throws IOException {
        try {
            UrlPermission permission = securityService.getPermission(urlPermission.getId());
            permission.setPath(urlPermission.getPath());
            permission.setResource(urlPermission.getResource());
            permission.setCategory(urlPermission.getCategory());
            securityService.updatePermission(permission);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("修改权限错误：" + e);
            new JsonResult(false, "修改失败").toJson(response);
        }
    }

    @RequestMapping(value = "/permission/delete", method = RequestMethod.POST)
    public void deletePermission(int id, HttpServletResponse response) throws IOException {
        try {
            securityService.deletePermission(id);
            securityService.deleteRolePermissionByPermissionId(id);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("删除权限错误：" + e);
            new JsonResult(false, "删除失败").toJson(response);
        }

    }

    @RequestMapping(value = "/permission/delete/batch", method = RequestMethod.POST)
    public void deleteRole(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids) {
                securityService.deletePermission(id);
                securityService.deleteRolePermissionByPermissionId(id);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("批量删除权限错误：" + e);
            new JsonResult(false, "批量删除失败").toJson(response);
        }
    }


}
