package com.kariqu.session;

/**
 * 只读异常
 * @author Tiger
 * @version 1.0.0
 * @since 11-5-19 下午2:22
 */
public class ModifyReadOnlyAttributeException extends RuntimeException {
    public ModifyReadOnlyAttributeException() {
    }

    public ModifyReadOnlyAttributeException(String message) {
        super(message);
    }

    public ModifyReadOnlyAttributeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModifyReadOnlyAttributeException(Throwable cause) {
        super(cause);
    }
}
