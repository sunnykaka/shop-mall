package com.kariqu.securitymanager.web;

import com.kariqu.login.SessionUtils;
import com.kariqu.securitysystem.domain.Role;
import com.kariqu.securitysystem.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 权限检查控制器
 * 每个请求的权限过滤会在安全系统的过滤器中
 * 本控制器的作用是输出是否有某种权限的boolean值，然后客户端
 * 可通过这个值以控制UI的显示
 * User: Asion
 * Date: 11-11-21
 * Time: 下午3:00
 */
@Controller
public class PermissionCheckController {
    /**
     * 用ajax的方式检查权限
     *
     * @param request
     * @param path
     * @return
     */
    @RequestMapping("/account/authCheck")
    public
    @ResponseBody
    boolean checkAuth(HttpServletRequest request, String path) {
        Role currentRole = SessionUtils.getLoginRole(request.getSession());
        return currentRole.hasPermission(path);
    }

    /**
     * 用vm渲染的方式
     * 可对js中的操作权限做控制，比如不输出某个添加按钮
     *
     * @param fileName
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/js/fnCheck/{fileName}")
    public String checkAuth(@PathVariable("fileName") String fileName, HttpServletRequest request, Model model) {
        Role currentRole = SessionUtils.getLoginRole(request.getSession());
        model.addAttribute("role", currentRole);
        return "js/" + fileName;
    }


}
