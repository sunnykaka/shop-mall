package com.kariqu.categorymanager.web;

import com.kariqu.categorycenter.client.sync.SyncType;
import com.kariqu.common.JsonResult;
import com.kariqu.common.http.DataPush;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** 类目推送控制器 */
@Controller
public class CategoryDataPushController {

    private final Log logger = LogFactory.getLog(CategoryDataPushController.class);
    
    private String pushUrl;

    /** 同步类目中心 */
    @RequestMapping(value = "/category/push", method = RequestMethod.POST)
    public void pushCategory(HttpServletResponse response) throws IOException {
        DataPush dataPush = new DataPush();
        StringBuilder result = new StringBuilder();
        Map<String, String> map = new HashMap<String, String>();
        map.put("type", SyncType.all.toString());

        for (String ip : pushUrl.split(",")) {
            String url = "http://" + ip.trim() + "/syncCc";
            try {
                dataPush.push(url, map);
                result.append(url).append(" 同步成功<br/>");
            } catch (Exception e) {
                logger.error("同步类目错误：" + e);
                result.append(url).append(" 同步失败<br/>");
            }
        }
        new JsonResult(true, result.toString()).toJson(response);
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }
    
}
