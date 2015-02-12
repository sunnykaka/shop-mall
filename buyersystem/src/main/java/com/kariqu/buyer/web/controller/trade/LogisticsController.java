package com.kariqu.buyer.web.controller.trade;

import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.common.iptools.IpTools;
import com.kariqu.common.json.JsonUtil;
import com.kariqu.tradecenter.domain.BackLogistics;
import com.kariqu.tradecenter.domain.LogisticsInfo;
import com.kariqu.tradecenter.service.OperateLogisticsService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 物流控制器
 * User: Asion
 * Date: 12-6-5
 * Time: 下午12:02
 */
@Controller
public class LogisticsController {

    private static final Log LOGGER = LogFactory.getLog("LogisticsLog");

    @Autowired
    private OperateLogisticsService operaLogisticsService;


    /**
     * 向第三方物流发送请求.
     *
     * @throws IOException
     */
    //@RequestMapping(value = "/trade/logistics/message")
    public void queryLogistics(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 哪个快递(一定要提供)
        String company = request.getParameter("type");
        // 快递单号(一定要提供)
        String number = request.getParameter("postId");
        // 从哪来(可以为空)
        String from = request.getParameter("from");
        // 要到哪去(一定要提供)
        String to = request.getParameter("to");

        operaLogisticsService.handleThirdLogisticsInfo(company, number, from, to);
        new JsonResult(true).toJson(response);
    }

    /**
     * 查询第三方物流推送过来的物流信息.
     *
     * @throws IOException
     */
    @RequestMapping(value = "/trade/logistics/getData")
    public void queryData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String number = request.getParameter("num");
        if (StringUtils.isBlank(number)) {
            response.getWriter().write("缺少参数!");
            return;
        }

        LogisticsInfo logisticsInfo = operaLogisticsService.queryLogistics(number);
        response.getWriter().write(logisticsInfo != null ? logisticsInfo.getExpressValue() : "");
    }

    /**
     * 接受第三方物流请求的数据.
     *
     */
    @RequestMapping(value = "/logistics/back")
    public void logisticsBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String body = request.getParameter("param");
        if (LOGGER.isWarnEnabled())
            LOGGER.warn("第三方物流(kuaidi100)发送过来的物流信息: (" + body + ")");

        if (StringUtils.isBlank(body)) {
            response.getWriter().write(JsonUtil.objectToJson(new BackLogistics()));
            return;
        }
        try {
            operaLogisticsService.receiveThirdLogisticsInfo(body);
            response.getWriter().write(JsonUtil.objectToJson(new BackLogistics(true)));
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("处理第三方物流查询平台回调数据时异常: ", e);
            response.getWriter().write(JsonUtil.objectToJson(new BackLogistics()));
        }
    }

}
