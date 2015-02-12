package com.kariqu.scheduletask.web;

import com.kariqu.common.iptools.IpTools;
import com.kariqu.scheduletask.ScheduleTask;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * User: kyle
 * Date: 13-1-9
 * Time: 下午2:06
 */
@Controller
public class ScheduleTaskController {

    private String serverIp;

    /**
     * 任务集合，使用key-value结构，key-任务名称，value-任务对象
     * resendMsg : 重新补发未发送成功的短息
     * closeOrder:关闭超时订单
     * autoSuccessOrder:自动将未已完成的订单置为已交易成功
     */
    private Map<String, ScheduleTask> scheduleTaskMap;

    @RequestMapping(value = "/schedule/{taskName}")
    public void doSchedule(@PathVariable("taskName") String taskName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestAddress = IpTools.getIpAddress(request);
        if (StringUtils.containsIgnoreCase(serverIp, requestAddress)) {
            Map<String, String> executeResult = scheduleTaskMap.get(taskName).execute(request.getParameterMap());
            response.getWriter().write(executeResult.toString());
        } else {
            response.getWriter().write("illegal external request! IP: " + requestAddress);
        }
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public void setScheduleTaskMap(Map<String, ScheduleTask> scheduleTaskMap) {
        this.scheduleTaskMap = scheduleTaskMap;
    }


}
