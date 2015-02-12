package com.kariqu.securitymanager.web;

import com.kariqu.accountcenter.domain.Account;
import com.kariqu.accountcenter.service.AccountService;
import com.kariqu.common.JsonResult;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.securitysystem.domain.Role;
import com.kariqu.securitysystem.service.SecurityService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Asion
 * Date: 11-11-16
 * Time: 下午5:38
 */
@Controller
public class RoleConfigController{

    private final Log logger = LogFactory.getLog(RoleConfigController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private SecurityService securityService;


    /**
     * 从账户中心读取员工列表
     * @param start
     * @param limit
     * @return
     */
    @RequestMapping(value = "/account/grid")
    public void accountGrid(@RequestParam("start") int start, @RequestParam("limit") int limit,HttpServletResponse response) throws IOException {
        Page<Account> accountPage = accountService.queryAccountByPage(new Page<Account>(start / limit + 1, limit));
        new JsonResult(true).addData("totalCount", accountPage.getTotalCount()).addData("result",accountPage.getResult()).toJson(response);
    }

    /**
     * 得到用户的角色列表
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/account/roleList/{userId}")
    public
    @ResponseBody
    List<Role> accountRoleList(@PathVariable("userId") int userId) {
        List<Role> roleList = new LinkedList<Role>();
        List<Integer> roleIdList = securityService.queryAccountRole(userId);
        for (Integer roleId : roleIdList) {
            Role role = securityService.getRole(roleId);
            roleList.add(role);
        }
        return roleList;
    }


    /**
     * 更新角色配置
     *
     * @param account
     * @param roleId
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/account/update", method = RequestMethod.POST)
    public void updateUser(Account account, int[] roleId, HttpServletResponse response) throws IOException {
        try{
            //先清除角色关联
            securityService.deleteAccountRoleByUserId(account.getId());
            if (roleId != null) {
                for (int role : roleId) {
                    securityService.createAccountRole(account.getId(), role);
                }
            }
            new JsonResult(true).toJson(response);
        }catch (Exception e){
            logger.error("更新角色配置错误："+ e);
            new JsonResult(false, "更新失败").toJson(response);
        }
    }


}
