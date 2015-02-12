package com.kariqu.session;

/**
 * session配置异常
 * @author Tiger
 * @version 1.0.0
 * @since 11-6-14 下午6:38
 */
public class SessionConfigParseException extends RuntimeException {
    public SessionConfigParseException() {
    }

    public SessionConfigParseException(String message) {
        super(message);
    }

    public SessionConfigParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionConfigParseException(Throwable cause) {
        super(cause);
    }
}
