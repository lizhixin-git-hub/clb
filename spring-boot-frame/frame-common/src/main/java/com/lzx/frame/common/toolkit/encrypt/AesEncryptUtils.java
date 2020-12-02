package com.lzx.frame.common.toolkit.encrypt;

import com.lzx.frame.common.toolkit.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * <p>AES加密处理工具类</p>
 */
public class AesEncryptUtils {

    private AesEncryptUtils() {
        throw new IllegalStateException("这是一个公共类不需要实例化");
    }

    /**
     * AES加密
     *
     * @param content  字符串内容
     * @param password 密钥
     */
    public static String encrypt(String content, String password) {
        return aes(content, password, Cipher.ENCRYPT_MODE);
    }


    /**
     * AES解密
     *
     * @param content  字符串内容
     * @param password 密钥
     */
    public static String decrypt(String content, String password) {
        return aes(content, password, Cipher.DECRYPT_MODE);
    }

    /**
     * AES加密/解密 公共方法
     *
     * @param content  字符串
     * @param password 密钥
     * @param type     加密：{@link Cipher#ENCRYPT_MODE}，解密：{@link Cipher#DECRYPT_MODE}
     */
    private static String aes(String content, String password, int type) {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            generator.init(128, random);
            SecretKey secretKey = generator.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(type, key);
            if (type == Cipher.ENCRYPT_MODE) {
                byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
                return Hex2Utils.parseByte2HexStr(cipher.doFinal(byteContent));
            } else {
                byte[] byteContent = Hex2Utils.parseHexStr2Byte(content);
                return !StringUtils.checkValNotNull(byteContent) ? null : new String(cipher.doFinal(byteContent));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
