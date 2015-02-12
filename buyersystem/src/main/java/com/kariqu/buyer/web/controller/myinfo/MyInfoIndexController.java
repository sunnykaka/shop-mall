package com.kariqu.buyer.web.controller.myinfo;

import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.common.uri.URLBrokerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 我的账户的首页控制
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-3
 *        Time: 上午11:40
 */
@Controller
public class MyInfoIndexController {

    @Autowired
    protected URLBrokerFactory urlBrokerFactory;


    @RequestMapping(value = "/my")
    @RenderHeaderFooter
    public String myAccount() {
        return "redirect:" + urlBrokerFactory.getUrl("MyAccount").toString();
    }
}
