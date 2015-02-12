package com.kariqu.session.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Blowfish加解密的方法
 *
 * @author Tiger
 * @version 1.0.0
 * @link http://www.schneier.com/blowfish.html
 */
public class BlowfishUtils {

    private static final Logger logger = Logger.getLogger(BlowfishUtils.class);

    private static final String CIPHER_NAME = "Blowfish/CFB8/NoPadding";

    private static final String KEY_SPEC_NAME = "Blowfish";

    private static final ThreadLocal<BlowfishUtils> pool = new ThreadLocal<BlowfishUtils>();

    private Cipher enCipher;

    private Cipher deCipher;

    private String key;

    private BlowfishUtils(String key) {
        try {
            this.key = key;

            String iv = StringUtils.substring(DigestUtils.md5Hex(key), 0, 8);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), KEY_SPEC_NAME);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());

            enCipher = Cipher.getInstance(CIPHER_NAME);
            deCipher = Cipher.getInstance(CIPHER_NAME);
            enCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            deCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        } catch (Exception e) {
            logger.error("初始化BlowfishUtils失败", e);
        }
    }

    public static String encryptBlowfish(String s, String key) {
        return getInstance(key).encrypt(s);
    }

    public static String decryptBlowfish(String s, String key) {
        return getInstance(key).decrypt(s);
    }

    private static BlowfishUtils getInstance(String key) {
        BlowfishUtils instance = (BlowfishUtils) pool.get();

        if (instance == null || !StringUtils.equals(instance.key, key)) {
            instance = new BlowfishUtils(key);
            pool.set(instance);
        }

        return instance;
    }

    private void resetInstance() {
        pool.set(null);
    }

    /**
     * 加密方法
     *
     * @param s
     * @return
     */
    private String encrypt(String s) {
        String result = null;

        if (StringUtils.isNotBlank(s)) {
            try {
                byte[] encrypted = enCipher.doFinal(s.getBytes());
                result = new String(Base64.encodeBase64(encrypted));
            } catch (Exception e) {
                resetInstance(); // 抛弃当前对象，防止enCipher出现中间状态
                logger.warn("加密失败", e);
            }
        }

        return result;
    }

    /**
     * 解密的方法
     *
     * @param s
     * @return
     */
    private String decrypt(String s) {
        String result = null;

        if (StringUtils.isNotBlank(s)) {
            try {
                byte[] decrypted = Base64.decodeBase64(s.getBytes());
                result = new String(deCipher.doFinal(decrypted));
            } catch (Exception e) {
                resetInstance(); // 抛弃当前对象，防止deCipher出现中间状态
                logger.warn("解密失败", e);
            }
        }

        return result;
    }

    public static void remove() {
        pool.set(null);
    }

    public static void main(String[] args) {
        final String tiger = BlowfishUtils.encryptBlowfish("session-key", "tiger");
        System.out.println(tiger);
        System.out.println(BlowfishUtils.decryptBlowfish(tiger,"tiger"));
    }

}
