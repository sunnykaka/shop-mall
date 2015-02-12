package com.kariqu.searchengine.exception;

/**
 * User: Asion
 * Date: 12-4-28
 * Time: 下午1:47
 */
public class SearchException extends RuntimeException {

    public SearchException() {
        super();
    }

    public SearchException(String message) {
        super(message);
    }

    public SearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public SearchException(Throwable cause) {
        super(cause);
    }
}
