package com.kariqu.common.iptools;

import com.kariqu.common.iptools.ip.IPLocation;
import com.kariqu.common.iptools.ip.IPSeeker;

import javax.servlet.http.HttpServletRequest;

/**
 * IP工具
 * User: Alec
 * Date: 12-11-29
 * Time: 下午1:56
 * To change this template use File | Settings | File Templates.
 */
public class IpTools {

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 使用方法：
     *  地址：ipLocation.getCountry();
     *  运营商：ipLocation.getArea()；
     * @param ip
     * @return
     */
    public static IPLocation getAddressFromIp(String ip) {
        return new IPSeeker().getIPLocation(ip);
    }



}
