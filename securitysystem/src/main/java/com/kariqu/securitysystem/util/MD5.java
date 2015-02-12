package com.kariqu.securitysystem.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5摘要算法工具
 * User: Asion
 * Date: 11-5-23
 * Time: 下午8:49
 */
public final class MD5 {

    public static String getHashString(String src) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(src.getBytes());
            return toHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("不能生成MD5摘要", e);
        }
    }

    private static String toHex(byte[] buffer) {
        StringBuilder sb = new StringBuilder(buffer.length * 2);
        for (byte b : buffer) {
            sb.append(Character.forDigit((b & 0xf0) >> 4, 16));
            sb.append(Character.forDigit(b & 0x0f, 16));
        }
        return sb.toString();
    }
}
