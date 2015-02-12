package com.kariqu.suppliersystem.supplierManager.web;

import com.kariqu.common.encrypt.BCryptUtil;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.securitysystem.domain.Role;
import com.kariqu.securitysystem.service.SecurityService;
import com.kariqu.suppliercenter.domain.Supplier;
import com.kariqu.suppliercenter.domain.SupplierAccount;
import com.kariqu.suppliersystem.orderManager.web.BaseController;
import com.kariqu.suppliersystem.supplierManager.vo.SessionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 商家登陆界面功能
 *
 * @author:Wendy
 * @since:1.0.0 Date: 12-11-28
 * Time: 上午9:59
 */
@Controller
public class SupplierLoginController extends BaseController {

    private final Log logger = LogFactory.getLog(SupplierLoginController.class);

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Autowired
    private SecurityService securityService;



    @RequestMapping(value = "/")
    public String index(Model model, HttpServletResponse response, HttpServletRequest request) throws Exception {
        SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
        model.addAttribute("isMainAccount", supplierAccount.isMainAccount());
        return "index";
    }


    @RequestMapping(value = "supplier/error")
    public String toError(Model model, HttpServletResponse response, HttpServletRequest request) {
        model.addAttribute("msg","该用户没有角色或者没有权限");
        return "error";
    }

    @RequestMapping(value = "supplier/toLogin")
    public String toLogin(Model model, HttpServletResponse response, HttpServletRequest request) {
        model.addAttribute("imageVersion", RandomStringUtils.randomNumeric(6));
        return "login";
    }


    @RequestMapping(value = "supplier/jump")
    public String toJump(String url,Model model) {
        model.addAttribute("jumpUrl", url);
        return "jump";
    }


    /**
     * 带有角色和用户ID的登陆，这是在第一次验证通过之后让用户选择完角色之后的行为
     *
     * @param roleId
     * @param request
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "supplier/roleLogin", method = RequestMethod.POST)
    public String loginWithRole(int roleId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Role role = securityService.getRole(roleId);
        SessionUtils.withRole(request.getSession(), role);
        return "redirect:/" ;
    }


    /**
     * 登录功能
     *
     * @param accountName
     * @param password
     * @param imageCode
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "supplier/login", method = RequestMethod.POST)
    public String supplierLogin(String accountName, String password, String imageCode, String name, Model model, HttpServletRequest request) {
        Object imageCodeInSession = request.getSession().getAttribute("imageCode");
        if (null == imageCode || imageCodeInSession == null || !imageCode.equals(imageCodeInSession.toString())) {
            model.addAttribute("msg", "验证码错误");
            return "login";
        }
        Supplier supplier;
        SupplierAccount account;
        try {
            supplier = supplierService.queryCustomerByName(name);
            if (null == supplier) {
                model.addAttribute("msg", "该商家不存在");
                return "login";
            }

            account = supplierService.querySupplierAccountByName(accountName, supplier.getId());
            if (null == account) {
                model.addAttribute("msg", "该用户不存在");
                return "login";
            } else if (BCryptUtil.check(password, account.getPassword())) {
                if (!account.isNormal()) {
                    SessionUtils.doLogin(request.getSession(), account);
                    model.addAttribute("accountName", accountName);
                    return "change_pwd";
                } else {
                    Supplier customer = supplierService.queryCustomerById(account.getCustomerId());
                    if (null == customer) {
                        model.addAttribute("accountName", accountName);
                        model.addAttribute("msg", "该账号无效");
                        return "login";
                    }
                }
            } else {
                model.addAttribute("msg", "密码错误");
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("msg", "登陆出错，请重新登陆");
            logger.error("登陆出错",e);
            return "login";
        }
        SessionUtils.doLogin(request.getSession(), account);
//        List<Integer> roleIdList = securityService.queryAccountRole(account.getId());
//        if (roleIdList.size() > 1) {
//            List<Role> roleList = new ArrayList<Role>(roleIdList.size());
//            for (Integer roleId : roleIdList) {
//                roleList.add(securityService.getRole(roleId));
//            }
//            model.addAttribute("roleList", roleList);
//            return "supplierRole/selectRole";
//        } else if (roleIdList.size() == 0 && !account.isMainAccount()) {
//            model.addAttribute("msg", "该用户没有角色");
//            return "login";
//        }
        return "redirect:/";
    }




    @RequestMapping(value = "/logout")
    public void loginOut(HttpServletResponse response, HttpServletRequest request) throws IOException {
        request.getSession().invalidate();
        response.sendRedirect("/");
    }

}
