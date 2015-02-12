package com.kariqu.shopsystem.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.http.DataPush;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 同步公共模块
 * User: Asion
 * Date: 12-8-5
 * Time: 下午2:38
 */
@Controller
public class CommonModuleDataPushController {

    private final Log logger = LogFactory.getLog(CommonModuleDataPushController.class);

    private String pushUrl;

    /** 同步系统模块 */
    @RequestMapping(value = "/page/module/common/{id}/push", method = RequestMethod.POST)
    public void pushCommonModule(String type, @PathVariable("id") int id, HttpServletResponse response) throws IOException {
        DataPush dataPush = new DataPush();
        StringBuilder result = new StringBuilder();
        Map<String, String> map = new HashMap<String, String>();
        map.put("type", type);
        map.put("id", String.valueOf(id));

        for (String ip : pushUrl.split(",")) {
            String url = "http://" + ip.trim() + "/syncDc";
            try {
                dataPush.push(url, map);
                result.append(url).append(" 同步成功<br/>");
            } catch (IOException e) {
                logger.error("向[" + url + "]同步模块的时候出错: " + e.getMessage());
                result.append(url).append(" 同步失败<br/>");
            }
        }
        new JsonResult(true, result.toString()).toJson(response);
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

}
