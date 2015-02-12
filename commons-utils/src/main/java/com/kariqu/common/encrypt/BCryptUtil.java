package com.kariqu.common.encrypt;

import org.apache.log4j.Logger;

/**
 * 加密工具类
 * User: ennoch
 * Date: 12-5-3
 * Time: 下午1:28
 */
public class BCryptUtil {

    private static final Logger LOGGER = Logger.getLogger(BCryptUtil.class);

    public static String encryptPassword(String password) {
        // 撒盐
        String salt = BCrypt.gensalt();
        return BCrypt.hashpsd(password, salt);
    }

    public static boolean check(String password, String hashedPassword) {
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
            LOGGER.error("检查密码时异常(" + password + ", " + hashedPassword + "):", e);
            return false;
        }
    }
}
