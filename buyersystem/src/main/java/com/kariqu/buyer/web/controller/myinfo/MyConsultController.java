package com.kariqu.buyer.web.controller.myinfo;

import com.kariqu.buyer.web.common.PageTitle;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.common.pagenavigator.PageProcessor;
import com.kariqu.productcenter.domain.Consultation;
import com.kariqu.productcenter.domain.ConsultationCategory;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.service.ConsultationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户后台查询咨询
 * User: Alec
 * Date: 12-11-19
 * Time: 上午11:22
 */
@PageTitle( "我的咨询")
@Controller
public class MyConsultController {
    private final Log logger = LogFactory.getLog(MyConsultController.class);

    @Autowired
    private ConsultationService consultationService;



    @Autowired
    private com.kariqu.productcenter.service.ProductService productService;

    @RenderHeaderFooter
    @RequestMapping(value = "/my/consult")
    public String queryConsultation(Integer pageNo, ConsultationCategory category, Model model, HttpServletRequest request) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);


        pageNo = pageNo == null ? 1 : pageNo;
        category = category == null ? ConsultationCategory.all : category;

        model.addAttribute("category", category);
        model.addAttribute("categoryName", category.name());
        model.addAttribute("categoryName", category.toDesc());
        Page<Consultation> consultationPage = new Page<Consultation>(pageNo, 10);

        consultationPage = consultationService.searchConsultation(consultationPage, 
                (category.name()).equals("all") ? null : category, null, String.valueOf(sessionUserInfo.getId()));

        if (consultationPage.getResult().size() > 0) {
            Page<ConsultationAboutProduct> consultationAboutProductPage = getMyConsultView(consultationPage);
            model.addAttribute("consultationAboutProductPage", consultationAboutProductPage);
            model.addAttribute("consultationPageBar", PageProcessor.process(consultationAboutProductPage));
        }

        model.addAttribute("contentVm", "myinfo/myConsult.vm");
        return "myinfo/myInfoLayout";
    }

    private Page<ConsultationAboutProduct> getMyConsultView(Page<Consultation> consultationPage) {
        Page<ConsultationAboutProduct> consultationAboutProductPage = new Page<ConsultationAboutProduct>();
        List<ConsultationAboutProduct> consultationAboutProducts = new ArrayList<ConsultationAboutProduct>();

        for (Consultation consult : consultationPage.getResult()) {
            ConsultationAboutProduct consultationAboutProduct = new ConsultationAboutProduct();
            consultationAboutProduct.setConsultation(consult);
            int productId = consult.getProductId();
            Product product = productService.getProductById(productId);
            String productName = null;
            String mainPicture = null;
            if (null != product) {
                productName = product.getName();
                mainPicture = productService.getPictureDesc(productId).getMainPicture().getPictureUrl();
            }
            consultationAboutProduct.setProductName(productName);
            consultationAboutProduct.setProductPictureUrl(mainPicture);

            consultationAboutProducts.add(consultationAboutProduct);
        }
        consultationAboutProductPage.setPageNo(consultationPage.getPageNo());
        consultationAboutProductPage.setPageSize(consultationPage.getPageSize());
        consultationAboutProductPage.setResult(consultationAboutProducts);
        consultationAboutProductPage.setTotalCount(consultationPage.getTotalCount());
        return consultationAboutProductPage;
    }

}
