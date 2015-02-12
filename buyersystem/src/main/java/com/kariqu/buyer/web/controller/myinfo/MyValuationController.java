package com.kariqu.buyer.web.controller.myinfo;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.math.DoubleMath;
import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.common.PageTitle;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.common.DateUtils;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.common.pagenavigator.PageBar;
import com.kariqu.common.pagenavigator.PageProcessor;
import com.kariqu.productcenter.domain.Consultation;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.domain.Order;
import com.kariqu.tradecenter.domain.OrderItem;
import com.kariqu.tradecenter.domain.Valuation;
import com.kariqu.tradecenter.service.ValuationQuery;
import com.kariqu.tradecenter.service.ValuationService;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.runtime.typehandling.BigDecimalMath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.*;

/**
 * User: kyle
 * Date: 13-3-7
 * Time: 上午10:28
 */
@PageTitle("商品评价")
@Controller
public class MyValuationController {
    private final Log logger = LogFactory.getLog(MyValuationController.class);

    @Autowired
    private TradeCenterUserClient tradeCenterUserClient;

    @Autowired
    private ValuationService valuationService;

    @Autowired
    private UserService userService;

    @RenderHeaderFooter
    @RequestMapping(value = "/my/valuation")
    public String myValuation(String state, String pageNo, Model model) throws IOException {
        int appraise = NumberUtils.toInt(state, 0);
        int page = NumberUtils.toInt(pageNo, 1);

        model.addAttribute("site_title", "商品评价");
        model.addAttribute("state", appraise);
        model.addAttribute("pageNo", page);

        model.addAttribute("contentVm", "myinfo/myValuation.vm");
        return "myinfo/myInfoLayout";
    }

