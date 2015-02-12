package com.kariqu.common.log4j;

import org.apache.log4j.MDC;
/**
 * Log4j MDC 包装器
 *
 * @author Tiger
 * @version 1.0
 * @since 12-4-27 下午4:42
 */
public class Log4jMDC {
    
    public static final String LOG4J_MDC_REQUEST_URI = "requestURIWithQueryString";

    public static final String USER_INFO = "userInfo";
    
    public static final String LOG4J_MDC_USER_ID = "userId";

    public static void put(String name, String value) {
        MDC.put(name,value);
    }

    public static void remove(String name) {
        MDC.remove(name);
    }
}
