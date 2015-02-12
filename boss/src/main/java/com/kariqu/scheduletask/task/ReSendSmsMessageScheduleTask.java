package com.kariqu.scheduletask.task;

import com.kariqu.scheduletask.ScheduleTaskTemplate;
import com.kariqu.usercenter.domain.MessageTask;
import com.kariqu.usercenter.service.MessageTaskService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: kyle
 * Date: 13-1-9
 * Time: 下午2:57
 */
public class ReSendSmsMessageScheduleTask extends ScheduleTaskTemplate {

    private final Log LOGGER = LogFactory.getLog(ReSendSmsMessageScheduleTask.class);

    @Autowired
    private MessageTaskService messageTaskService;


    @Override
    public Map<String, String> doTrigger(Map<String,String>  taskParams) {
        Map<String, String> executeResult = new HashMap<String, String>();
        List<MessageTask> messageTasks = messageTaskService.queryNotSendedMessageTask();
        int failCount = 0;
        for (MessageTask item : messageTasks) {
              if (!messageTaskService.reSendSmsMessage(item))
                  failCount++;
        }
        executeResult.put("count", String.valueOf(messageTasks.size()));
        executeResult.put("failCount", String.valueOf(failCount));
        return executeResult;
    }
}
