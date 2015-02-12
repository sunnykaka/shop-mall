package com.kariqu.scheduletask;

import java.util.Map;

/**
 * User: kyle
 * Date: 13-1-8
 * Time: 下午1:46
 */
public interface ScheduleTask {

    Map<String,String>  execute(Map<String, String> taskParams);

}
