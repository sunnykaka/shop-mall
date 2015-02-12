package com.kariqu.tradecenter.service.impl;

import com.kariqu.common.DateUtils;
import com.kariqu.common.json.JsonUtil;
import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.domain.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Athens(刘杰)
 * @Time 2013-03-05 11:02
 * @since 1.0.0
 */
public class JsTest {

    private long orderId = 188;
    private String expressNo = "1900266804197";

    private Date formatString(String str) {
        return DateUtils.parseDate(str, DateUtils.DateFormatType.DATE_FORMAT_STR);
    }

    @Test
    public void convertJs() {
        List<OrderStateHistory> historyList = new ArrayList<OrderStateHistory>();

        OrderStateHistory o1 = new OrderStateHistory();
        o1.setId(118);
        o1.setOrderId(orderId);
        o1.setOrderState(OrderState.Create);
        o1.setDate(formatString("2012-11-18 22:31:40"));
        o1.setOperator("陆琥MA");
        o1.setDoWhat("提交订单");
        historyList.add(o1);

        OrderStateHistory o2 = new OrderStateHistory();
        o2.setId(123);
        o2.setOrderId(orderId);
        o2.setOrderState(OrderState.Pay);
        o2.setDate(formatString("2012-11-18 22:42:57"));
        o2.setOperator("陆琥MA");
        o2.setDoWhat("付款完成");
        historyList.add(o2);

        OrderStateHistory o3 = new OrderStateHistory();
        o3.setId(170);
        o3.setOrderId(orderId);
        o3.setOrderState(OrderState.Verify);
        o3.setDate(formatString("2012-11-20 10:42:56"));
        o3.setOperator("罗凤江");
        o3.setDoWhat("已验货");
        historyList.add(o3);

        OrderStateHistory o4 = new OrderStateHistory();
        o4.setId(177);
        o4.setOrderId(orderId);
        o4.setOrderState(OrderState.Send);
        o4.setDate(formatString("2012-11-20 11:46:55"));
        o4.setOperator("罗凤江");
        o4.setDoWhat("已发货");
        historyList.add(o4);

        OrderStateHistory o5 = new OrderStateHistory();
        o5.setId(201);
        o5.setOrderId(orderId);
        o5.setOrderState(OrderState.Success);
        o5.setDate(formatString("2012-11-25 19:28:12"));
        o5.setOperator("陆琥MA");
        o5.setDoWhat("交易完成");
        historyList.add(o5);


        String historyDb = JsonUtil.objectToJson(historyList);

        System.out.println("historyList : " + historyDb + "\n");
        System.out.println("convertList : " + Arrays.asList(JsonUtil.json2Object(historyDb, OrderStateHistory[].class)));


        Logistics logistics = new Logistics();
        logistics.setId(181);
        logistics.setOrderId(orderId);
        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setWaybillNumber(expressNo);
        deliveryInfo.setDeliveryType(DeliveryInfo.DeliveryType.yunda);
        logistics.setDeliveryInfo(deliveryInfo);
        System.out.println("logistics : " + JsonUtil.objectToJson(logistics) + "\n");


        LogisticsInfo logisticsInfo = new LogisticsInfo();
        logisticsInfo.setExpressNo(expressNo);
        logisticsInfo.setStatus(1);
        logisticsInfo.setExpressValue("{\"message\":\"\",\"status\":\"shutdown\",\"lastResult\":{\"message\":\"ok\",\"state\":\"3\",\"data\":[{\"context\":\"由广东深圳公司龙岗区坂田杨美分部 派送，由 胡雪莲 签收\",\"time\":\"2012-11-21 18:50:25\",\"ftime\":\"2012-11-21 18:50:25\"},{\"context\":\"由广东深圳公司龙岗区坂田杨美分部 派送，由 胡雪莲 签收\",\"time\":\"2012-11-21 18:50:25\",\"ftime\":\"2012-11-21 18:50:25\"},{\"context\":\"到达广东深圳公司龙岗区坂田杨美分部 指定刘练派送\",\"time\":\"2012-11-21 17:21:17\",\"ftime\":\"2012-11-21 17:21:17\"},{\"context\":\"到达广东深圳公司龙岗区坂田杨美分部 指定刘练派送\",\"time\":\"2012-11-21 10:59:43\",\"ftime\":\"2012-11-21 10:59:43\"},{\"context\":\"到达 广东深圳公司龙岗区坂田杨美分部 进行派送扫描， 上级站点：广东深圳公司，发往\",\"time\":\"2012-11-21 10:53:44\",\"ftime\":\"2012-11-21 10:53:44\"},{\"context\":\"到达广东深圳公司 指定派送\",\"time\":\"2012-11-21 09:21:09\",\"ftime\":\"2012-11-21 09:21:09\"},{\"context\":\"到达广东深圳中转站 发往 广东深圳公司\",\"time\":\"2012-11-21 08:36:31\",\"ftime\":\"2012-11-21 08:36:31\"},{\"context\":\"到达广东广州中转站 发往 广东深圳中转站\",\"time\":\"2012-11-21 02:26:47\",\"ftime\":\"2012-11-21 02:26:47\"},{\"context\":\"到达广东广州中转站 发往 广东深圳中转站\",\"time\":\"2012-11-21 02:26:47\",\"ftime\":\"2012-11-21 02:26:47\"},{\"context\":\"到达广东广州中转站,上级地点：广东广州番禺区公司(511400)\",\"time\":\"2012-11-21 02:24:08\",\"ftime\":\"2012-11-21 02:24:08\"},{\"context\":\"收件公司：广东广州番禺区公司洛溪分部\",\"time\":\"2012-11-21 00:04:48\",\"ftime\":\"2012-11-21 00:04:48\"},{\"context\":\"收件公司：广东广州番禺区公司\",\"time\":\"2012-11-20 20:28:48\",\"ftime\":\"2012-11-20 20:28:48\"},{\"context\":\"在广东广州番禺区公司进行中转，并发往 广东广州中转站\",\"time\":\"2012-11-20 20:28:33\",\"ftime\":\"2012-11-20 20:28:33\"}],\"status\":\"200\",\"com\":\"yunda\",\"nu\":\"1900266804197\",\"ischeck\":\"1\",\"condition\":\"F00\"},\"billstatus\":\"check\"}");
        System.out.println("info : " + JsonUtil.objectToJson(logisticsInfo) + "\n");


        System.out.println("value:" + logisticsInfo.getExpressValue() + "\n");
        System.out.println(JsonUtil.json2Object(logisticsInfo.getExpressValue(), BackMsg.class));
    }
}
