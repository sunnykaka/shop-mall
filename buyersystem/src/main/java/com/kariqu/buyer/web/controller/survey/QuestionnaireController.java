package com.kariqu.buyer.web.controller.survey;

import com.kariqu.buyer.web.common.CheckFormToken;
import com.kariqu.buyer.web.common.PageTitle;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.common.Token;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.common.iptools.IpTools;
import com.kariqu.om.domain.Result;
import com.kariqu.om.domain.ResultDetail;
import com.kariqu.om.domain.Survey;
import com.kariqu.om.service.QuestionnaireService;
import com.kariqu.tradecenter.service.AddressService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 问卷调查
 *
 * User: Athens(刘杰)
 * Date: 13-4-24
 * Time: 上午11:25
 */
@Controller
public class QuestionnaireController {

    private static final Logger LOGGER = Logger.getLogger(QuestionnaireController.class);

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private AddressService addressService;

    @RenderHeaderFooter
    @RequestMapping(value = "/survey/{surveyId}")
    @PageTitle("问卷调查")
    @Token
    public String querySurvey(@PathVariable int surveyId, HttpServletRequest request, Model model) {
        Survey survey = questionnaireService.querySurvey(surveyId);
        if (survey == null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("没有此问卷调查的 Id 项: [" + surveyId + "], 访问 IP: [" + IpTools.getIpAddress(request)+ "]");
            }
            model.addAttribute("site_title", "操作有误！");
            model.addAttribute("msg", "没有此问卷调查");
            return "error";
        }
        model.addAttribute("survey", survey);

        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        if (sessionUserInfo != null) {
            // 用户的缺省收货地址
            model.addAttribute("address", addressService.queryDefaultAddress(sessionUserInfo.getId()));
        }
        return "survey/questionnaire";
    }

    @RenderHeaderFooter
    @RequestMapping(value = "/survey/submit", method = RequestMethod.POST)
    @PageTitle("问卷调查")
    @CheckFormToken
    public String submitSurvey(Result result, int questionCount, HttpServletRequest request) {
        List<ResultDetail> resultDetailList = new ArrayList<ResultDetail>();
        for (int i = 1; i <= questionCount; i++) {
            String questionId = request.getParameter("question_" + i);
            String[] answers = request.getParameterValues("answer_" + i);
            for (String answer : answers) {
                ResultDetail detail = new ResultDetail();
                detail.setQuestionId(Integer.parseInt(questionId));
                detail.setAnswerId(Integer.parseInt(answer));

                String write = request.getParameter("write_" + answer);
                if (StringUtils.isNotBlank(write))
                    detail.setAnswerInput(write);

                resultDetailList.add(detail);
            }
        }
        result.setResultDetailList(resultDetailList);
        questionnaireService.submitResult(result);
        return "survey/submitSuccess";
    }

}
