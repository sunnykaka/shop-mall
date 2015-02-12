package com.kariqu.omcenter.web;

import com.kariqu.common.JsonResult;
import com.kariqu.om.domain.Const;
import com.kariqu.om.service.ConstService;
import com.kariqu.tradecenter.client.TradeCenterBossClient;
import com.kariqu.tradecenter.client.TradeCenterSupplierClient;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.domain.SubmitOrderInfo;
import com.kariqu.tradecenter.excepiton.OrderBaseException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/const")
public class ConstController {

    private static final Log LOG = LogFactory.getLog("root");

    @Autowired
    private ConstService constService;

    @Autowired
    private TradeCenterBossClient tradeCenterBossClient;

    @RequestMapping(value = "/list")
    public String list(Model model) {
        model.addAttribute("constList", constService.getAllConst());
        return "constForm";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public void update(Const constInfo, HttpServletResponse response) throws IOException {
        try {
            constService.updateConst(constInfo);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            LOG.error("更新常数时异常:" + e.getMessage());
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    /**
     * 自动生成用户及订单
     */
    @RequestMapping("/auto/order")
    public void automaticOrder(String orderInfo, HttpServletResponse response) throws IOException {
        if (StringUtils.isBlank(orderInfo)) {
            new JsonResult(true).toJson(response);
            return;
        }
        int rowCount = 0;
        for (String order : orderInfo.split(System.getProperty("line.separator"))) {
            rowCount++;
            if (StringUtils.isBlank(order)) continue;

            try {
                tradeCenterBossClient.automaticOrder(order);
            } catch (OrderBaseException e) {
                new JsonResult(false, "第 " + rowCount + " 行生成订单时出错!" + e.getMessage()).toJson(response);
                return;
            }
        }
        new JsonResult(true, "OK! 共生成 " + rowCount + " 条订单!").toJson(response);
    }

    /**
     * 确认数天前的订单
     */
    @RequestMapping("/auto/confirm")
    public void confirmOrder(String day, String limit, String userNameRegex, HttpServletResponse response) throws IOException {
        int dayTime = NumberUtils.toInt(day);
        int lt = NumberUtils.toInt(limit);
        int count = 0;
        if (dayTime > 0 && lt > 0) {
            try {
                if (StringUtils.isBlank(userNameRegex)) userNameRegex = "^[a-l]0[0-9]{3}$";

                count = tradeCenterBossClient.confirmNotTrueOrder(dayTime, lt, userNameRegex);
            } catch (OrderBaseException e) {
                new JsonResult(false, e.getMessage()).toJson(response);
                return;
            }
        }
        new JsonResult(true, "OK! 确认了 " + count + " 条订单!").toJson(response);
    }

    /**
     * 修改地址
     */
    @RequestMapping("/auto/updateAddress")
    public void updateAddress(String day, HttpServletResponse response) throws IOException {
    }

}
