package com.kariqu.scheduletask.task;

import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import com.kariqu.scheduletask.ScheduleTaskTemplate;
import com.kariqu.tradecenter.service.OrderQueryService;
import com.kariqu.tradecenter.service.TradeService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * User: kyle
 * Date: 13-1-9
 * Time: 下午4:04
 */
public class CloseOrderScheduleTask extends ScheduleTaskTemplate {
    private final Log LOGGER = LogFactory.getLog(CloseOrderScheduleTask.class);



    @Autowired
    private OrderQueryService orderQueryService;

    @Autowired
    private TradeService tradeService;

    @Override
    public Map<String, String> doTrigger(Map<String,String>  taskParams) {

        int delay=24;
       if(null !=taskParams ){
           if(StringUtils.isNumeric(taskParams.get("delay"))){
               delay= Integer.parseInt(taskParams.get("delay"));
           }
       }
     // 查询超过指定时间还未进行支付的 虚拟订单, 将订单进行取消, 库存回加等操作
        List<Long> notPayList = orderQueryService.queryNotPayOrder(delay);
        Map<String,String> result= Maps.newHashMap();
        int failCount=0;
        for (long orderId : notPayList) {
            // 在 for 内部进行 try catch, 避免因为取消某个订单异常后, 其他的订单将不再被执行的问题.
            try {
                // 事务的处理基于虚拟订单
               // tradeService.triggerOrderCancel(orderId);
                LOGGER.error("ignore info:将订单编号为 [" + orderId + "] 的订单取消成功!");
            } catch (Exception e) {
                failCount++;
                LOGGER.warn("将订单编号为 [" + orderId + "] 的订单进行取消时异常: " + e);
            }
        }

        result.put("count",String.valueOf(notPayList.size()));
        result.put("failCount",String.valueOf(failCount));
        return  result;
    }

}
