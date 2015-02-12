package com.kariqu.productmanager.web;

import com.kariqu.common.DateUtils;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.*;
import com.kariqu.productcenter.service.ConsultationService;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productmanager.helper.ConsultationVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 后台咨询管理
 * User: Wendy
 * Date: 12-9-1
 * Time: 下午2:11
 */

@Controller
public class ConsultationController {

    private final Log logger = LogFactory.getLog(ConsultationController.class);

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private ProductService productService;

    /**
     * 后台咨询管理的筛选信息
     *
     * @param consultation
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/product/consultation/search")
    public void searchConsultation(Consultation consultation, int start, int limit, HttpServletResponse response) throws IOException {
        Page<Consultation> consultationPage = consultationService.queryConsultation(new Page<Consultation>(start / limit + 1, limit),
                consultation.getConsultationCategory(), consultation.getProductId(), consultation.getHasAnswer());
        List<ConsultationVo> consultationVos = new ArrayList<ConsultationVo>();
        for (Consultation consultations : consultationPage.getResult()) {
            if (consultations.getAnswerContent() != null && !consultations.getAnswerContent().equals("")) {
                if (consultations.getAnswerContent().indexOf("<br>") > 0) {
                    consultations.setAnswerContent(consultations.getAnswerContent().replaceAll("<br>", "\n"));
                }
            }
            Product product = productService.getProductById(consultations.getProductId());
            if (product != null) {
                ConsultationVo consultationVo = new ConsultationVo();
                consultationVo.setProductName(product.getName());
                consultationVo.setProductCode(product.getProductCode());
                consultationVo.setAnswerContent(consultations.getAnswerContent());
                consultationVo.setAnswerTime(DateUtils.formatDate(consultations.getAnswerTime(), DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA_HOUR));
                consultationVo.setAskContent(consultations.getAskContent());
                consultationVo.setAskedUserName(consultations.getAskedUserName());
                consultationVo.setAskTime(DateUtils.formatDate(consultations.getAskTime(), DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA_HOUR));
                consultationVo.setConsultationCategory(consultations.getConsultationCategory());
                consultationVo.setHasAnswer(consultations.getHasAnswer());
                consultationVo.setId(consultations.getId());
                consultationVos.add(consultationVo);
            } else {
                logger.warn("遍历咨询列表时发现被删除的商品");
            }
        }
        new JsonResult(true).addData("totalCount", consultationPage.getTotalCount()).addData("result", consultationVos).toJson(response);
    }

    /**
     * 回答咨询信息
     *
     * @param consultation
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/consultation/update", method = RequestMethod.POST)
    @Permission("回答咨询")
    public void updateConsultation(Consultation consultation, HttpServletResponse response) throws IOException {
        try {
            if (consultation.getAnswerContent().trim().equals("")) {
                new JsonResult(false, "回答信息不允许空值").toJson(response);
                return;
            }
            consultation.setAnswerContent(htmlFilter(consultation.getAnswerContent()));
            if (consultation.getAnswerContent().indexOf("\n") > 0) {
                consultation.setAnswerContent(consultation.getAnswerContent().replaceAll("\n", "<br>"));
            }
            consultationService.answerConsultation(consultation.getId(), consultation.getAnswerContent(), new Date());

        } catch (Exception e) {
            logger.error("修改咨询信息失败：" + e);
            new JsonResult(false, "编辑失败").toJson(response);
        }

    }

    /**
     * 删除咨询信息
     *
     * @param ids
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/consultation/delete", method = RequestMethod.POST)
    @Permission("删除商品咨询")
    public void deleteConsultation(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids) {
                consultationService.deleteConsultationById(id);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("删除咨询信息错误：" + e);
            new JsonResult(false, "删除咨询信息失败").toJson(response);
        }
    }

    private String htmlFilter(String content) {
        if (content.indexOf(">") > 0) {
            content = content.replaceAll(">", "&gt;");
        }
        if (content.indexOf("<") > 0) {
            content = content.replaceAll("<", "&lt;");
        }
        return content;
    }
}
