package com.kariqu.session;

/**
 * when setting the unsupport info to session,this exception will be throwed.
 * so the session info must be configured in the session-config.xml file first.
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-5-19 下午2:18
 */
public class UnSupportNameException extends RuntimeException {

    public UnSupportNameException() {

    }

    public UnSupportNameException(String message) {
        super(message);
    }

    public UnSupportNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnSupportNameException(Throwable cause) {
        super(cause);
    }
}
