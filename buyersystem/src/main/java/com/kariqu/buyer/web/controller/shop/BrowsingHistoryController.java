package com.kariqu.buyer.web.controller.shop;

import com.kariqu.buyer.web.common.CartTrackUtil;
import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.productcenter.domain.BrowsingHistory;
import com.kariqu.productcenter.domain.PictureDesc;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.service.BrowsingHistoryService;
import com.kariqu.productcenter.service.ProductPictureResolver;
import com.kariqu.productcenter.service.ProductService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 浏览历史记录
 * User: Alec
 * Date: 13-8-2
 * Time: 上午9:47
 */
@Controller
public class BrowsingHistoryController {

    private static Log logger = LogFactory.getLog(BrowsingHistoryController.class);

    @Autowired
    private BrowsingHistoryService browsHistoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    protected URLBrokerFactory urlBrokerFactory;

    @RequestMapping(value = "/addHistory")
    public void addHistory(int productId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
            int userId = sessionUserInfo != null ? sessionUserInfo.getId() : 0;
            String trackId = CartTrackUtil.readOrWriteTrackId(request);

            browsHistoryService.addBrowsHistory(userId, trackId, productId);
            new JsonResult(true, "浏览历史已记录").toJson(response);
        } catch (Exception ignore) {
            logger.error("添加用户浏览历史记录出错", ignore);
            new JsonResult(true, "浏览历史记录失败").toJson(response);
        }
    }

    @RequestMapping(value = "/historyList")
    public void historyList(String productId, String pageSize, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<BrowsingHistory> historyList = new LinkedList<BrowsingHistory>();

        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        String trackId = CartTrackUtil.getTrackId(request);
        int limit = NumberUtils.toInt(pageSize);
        limit = (limit > 0 && limit < 20) ? limit : 12;
        // 有登陆则使用 用户Id 查询, 若无则使用跟踪Id 查询
        if (sessionUserInfo != null) {
            historyList = browsHistoryService.queryBrowsHistoryByUserIdWithOutProductId(sessionUserInfo.getId(), NumberUtils.toInt(productId), limit);
        } else if (StringUtils.isNotBlank(trackId)) {
            historyList = browsHistoryService.queryBrowsHistoryByTrackIdWithOutProductId(trackId, NumberUtils.toInt(productId), limit);
        }

        List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
        for (BrowsingHistory history : historyList) {
            Map<String, Object> productMap = productService.getProductMap(history.getProductId());
            if (productMap != null) {
                productMap.put("id", history.getId());
                list.add(productMap);
            }
        }
        new JsonResult(true).addData("history", list).toJson(response);
    }

    @RequestMapping(value = "/delHistory")
    public void historyList(String id, HttpServletResponse response) throws IOException {
        long historyId = NumberUtils.toLong(id);
        if (historyId <= 0) {
            new JsonResult(false, "无此浏览历史!").toJson(response);
            return;
        }

        browsHistoryService.delBrowsHistoryn(historyId);
        new JsonResult(true, "删除成功!").toJson(response);
    }

}
