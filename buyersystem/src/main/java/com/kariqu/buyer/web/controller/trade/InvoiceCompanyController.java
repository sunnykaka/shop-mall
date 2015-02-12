package com.kariqu.buyer.web.controller.trade;

import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.tradecenter.domain.InvoiceCompany;
import com.kariqu.tradecenter.service.InvoiceCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用常用发票单位管理
 * User: Asion
 * Date: 12-6-5
 * Time: 下午12:08
 */
@Controller
public class InvoiceCompanyController {

    @Autowired
    private InvoiceCompanyService invoiceCompanyService;


    @RequestMapping(value = "/user/invoice/company/add", method = RequestMethod.POST)
    public void createInvoiceCompany(final InvoiceCompany invoiceCompany, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        invoiceCompany.setUserId(sessionUserInfo.getId());
        invoiceCompany.setCompanyName(HtmlUtils.htmlEscape(invoiceCompany.getCompanyName()));
        invoiceCompanyService.createInvoiceCompany(invoiceCompany);
        new JsonResult(true).addData("dataId", invoiceCompany.getId()).toJson(response);
    }

    @RequestMapping(value = "/user/invoice/company/update", method = RequestMethod.POST)
    public void updateInvoiceCompany(final InvoiceCompany invoiceCompany, HttpServletResponse response, HttpServletRequest request) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        if (sessionUserInfo != null) {
            invoiceCompany.setUserId(sessionUserInfo.getId());
            invoiceCompany.setCompanyName(HtmlUtils.htmlEscape(invoiceCompany.getCompanyName()));
            invoiceCompanyService.updateInvoiceCompany(invoiceCompany);
            new JsonResult(true).addData("dataId", invoiceCompany.getId()).toJson(response);
        }
    }

    @RequestMapping(value = "/user/invoice/company/delete", method = RequestMethod.POST)
    public void deleteInvoiceCompany(int invoiceCompanyId, HttpServletResponse response, HttpServletRequest request) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        if (sessionUserInfo != null) {
            invoiceCompanyService.deleteInvoiceCompany(invoiceCompanyId);
            new JsonResult(true).addData("dataId", invoiceCompanyId).toJson(response);
        } else {
            new JsonResult(false).toJson(response);
        }
    }

}
