package com.kariqu.session;

import javax.servlet.http.Cookie;

/**
 * cookie implement of Cookie
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-5-17 下午6:48
 */
public class KariquCookie extends Cookie {

    /**
     * httpOnly is used to prevent the client script(js...) to read the cookies.
     * since servlet 3.0 spcification,the httponly has been supported internal,
     * the purpose here is mainly compatilabilty with pre servlet 3.0
     */
    private boolean httpOnly;

    /**
     * Constructs a cookie with a specified name and value.
     * <p/>
     * <p>The name must conform to RFC 2109. That means it can contain
     * only ASCII alphanumeric characters and cannot contain commas,
     * semicolons, or white space or begin with a $ character. The cookie's
     * name cannot be changed after creation.
     * <p/>
     * <p>The value can be anything the server chooses to send. Its
     * value is probably of interest only to the server. The cookie's
     * value can be changed after creation with the
     * <code>setValue</code> method.
     * <p/>
     * <p>By default, cookies are created according to the Netscape
     * cookie specification. The version can be changed with the
     * <code>setVersion</code> method.
     *
     * @param name  a <code>String</code> specifying the name of the cookie
     * @param value a <code>String</code> specifying the value of the cookie
     * @throws IllegalArgumentException if the cookie name contains illegal characters
     *                                  (for example, a comma, space, or semicolon) or it is one of the tokens reserved for use
     *                                  by the cookie protocol
     * @see #setValue
     * @see #setVersion
     */
    public KariquCookie(String name, String value) {
        super(name, value);
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }
}
