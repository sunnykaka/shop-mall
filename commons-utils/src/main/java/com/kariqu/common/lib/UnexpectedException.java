package com.kariqu.common.lib;

/**
 * Created by Canal.wen on 2014/6/27 14:34.
 */
public class UnexpectedException extends RuntimeException {
    public UnexpectedException(String message) {
        super(message);
    }

    public UnexpectedException(Throwable exception) {
        super("Unexpected Error", exception);
    }

    public UnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorTitle() {
        if(getCause() == null) {
            return "Unexpected error";
        }
        return String.format("Oops: %s", getCause().getClass().getSimpleName());
    }

    public String getErrorDescription() {
        if(getCause() != null && getCause().getClass() != null)
            return String.format("An unexpected error occured caused by exception <strong>%s</strong>:<br/> <strong>%s</strong>", getCause().getClass().getSimpleName(), getCause().getMessage());
        else return String.format("Unexpected error : %s", getMessage());
    }
}
