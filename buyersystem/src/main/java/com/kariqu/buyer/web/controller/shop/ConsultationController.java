package com.kariqu.buyer.web.controller.shop;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kariqu.buyer.web.common.CheckUserException;
import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.common.DateUtils;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Consultation;
import com.kariqu.productcenter.domain.ConsultationCategory;
import com.kariqu.productcenter.service.ConsultationService;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 商品资讯控制器
 *
 * @author: enoch
 * @since 1.0.0
 *        Date: 12-8-30
 *        Time: 上午10:17
 */
@Controller
public class ConsultationController {

    private static final Log logger = LogFactory.getLog(ConsultationController.class);

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private UserService userService;


    @RequestMapping(value = "/product/consultation")
    public void queryConsultation(String productId, String pageNo, String pageSize, String category,
                                  HttpServletResponse response) throws Exception {
        int proId = NumberUtils.toInt(productId);
        if (proId <= 0) {
            new JsonResult(false, "没有此商品").toJson(response);
            return;
        }

        int page = NumberUtils.toInt(pageNo);
        if (page <= 0) page = 1;
        int size = NumberUtils.toInt(pageSize);
        if (size <= 0 || size > 100) size = 5;

        ConsultationCategory consultationCategory = ConsultationCategory.all;
        if (StringUtils.isNotBlank(category)) {
            try {
                consultationCategory = ConsultationCategory.valueOf(category);
            } catch (IllegalArgumentException e) {
                // ignore
            }
        }

        Page<Consultation> consultationPage = new Page<Consultation>(page, size);
        // 客服是否有回复咨询
        int hasAnswer = 1;
        consultationPage = consultationService.queryConsultation(consultationPage, consultationCategory, proId, hasAnswer);
        List<Consultation> rs = consultationPage.getResult();
        List<Map<String, String>> resultList = Lists.newArrayList();
        for (Consultation consultation : rs) {
            Map<String, String> map = Maps.newHashMap();
            map.put("askUserName", consultation.getAskedUserName());
            map.put("category", consultation.getConsultationCategory().toDesc());
            map.put("askContent", consultation.getAskContent());
            map.put("askTime", DateUtils.formatDate(consultation.getAskTime(), DateUtils.DateFormatType.DATE_MINUTE_FORMAT_STR));
            map.put("answerContent", consultation.getAnswerContent());
            map.put("answerTime", DateUtils.formatDate(consultation.getAnswerTime(), DateUtils.DateFormatType.DATE_MINUTE_FORMAT_STR));
            map.put("grade", consultation.getGrade() == null ? "" : userService.getUserGradeByGrade(consultation.getGrade().name()).getName());
            resultList.add(map);
        }
        JsonResult result = new JsonResult(true).addData("totalCount", consultationPage.getTotalCount()).addData("result", resultList);

        List<Map<String, Object>> categoryList = Lists.newArrayList();
        int sum = 0;
        for (ConsultationCategory conCategory : ConsultationCategory.iter()) {
            if (conCategory.queryAll()) continue;

            int count = consultationService.queryConsultationNumById(proId, conCategory, hasAnswer);
            Map<String, Object> map = Maps.newHashMap();
            map.put("name", conCategory.name());
            map.put("desc", conCategory.toDesc());
            map.put("count", count);
            categoryList.add(map);
            sum += count;
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", ConsultationCategory.all.name());
        map.put("desc", ConsultationCategory.all.toDesc());
        map.put("count", sum);
        categoryList.add(0, map);

        result.addData("consultCategory", categoryList);
        result.toJson(response);
    }

    /**
     * 添加咨询
     *
     * @param consultation
     * @return
     */
    @RequestMapping(value = "/my/consultation/add", method = RequestMethod.POST)
    public void createConsultation(Consultation consultation, String imageCode,
                                   HttpServletRequest request, HttpServletResponse response) throws IOException {
        Object imageCodeInSession = request.getSession().getAttribute("imageCode");
        if (imageCodeInSession == null || StringUtils.isBlank(imageCode) || !imageCode.equals(imageCodeInSession.toString())) {
            new JsonResult(false, "验证码错误").toJson(response);
            return;
        }
        request.getSession().removeAttribute("imageCode");

        try {
            SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
            if (sessionUserInfo == null) {
                new JsonResult(false, "请先登录").toJson(response);
                return;
            }
            if (consultation.getAskContent().length() > 1000 || consultation.getAskContent().length() < 5) {
                new JsonResult(false, "咨询内容请保证在 5 ~ 1000 之间");
                return;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("用户:" + sessionUserInfo.getUserName() + "ID为：" + sessionUserInfo.getId()
                        + "添加的咨询为" + consultation.toString());
            }
            consultation.setAskContent(HtmlUtils.htmlEscape(consultation.getAskContent()));
            consultation.setAskUserId(sessionUserInfo.getId());
            consultation.setAskedUserName(sessionUserInfo.getUserName());
            consultationService.createConsultation(consultation);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, "服务器出错").toJson(response);
            logger.error("提交咨询发生错误", e);
        }
    }

}
