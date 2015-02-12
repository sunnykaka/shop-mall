package com.kariqu.suppliersystem.orderManager.web;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * inspect（验货）页面功能
 *
 * @author:Wendy
 * @since:1.0.0 Date: 12-11-29
 * Time: 下午4:23
 */
@Controller
public class InspectionController extends BaseController {

    private final Log logger = LogFactory.getLog(InspectionController.class);


    @RequestMapping(value = "supplier/orderInspection/inspectionPage")
    public String inspectionPage(Model model, HttpServletResponse response, HttpServletRequest request) {
        model.addAttribute("has", "no");
        return "inspection";
    }

}

