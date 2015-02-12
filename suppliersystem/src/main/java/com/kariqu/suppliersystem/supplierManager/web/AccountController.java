package com.kariqu.suppliersystem.supplierManager.web;

import com.kariqu.common.encrypt.BCryptUtil;
import com.kariqu.securitysystem.domain.Role;
import com.kariqu.securitysystem.service.SecurityService;
import com.kariqu.suppliercenter.domain.SupplierAccount;
import com.kariqu.suppliercenter.service.SupplierRoleService;
import com.kariqu.suppliersystem.common.JsonResult;
import com.kariqu.suppliersystem.orderManager.web.BaseController;
import com.kariqu.suppliersystem.supplierManager.vo.SessionUtils;
import com.kariqu.usercenter.domain.MailHeader;
import com.kariqu.usercenter.domain.MessageTemplateName;
import com.kariqu.usercenter.service.MessageTaskService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商家账号管理控制器
 *
 * @author:Wendy
 * @since:1.0.0 Date: 13-4-22
 * Time: 下午4:14
 */
@Controller
@RequestMapping("/account")
public class AccountController extends BaseController {


    private final Log logger = LogFactory.getLog(AccountController.class);

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SupplierRoleService supplierRoleService;

    @Autowired
    private MessageTaskService messageTaskService;


    /**
     * 查询某个商家的帐号
     */
    @RequestMapping(method = RequestMethod.GET)
    public void accountList(String accountName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
//        if (supplierAccount.getAccountName().equals("yijushang")) {
//            model.addAttribute("has", "yes");
//        }
        List<SupplierAccount> supplierAccountList = supplierService.querySupplierAccountBySupplierId(supplierAccount.getCustomerId(), accountName);
        new JsonResult(JsonResult.SUCCESS).addData(JsonResult.RESULT_TYPE_LIST, supplierAccountList).toJson(response);
    }


    /**
     * 获取商家账号分配角色的界面数据
     *
     * @param id
     * @param model
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/supplier/supplierAccount/supplierRoleConfigPageJump/{id}")
    public String supplierRoleConfigPage(@PathVariable("id") int id, Model model, HttpServletRequest request) throws IOException {
        SupplierAccount supplierAccount = supplierService.getSupplierAccountById(id);
        List<Integer> roleIds = securityService.queryAccountRole(id);
        /*获取该用户有的角色*/
        List<Role> roleList = new ArrayList<Role>();
        for (int roleId : roleIds) {
            Role role = securityService.getRole(roleId);
            roleList.add(role);
        }
        List<Integer> allRoleIds = supplierRoleService.queryRoleSupplierBySupplierId(supplierAccount.getCustomerId());
        /*获取该商家所有的角色*/
        List<Role> allRoleList = new ArrayList<Role>();
        for (int roleId : allRoleIds) {
            Role role = securityService.getRole(roleId);
            allRoleList.add(role);
        }
        model.addAttribute("supplierAccount", supplierAccount);
        model.addAttribute("roleList", roleList);
        model.addAttribute("allRoleList", allRoleList);
        return "supplierAccountPage/supplierRoleConfig";
    }


    /**
     * 给商家分配帐号
     *
     * @param supplierAccount
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(method = RequestMethod.POST)
    public void addCustomerAccount(SupplierAccount supplierAccount, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            SupplierAccount supplier = SessionUtils.getLoginAccount(request.getSession());
            SupplierAccount account = supplierService.querySupplierAccountByName(supplierAccount.getAccountName(), supplier.getCustomerId());
            if (account != null) {
                if (account.getCustomerId() == supplier.getCustomerId()) {
                    new JsonResult(JsonResult.FAILURE, "该帐号已存在").toJson(response);
                    return;
                }
            } else {
                String password = supplierAccount.getPassword();
                supplierAccount.setPassword(BCryptUtil.encryptPassword(supplierAccount.getPassword()));  // 加密，密码默认为"666666"
                supplierAccount.setCustomerId(supplier.getCustomerId());
                supplierService.createSupplierAccount(supplierAccount);
                // 给用户发送邮件
                Map mailParams = new HashMap();
                mailParams.put("mailKey", "message");
                mailParams.put("accountName", supplierAccount.getAccountName()); // 用于在邮件显示用户名
                mailParams.put("password", password); // 用于在邮件显示密码，默认为"666666"
                sendMail(mailParams, supplierAccount.getEmail());
            }
        } catch (Exception e) {
            logger.error("添加商家帐号错误：", e);
            new JsonResult(JsonResult.FAILURE, "添加失败").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }


    /**
     * 更新用户相关角色的配置
     *
     * @param supplierAccount
     * @param roleId
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/supplier/supplierAccount/update", method = RequestMethod.POST)
    public void updateSupplierAccount(SupplierAccount supplierAccount, int[] roleId, HttpServletResponse response) throws IOException {
        try {
            SupplierAccount account = supplierService.getSupplierAccountById(supplierAccount.getId());
            if (account.isMainAccount()) {
                new JsonResult(false, "该账号为主账号，不需要分配角色！").toJson(response);
                return;
            }
            //先清除角色关联
            securityService.deleteAccountRoleByUserId(supplierAccount.getId());
            if (roleId != null) {
                for (int role : roleId) {
                    securityService.createAccountRole(supplierAccount.getId(), role);
                }
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("更新角色配置错误：", e);
            new JsonResult(false, "更新失败").toJson(response);
        }
    }


    /**
     * 批量删除商家帐号
     *
     * @param ids
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/batchDelete", method = RequestMethod.POST)
    public void deleteSupplierAccount(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids) {
                SupplierAccount supplierAccount = supplierService.getSupplierAccountById(id);
                if (supplierAccount.isMainAccount()) {
                    new JsonResult(JsonResult.FAILURE, "删除失败,主账号不允许被删除").toJson(response);
                    return;
                }
                supplierService.deleteSupplierAccountById(id);
            }
            new JsonResult(JsonResult.SUCCESS).toJson(response);
        } catch (Exception e) {
            logger.error("删除商家帐号失败：", e);
            new JsonResult(JsonResult.FAILURE, "删除失败").toJson(response);
        }
    }


    /**
     * 修改密码
     *
     * @param oldPassword,accountName,password
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public void supplierChangePassword(String oldPassword, String password,  HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            SupplierAccount account = SessionUtils.getLoginAccount(request.getSession());
            if (!BCryptUtil.check(oldPassword, account.getPassword())) {
                new JsonResult(JsonResult.FAILURE, "原始密码错误").toJson(response);
            } else {
                changePassword(account, password);
                new JsonResult(JsonResult.SUCCESS).toJson(response);
            }
        } catch (Exception e) {
            logger.error("修改密码出错", e);
            new JsonResult(JsonResult.FAILURE, "原始密码错误").toJson(response);
        }

    }

    private void changePassword(SupplierAccount account, String password) {
        account.setPassword(BCryptUtil.encryptPassword(password));
        account.setNormal(true);
        supplierService.updateSupplierAccount(account);
    }


    /**
     * 发送HTML邮件
     *
     * @param mailParams
     * @param mail
     * @return
     */
    private boolean sendMail(Map mailParams, String mail) {
        MailHeader mailHeader = new MailHeader();
        mailHeader.setMailSubject("易居尚邮件通知");
        mailHeader.setMailTo(mail);
        mailHeader.setParams(mailParams);
        try {
            messageTaskService.sendHtmlMail(mailHeader, MessageTemplateName.ACCOUNT_INFORM);
        } catch (Exception e) {
            logger.error("邮件发送失败：" + e);
            return false;
        }
        return true;
    }
}
