package com.kariqu.common.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author: Pablo Fernandez
 */
public class Utf8UrlEncoder {

    private static String CHARSET = "UTF-8";

    public static String encode(String plain) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(plain, CHARSET);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Charset not found while encoding string: " + CHARSET, uee);
        }
        return encoded;
    }


    public static String decode(String encoded) {
        try {
            return URLDecoder.decode(encoded, CHARSET);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Charset not found while decoding string: " + CHARSET, uee);
        }
    }

}
