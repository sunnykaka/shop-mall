package com.kariqu.scheduletask;

import java.util.Map;

/**
 * User: kyle
 * Date: 13-1-8
 * Time: 下午2:41
 */
public abstract class ScheduleTaskTemplate implements ScheduleTask {

    public void beforeTrigger(Map<String, String> taskParams) {
    }

    public abstract Map<String, String> doTrigger(Map<String, String> taskParams);

    public void afterTrigger(Map<String, String> taskParams) {
    }

    @Override
    public Map<String, String> execute(Map<String, String> taskParams) {
        long begin = System.currentTimeMillis();
        this.beforeTrigger(taskParams);
        Map<String, String> returnValue = this.doTrigger(taskParams);
        this.afterTrigger(taskParams);
        long costTime = System.currentTimeMillis() - begin;
        returnValue.put("cost", String.valueOf(costTime));
        return returnValue;
    }
}
