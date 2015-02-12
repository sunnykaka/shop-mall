package com.kariqu.buyer.web.controller.myinfo;

import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.OrderView;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.buyer.web.helper.TradeViewHelper;
import com.kariqu.common.DateUtils;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.common.pagenavigator.PageProcessor;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.productcenter.domain.BrowsingHistory;
import com.kariqu.productcenter.domain.PictureDesc;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.service.BrowsingHistoryService;
import com.kariqu.productcenter.service.ProductPictureResolver;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.domain.Order;
import com.kariqu.tradecenter.domain.Progress;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: Maven
 * @since 1.0.0
 *        Date: 14-6-30
 *        Time: 18:10
 */
@Controller
public class MyBrowsingHistoryController {

    @Autowired
    private BrowsingHistoryService browsHistoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    protected URLBrokerFactory urlBrokerFactory;

    @RenderHeaderFooter
    @RequestMapping(value = "/my/history")
    public String toMyHistory(Integer state, Model model, HttpServletRequest request) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        int userId = sessionUserInfo.getId();

        tidyRecentBrowsHistory(userId, model);

        model.addAttribute("site_title", "浏览记录");
        return "myinfo/myHistory";
    }

    @RequestMapping(value = "/my/delAllHistory")
    public void historyList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        int userId = sessionUserInfo.getId();

        if (userId == 0) {
            new JsonResult(false, "请登录!").toJson(response);
            return;
        }

        browsHistoryService.delBrowsHistorynByUserId(userId);
        new JsonResult(true, "删除成功!").toJson(response);
    }

    /**
     * 整理一个月的浏览记录，分成今天，昨天，前天，一周内，一个月前
     *
     * @param userId
     * @param model
     */
    private void tidyRecentBrowsHistory(int userId, Model model) {
        List<BrowsingHistory> historyList = new LinkedList<BrowsingHistory>();

        // 获取当前时间，昨天，前天，一周前，一月前的日期
        DateUtils.DateFormatType dateFormatType = DateUtils.DateFormatType.SIMPLE_DATE_FORMAT_STR;
        Date today = DateUtils.parseDate(DateUtils.getCurrentDateStr(dateFormatType), dateFormatType);
        String todayKey = "今天," + DateUtils.formatDate(today, dateFormatType);
        Date yesterday = getTime(-1, false);
        String yesterdayKey = "昨天," + DateUtils.formatDate(yesterday, dateFormatType);
        Date beforeYesterday = getTime(-2, false);
        String beforeYesterdayKey = "前天," + DateUtils.formatDate(beforeYesterday, dateFormatType);
        Date withinWeek = getTime(-6, false);
        String withinWeekKey = "一周内," + DateUtils.formatDate(withinWeek, dateFormatType);
        Date withinMonth = getTime(-1, true);
        String withinMonthKey = "一月内," + DateUtils.formatDate(withinMonth, dateFormatType);

        historyList = browsHistoryService.queryRecentBrowsHistoryByUserId(userId, 0);
        if (null == historyList || historyList.size() == 0) { // 如果没有信息直接返回
            model.addAttribute("hasHistoryData", false);
            return;
        }

        Map<String, LinkedList<Map<String, Object>>> historyMap = new LinkedHashMap<String, LinkedList<Map<String, Object>>>();
        historyMap.put(todayKey, new LinkedList<Map<String, Object>>());
        historyMap.put(yesterdayKey, new LinkedList<Map<String, Object>>());
        historyMap.put(beforeYesterdayKey, new LinkedList<Map<String, Object>>());
        historyMap.put(withinWeekKey, new LinkedList<Map<String, Object>>());
        historyMap.put(withinMonthKey, new LinkedList<Map<String, Object>>());
        for (BrowsingHistory history : historyList) {
            Map<String, Object> productMap = this.getProductMap(history.getProductId());
            if (productMap != null) {
                productMap.put("id", history.getId());
                if (today.before(history.getCreateDate())) {    // 是否是今天的记录
                    historyMap.get(todayKey).add(productMap);
                } else if (yesterday.before(history.getCreateDate())) { //是否是昨天的记录
                    historyMap.get(yesterdayKey).add(productMap);
                } else if (beforeYesterday.before(history.getCreateDate())) {   // 是否是前天的记录
                    historyMap.get(beforeYesterdayKey).add(productMap);
                } else if (withinWeek.before(history.getCreateDate())) {    // 是否是一周内的记录
                    historyMap.get(withinWeekKey).add(productMap);
                } else if (withinMonth.before(history.getCreateDate())) {   // 是否是一月内的记录
                    historyMap.get(withinMonthKey).add(productMap);
                }
            }
        }

        model.addAttribute("historyMap", historyMap);
        model.addAttribute("hasHistoryData", true);
    }

    private Date getTime(int time, boolean isMonth) {
        Calendar cal = Calendar.getInstance();
        if (isMonth) {
            cal.add(Calendar.MONTH, time);
        } else {
            cal.add(Calendar.DATE, time);
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * @param productId
     * @return
     */
    private Map<String, Object> getProductMap(int productId) {
        Map<String, Object> map = new HashMap<String, Object>();
        Product product = productService.getProductById(productId);
        if (null == product) {
            return null;
        }
        map.put("url", urlBrokerFactory.getUrl("ProductDetail").addQueryData("productId", product.getId()).toString());
        map.put("product", product.getName());
        map.put("picture", getMainPicture(productId));
        map.put("price", productService.getProductPrice(productId));
        return map;
    }

    /**
     * 取列表主图
     */
    private String getMainPicture(int productId) {
        PictureDesc pictureDesc = productService.getPictureDesc(productId);

        return pictureDesc.getMainPicture().getPictureUrl();
    }
}
