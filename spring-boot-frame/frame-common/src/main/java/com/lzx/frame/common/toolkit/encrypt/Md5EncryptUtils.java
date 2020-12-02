package com.lzx.frame.common.toolkit.encrypt;

import com.lzx.frame.common.constant.StringPool;

import java.security.MessageDigest;

/**
 * <p>MD5加密工具类</p>
 */
public class Md5EncryptUtils {

    private Md5EncryptUtils() {

    }

    /**
     * MD5加密-32位小写
     */
    public static String encrypt(String encryptStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(encryptStr.getBytes());
            StringBuilder hexValue = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                int val = ((int) md5Byte) & 0xff;
                if (val < 16) {
                    hexValue.append(StringPool.ZERO);
                }
                hexValue.append(Integer.toHexString(val));
            }
            encryptStr = hexValue.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptStr;
    }

}
