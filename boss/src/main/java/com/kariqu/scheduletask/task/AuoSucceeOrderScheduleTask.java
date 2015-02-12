package com.kariqu.scheduletask.task;

import com.kariqu.scheduletask.ScheduleTaskTemplate;
import com.kariqu.tradecenter.service.OrderQueryService;
import com.kariqu.tradecenter.service.TradeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * User: kyle
 * Date: 13-1-9
 * Time: 下午4:05
 */
//TODO:自动让订单成功的服务还未实现
public class AuoSucceeOrderScheduleTask extends ScheduleTaskTemplate {
    private final Log LOGGER = LogFactory.getLog(AuoSucceeOrderScheduleTask.class);


    @Autowired
    private OrderQueryService orderQueryService;

    @Autowired
    private TradeService tradeService;

    @Override
    public Map<String, String> doTrigger(Map<String,String> params) {
        return null;
    }
}
