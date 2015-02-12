package com.kariqu.categorycenter.domain.util;

import com.kariqu.common.json.JsonUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * User: Asion
 * Date: 11-9-19
 * Time: 上午11:55
 */
public class PidVidJsonUtil {

    private static final Log logger = LogFactory.getLog(PidVidJsonUtil.class);

    /**
     * 将pidvid对象转为json字符串
     *
     * @param pidVid
     * @return
     */
    public static String toJson(PidVid pidVid) {
        return JsonUtil.objectToJson(pidVid);
    }

    /**
     * 将Json还原为pidvid对象
     *
     * @param json
     * @return
     */
    public static PidVid restore(String json) {
        ObjectMapper mapper = new ObjectMapper();
        PidVid pidVid;
        try {
            pidVid = mapper.readValue(json, PidVid.class);
        } catch (IOException e) {
            logger.error("不能解析PidVid Json字符串", e);
            throw new RuntimeException("不能解析PidVid JSON字符串", e);
        }
        return pidVid;
    }


}
