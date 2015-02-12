package com.kariqu.common;

import com.kariqu.accountcenter.domain.Account;
import com.kariqu.login.SessionUtils;
import com.kariqu.om.util.SystemMonitor;
import com.kariqu.securitysystem.domain.Role;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 首页控制器
 * User: Asion
 * Date: 11-8-13
 * Time: 上午9:40
 */

@Controller
public class IndexController {

    /**
     * 后台桌面首页
     * 根据账户角色的功能集合来现实图标菜单
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/")
    public String index(HttpServletRequest request, Model model) {
        Role currentRole = SessionUtils.getLoginRole(request.getSession());
        Account account = SessionUtils.getLoginAccount(request.getSession());
        String functionSet = currentRole.getFunctionSet();
        if (StringUtils.isNotEmpty(functionSet)) {
            String[] funcArray = functionSet.split(",|，");
            Set<String> set = new HashSet<String>();
            for (String func : funcArray) {
                set.add(func.trim());
            }
            model.addAttribute("functionSet", set);
        } else {
            model.addAttribute("functionSet", Collections.emptyList());

        }
        model.addAttribute("account", account);

        model.addAllAttributes(SystemMonitor.gatherJvmInfo());
        return "index";
    }

}
