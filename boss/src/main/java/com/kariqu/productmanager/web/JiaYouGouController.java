package com.kariqu.productmanager.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.productcenter.service.JiayougoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 家有购管理
 * User: Alec
 * Date: 13-10-16
 * Time: 下午3:50
 */
@Controller
public class JiaYouGouController {

    @Autowired
    private JiayougoService jiayougoService;

    /**
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/product/jyg", method = RequestMethod.POST)
    @Permission("加入家有购")
    public void jiayougou(int productId, HttpServletResponse response) throws IOException {
        try {
            jiayougoService.addProductToJYG(productId);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }
}
