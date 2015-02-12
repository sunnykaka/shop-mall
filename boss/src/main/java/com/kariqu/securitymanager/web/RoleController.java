package com.kariqu.securitymanager.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.securitymanager.helper.RoleVo;
import com.kariqu.securitysystem.domain.Role;
import com.kariqu.securitysystem.domain.RoleScope;
import com.kariqu.securitysystem.domain.UrlPermission;
import com.kariqu.securitysystem.service.SecurityService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Asion
 * Date: 11-11-17
 * Time: 下午9:25
 */
@Controller
public class RoleController {

    private final Log logger = LogFactory.getLog(RoleController.class);

    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = "/role/grid")
    public void roleGrid(@RequestParam("start") int start, @RequestParam("limit") int limit, HttpServletResponse response) throws IOException {
        Page<Role> rolePage = securityService.queryRoleByPage(new Page<Role>(start / limit + 1, limit));

        for (Role role : rolePage.getResult()) {
            List<Integer> permissionIdList = securityService.queryRolePermission(role.getId());
            List<UrlPermission> urlPermissions = new LinkedList<UrlPermission>();
            for (Integer permissionId : permissionIdList) {
                UrlPermission permission = securityService.getPermission(permissionId);
                urlPermissions.add(permission);
            }
            role.setUrlPermissions(urlPermissions);
        }
        new JsonResult(true).addData("totalCount", rolePage.getTotalCount()).addData("result", rolePage.getResult()).toJson(response);
    }


    @RequestMapping(value = "/role/roleFunctionSetList/{roleId}")
    public void roleFunctionSetList(@PathVariable("roleId") int roleId, HttpServletResponse response) throws IOException {
        Role role = securityService.getRole(roleId);
        String functionSet = role.getFunctionSet();
        List<RoleVo> roleVoList = geneRoleVo();

        if (StringUtils.isNotEmpty(functionSet)) {
            String[] set = functionSet.split(",");
            List<String> list = Arrays.asList(set);

            for (RoleVo roleVo : roleVoList) {
                if (list.contains(roleVo.getFunctionSet())) {
                    roleVo.setHasExist(true);
                }
            }
        }
        new JsonResult(true).addData("totalCount", roleVoList.size()).addData("result", roleVoList).toJson(response);
    }


    @RequestMapping(value = "/role/updateRoleFunctionSet", method = RequestMethod.POST)
    public void updateRoleFunctionSet(int roleId, String[] functionSets, boolean hasActivate, HttpServletResponse response) throws IOException {
        try {
            Role role = securityService.getRole(roleId);
            for (int i = 0; i < functionSets.length; i++) {
                if (hasActivate) {
                    //激活操作
                    if (StringUtils.isEmpty(role.getFunctionSet())) {
                        role.setFunctionSet(functionSets[i]);
                    } else {
                        role.setFunctionSet(role.getFunctionSet() + "," + functionSets[i]);
                    }
                } else {
                    //取消操作
                    List functionSetList = new ArrayList(Arrays.asList(role.getFunctionSet().trim().split(",")));
                    functionSetList.remove(functionSets[i]);
                    role.setFunctionSet((functionSetList.toString().substring(1, functionSetList.toString().length() - 1)).replaceAll(" ", ""));
                }
            }
            securityService.updateRole(role);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }


    @RequestMapping(value = "/role/list")
    public
    @ResponseBody
    List<Role> roleList() {
        List<Role> roles = securityService.queryAllRole();
        return roles;
    }

    @RequestMapping(value = "/role/add", method = RequestMethod.POST)
    public void createRole(Role role, int[] permissionId, HttpServletResponse response) throws IOException {
        try {
            Role roleByName = securityService.getRoleByName(role.getRoleName());
            if (roleByName != null) {
                new JsonResult(false, "该角色已经存在").toJson(response);
                return;
            }
            securityService.createRole(role);
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
     * 保存某个角色的配置
     *
     * @param roleScope
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/role/config/save", method = RequestMethod.POST)
    public void saveRoleScope(RoleScope roleScope, HttpServletResponse response) throws IOException {
        try {
            RoleScope scope = securityService.queryRoleScopeByRoleIdAndResourceName(roleScope.getRoleId(), roleScope.getResource());
            if (scope != null) {
                new JsonResult(false, "已经存在了对这个角色的配置").toJson(response);
                return;
            }
            securityService.createRoleScope(roleScope);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("角色配置错误：" + e);
            new JsonResult(false, "配置失败").toJson(response);
        }

    }

    @RequestMapping(value = "/role/delete", method = RequestMethod.POST)
    public void deleteRole(int id, HttpServletResponse response) throws IOException {
        try {
            Role role = securityService.getRole(id);
            if (role.getRoleName().equals("超级管理员")) {
                new JsonResult(false, "不能删除超级管理员这个角色").toJson(response);
                return;
            }
            securityService.deleteRole(id);
            securityService.deleteAccountRoleByRoleId(id);
            securityService.deleteRolePermissionByRoleId(id);
            securityService.deleteRoleScopeByRoleId(id);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("删除角色错误：" + e);
            new JsonResult(false, "删除失败").toJson(response);
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
    @RequestMapping(value = "/role/update", method = RequestMethod.POST)
    public void updateRole(Role role, int[] permissionId, HttpServletResponse response) throws IOException {
        try {
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


    @RequestMapping(value = "/role/delete/batch", method = RequestMethod.POST)
    public void deleteRole(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids) {
                securityService.deleteRole(id);
                securityService.deleteAccountRoleByRoleId(id);
                securityService.deleteRolePermissionByRoleId(id);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("批量删除角色错误：" + e);
            new JsonResult(false, "批量删除失败").toJson(response);
        }
    }

    private List<RoleVo> geneRoleVo() {
        return Arrays.asList(
                new RoleVo("Account", "账户"),
                new RoleVo("Cms", "内容"),
                new RoleVo("Template", "模版"),
                new RoleVo("Order", "订单"),
                new RoleVo("Page", "页面"),
                new RoleVo("Design", "装修"),
                new RoleVo("Module", "模块"),
                new RoleVo("Category", "后台类目"),
                new RoleVo("NavigateCategory", "前台类目"),
                new RoleVo("Supplier", "商家"),
                new RoleVo("PictureSpace", "图片"),
                new RoleVo("Log", "日志"),
                new RoleVo("User", "用户"),
                new RoleVo("Feedback", "反馈"),
                new RoleVo("Finance", "退款"),
                new RoleVo("BackGoods", "退单"),
                new RoleVo("Consultation", "咨询"),
                new RoleVo("LimitTime", "限时折扣"),
                new RoleVo("Product", "商品"),
                new RoleVo("Valuation", "商品评论"),
                new RoleVo("Surveys", "问卷调查"),
                new RoleVo("Email", "邮件发送"),
                new RoleVo("MealSet", "套餐搭配"),
                new RoleVo("Coupon", "优惠劵管理"),
                new RoleVo("Sms", "短信发送"),
                new RoleVo("Const", "常数设置"),
                new RoleVo("Rotary", "幸运抽奖"),
                new RoleVo("Trade", "交易记录"),
                new RoleVo("Program", "设置终端首页图片"),
                new RoleVo("Exchange", "积分换购"));
    }

}
