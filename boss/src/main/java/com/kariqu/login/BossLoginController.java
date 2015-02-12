package com.kariqu.login;

import com.kariqu.accountcenter.domain.Account;
import com.kariqu.accountcenter.service.AccountService;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.common.json.JsonUtil;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.securitysystem.domain.Role;
import com.kariqu.securitysystem.service.SecurityService;
import com.kariqu.securitysystem.util.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 登陆控制器
 * User: Asion
 * Date: 11-11-18
 * Time: 下午10:57
 */
@Controller
public class BossLoginController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    /**
     * 修改用户密码
     *
     * @param oldPassword
     * @param newPassword
     * @param response
     * @param request
     * @throws IOException
     */
    @RequestMapping(value = "/account/changePassword", method = RequestMethod.POST)
    @Permission("修改了密码")
    public void updateUserPassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Account currentAccount = SessionUtils.getLoginAccount(request.getSession());
        if (!currentAccount.getPassword().equals(MD5.getHashString(oldPassword))) {
            new JsonResult(false, "旧密码不对").toJson(response);
            return;
        }
        currentAccount.setPassword(MD5.getHashString(newPassword));
        accountService.updateAccount(currentAccount);
        new JsonResult(true).toJson(response);
    }

    /**
     * 带有角色和用户ID的登陆，这是在第一次验证通过之后让用户选择完角色之后的行为
     *
     * @param roleId
     * @param request
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/account/login")
    public void loginWithRole(int roleId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Role role = securityService.getRole(roleId);
        SessionUtils.withRole(request.getSession(), role);
        new JsonResult(true).toJson(response);
    }

    /**
     * 选择角色的界面导航
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/account/selectRole")
    public String selectRole(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        if (!SessionUtils.isLogin(request.getSession())) {
            return "redirect:/account/toLogin";
        }
        Account account = SessionUtils.getLoginAccount(request.getSession());
        List<Integer> roleIdList = securityService.queryAccountRole(account.getId());
        List<Role> roleList = new ArrayList<Role>(roleIdList.size());
        for (Integer roleId : roleIdList) {
            roleList.add(securityService.getRole(roleId));
        }
        String roleListJson = JsonUtil.objectToJson(roleList);
        model.addAttribute("roleList", roleListJson);
        return "selectRole";
    }


    /**
     * 登陆
     *
     * @param userName
     * @param password
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/account/login", method = RequestMethod.POST)
    @Permission("登陆")
    public void login(String userName, String password, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Account account = accountService.queryAccountByEnglishName(userName);
        if (account == null) {
            account = accountService.queryAccountByName(userName);
            if (account == null) {
                new JsonResult(false, "用户不存在").toJson(response);
                return;
            }
        }
        if (!account.getPassword().equals(MD5.getHashString(password))) {
            new JsonResult(false, "密码不正确").toJson(response);
            return;
        }
        SessionUtils.doLogin(request.getSession(), account);
        new JsonResult(true).toJson(response);
    }

    /**
     * 登出
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/account/loginOut")
    public String loginOut(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:" + urlBrokerFactory.getUrl("AdminHome").toString();
    }


    /**
     * 导航到登陆
     *
     * @return
     */
    @RequestMapping(value = "/account/toLogin")
    public String toLogin(Model model) {
        model.addAttribute("urlBroker", urlBrokerFactory);
        return "login";
    }

}