    /**
     * @param state 0.未评价, 1.已评价
     */
    @RequestMapping(value = "/my/valuation/list")
    public String myValuationList(String state, String pageNo, Model model, HttpServletRequest request) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);

        int appraise = NumberUtils.toInt(state, 0);
        int page = NumberUtils.toInt(pageNo, 1);

        Page<OrderItem> orderItemPage = tradeCenterUserClient.queryValuationByUserIdAndAppraise(sessionUserInfo.getId(),
                appraise, new Page<OrderItem>(page, 5));

        Map<Long, Valuation> valuationMap = new HashMap<Long, Valuation>();
        for (OrderItem item : orderItemPage.getResult()) {
            if (item.isAppraise()) {
                Valuation valuation = valuationService.getValuationByUserIdAndOrderItemId(sessionUserInfo.getId(), item.getId());
                if (valuation != null) {
                    valuationMap.put(item.getId(), valuation);
                }
            }
        }

        model.addAttribute("valuationPageBar", PageProcessor.process(orderItemPage));
        model.addAttribute("valuationPage", orderItemPage);
        model.addAttribute("orderItems", orderItemPage.getResult());
        model.addAttribute("valuationMap", valuationMap);

        model.addAttribute("pageNo", pageNo);
        model.addAttribute("state", state);

        return "myinfo/valuationList";
    }

    @RenderHeaderFooter
    @RequestMapping(value = "/my/valuation/add", method = RequestMethod.POST)
    public void createValuation(Valuation valuation, HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        try {
            SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
            String content = HtmlUtils.htmlEscape(valuation.getContent());

            if (StringUtils.isEmpty(content)) {
                new JsonResult(false, "评价内容不能为空!").toJson(response);
                return;
            }
            if (content.length() <10 || content.length() > 2000) {
                new JsonResult(false, "评价内容必须在10-2000字之间!").toJson(response);
                return;
            }
            if (valuationService.isValuation(sessionUserInfo.getId(), valuation.getOrderItemId())) {
                new JsonResult(false, "对不起,您已对该商品做出评价!").toJson(response);
                return;
            }

            valuation.setUserId(sessionUserInfo.getId());
            valuation.setUserName(sessionUserInfo.getUserName());
            valuation.setContent(content);

            if (valuation.getPoint() <= 0 || valuation.getPoint() > 5)
                valuation.setPoint(5);

            tradeCenterUserClient.changeOrderItemAppraise(valuation);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, "服务器繁忙,请稍后重试!").toJson(response);
            logger.error("提交咨询发生错误 !" + valuation, e);
        }
    }

    @RenderHeaderFooter
    @RequestMapping(value = "/my/valuation/addReply", method = RequestMethod.POST)
    public void addValuationReply(Valuation valuation, HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        try {
            SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
            String appendContent = HtmlUtils.htmlEscape(valuation.getAppendContent());

            if (StringUtils.isEmpty(appendContent)) {
                new JsonResult(false, "评价内容不能为空!").toJson(response);
                return;
            }
            if (appendContent.length() <10 || appendContent.length() > 2000) {
                new JsonResult(false, "评价内容必须在10-2000字之间!").toJson(response);
                return;
            }
            Valuation val = valuationService.getValuationByUserIdAndOrderItemId(sessionUserInfo.getId(), valuation.getOrderItemId());
            if (val == null || StringUtils.isNotBlank(val.getAppendContent())) {
                new JsonResult(false, "您未对此商品评价过或已经追加过评价!").toJson(response);
                return;
            }
            val.setAppendContent(appendContent);
            val.setAppendDate(new Date());

            valuationService.createValuationReply(val);
            new JsonResult(true).addData("appendDate", DateUtils.formatDate(val.getAppendDate(), DateUtils.DateFormatType.DATE_FORMAT_STR)).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, "服务器繁忙,请稍后重试!").toJson(response);
            logger.error("提交咨询发生错误 !" + valuation, e);
        }
    }


    @RenderHeaderFooter
    @RequestMapping(value = "/my/valuation/toValuation/{orderId}", method = RequestMethod.GET)
    public String toValuation(@PathVariable("orderId") long orderId, Model model, HttpServletRequest request) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);

        Order basicOrder = tradeCenterUserClient.getOrderBasic(orderId, sessionUserInfo.getId());
        Order order = tradeCenterUserClient.getOrderDetails(basicOrder.getOrderNo(), sessionUserInfo.getId());
        Map<Long, Valuation> valuationMap = new HashMap<Long, Valuation>();
        for (OrderItem item : order.getOrderItemList()) {
            if (item.isAppraise()) {
                valuationMap.put(item.getId(), valuationService.getValuationByUserIdAndOrderItemId(sessionUserInfo.getId(), item.getId()));
            }
        }
        model.addAttribute("orderItems", order.getOrderItemList());
        model.addAttribute("valuationMap", valuationMap);

        model.addAttribute("contentVm", "myinfo/orderComplete.vm");
        return "myinfo/myInfoLayout";
    }


    @RequestMapping(value = "/product/valuation")
    public void queryValuation(String productId,  String pageNo, String pageSize,  String likeFilter,
                               HttpServletResponse response) throws IOException {
        ValuationQuery valuationQuery;
        int proId = NumberUtils.toInt(productId);
        if (proId <= 0) {
            new JsonResult(false, "没有此商品").toJson(response);
            return;
        }
        int page = NumberUtils.toInt(pageNo);
        if (page <= 0) page = 1;
        int size = NumberUtils.toInt(pageSize);
        if (size <= 0 || size > 100) size = 5;
        ValuationQuery.LikeFilter lf;
        try{
            lf = Enum.valueOf(ValuationQuery.LikeFilter.class, StringUtils.isEmpty(likeFilter) ? "" : likeFilter);
            valuationQuery = ValuationQuery.asProductIdAndLikeFilter(proId, lf);
        }   catch (IllegalArgumentException e){
            valuationQuery = ValuationQuery.asProductId(proId);
        }

        valuationQuery.setPageNo(page);
        valuationQuery.setPageSize(size);
        Page<Valuation> valuationPage = valuationService.queryValuationAndImportValuation(valuationQuery);

        /*PageBar valuationPageBar = null;
        if (valuationPage.getResult().size() > 0) {
            valuationPageBar = PageProcessor.process(valuationPage);
        }  */

        List<Map<String, String>> resultList = Lists.newArrayList();
        for (Valuation valuation : valuationPage.getResult()) {
            Map<String, String> map = Maps.newHashMap();
            map.put("id", valuation.getId() + "");
            map.put("content", valuation.getContent());
            map.put("userName", valuation.getUserName());
            map.put("userId", valuation.getUserId() + "");
            map.put("point", valuation.getPoint() + "");
            map.put("updateDate", valuation.getUpdateDateStr());
            map.put("grade", valuation.getGrade() == null ? "" : userService.getUserGradeByGrade(valuation.getGrade().name()).getName());

            map.put("productId", valuation.getProductId() + "");
            map.put("orderItemId", valuation.getOrderItemId() + "");
            map.put("operator", valuation.getOperator());
            map.put("createDate", valuation.getCreateDateStr());
            map.put("orderCreateDate", valuation.getOrderCreateDateStr());
            map.put("operatorId", valuation.getOperatorId() + "");
            map.put("replyContent", valuation.getReplyContent());
            map.put("replyTime", valuation.getReplyTimeStr());
            map.put("appendContent", valuation.getAppendContent());
            map.put("appendReplyContent", valuation.getAppendReplyContent());
            map.put("appendDate", valuation.getAppendDateStr());
            map.put("appendReplyDate", valuation.getAppendReplyDateStr());
            map.put("appendOperator", valuation.getAppendOperator());
            resultList.add(map);
        }
        Map map = this.calculateValuation(proId);
        new JsonResult(true).addData("totalCount", valuationPage.getTotalCount())
                .addData("result", resultList).addData("valuationCount", map.get("valuationCount"))
                .addData("percent", map.get("percent")).addData("score", Float.valueOf(this.calculateScore(proId))).toJson(response);
    }

    //计算综合评分
    private String calculateScore(int productId) {
        List<Integer> points = getPoints(productId);
        float sum = 0f;
        for(Integer point : points) {
            sum += point;
        }
        return String.format("%.2f", points.size() == 0 ? 0 : sum / (double) points.size());
    }

    //按商品id查询此商品的所有的评价分数
    private List<Integer> getPoints(int productId) {
            return valuationService.queryPointByProductId(productId);
    }

    private Map calculateValuation(int productId) {
        List<Integer> points = getPoints(productId);
        Collection<Integer> likes = Collections2.filter(points, new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return input >= 4;
            }
        });
        Collection<Integer> unlikes = Collections2.filter(points, new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return input < 3;
            }
        });
        Collection<Integer> fines = Collections2.filter(points, new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return input == 3;
            }
        });
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for(ValuationQuery.LikeFilter vqlf : ValuationQuery.LikeFilter.values()) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("desc", vqlf.toDesc());
            switch(vqlf) {
                case all:map.put("count", points.size() + "");break;
                case Good:map.put("count", likes.size() + "");break;
                case Fine:map.put("count", fines.size() + "");break;
                case Bad:map.put("count", unlikes.size() + "");break;
            }
            map.put("name", vqlf.name());
            list.add(map);
        }


        Map<String, String> percent = new HashMap<String, String>();
        if (points.size() > 0) {
            int likesPercent = DoubleMath.roundToInt(BigDecimalMath.divide(likes.size(),
                    points.size()).doubleValue() * 100, RoundingMode.UP);
            percent.put("good", likesPercent +"");
            int finesPercent = DoubleMath.roundToInt(BigDecimalMath.divide(fines.size(),
                    points.size()).doubleValue() * 100, RoundingMode.UP);

            // 避免出现舍入计算后的失误
            if (likesPercent + finesPercent > 100)
                finesPercent = 100 - likesPercent;
            percent.put("fine", finesPercent +"");
            percent.put("bad", (100 - likesPercent - finesPercent) +"");
        } else {
            percent.put("good", "0");
            percent.put("fine", "0");
            percent.put("bad", "0");
        }

        Map resultMap = new HashMap();
        resultMap.put("valuationCount", list);
        resultMap.put("percent", percent);
        return resultMap;
    }



}
