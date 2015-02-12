package com.kariqu.buyer.web.common;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 购物车跟踪ID工具
 * User: Asion
 * Date: 13-4-9
 * Time: 上午10:30
 */
public class CartTrackUtil {

    public static String readOrWriteTrackId(HttpServletRequest request) {
        String trackId = getTrackId(request);
        return StringUtils.isEmpty(trackId) ? writeTrackId(request) : trackId;
    }

    public static String getTrackId(HttpServletRequest request) {
        return (String) request.getSession().getAttribute("trackId");
    }

    private static String writeTrackId(HttpServletRequest request) {
        String cookieValue = UUID.randomUUID().toString();
        request.getSession().setAttribute("trackId", cookieValue);
        return cookieValue;
    }

    public static void deleteTrackId(HttpServletRequest request) {
        // ignore
        //request.getSession().removeAttribute("cartTrack");
    }
}
