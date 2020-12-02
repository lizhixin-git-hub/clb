package com.lzx.frame.common.toolkit.encrypt;

import com.lzx.frame.common.constant.StringPool;
import com.lzx.frame.common.enums.encrypt.SHAEncryptType;
import com.lzx.frame.common.toolkit.NumberUtils;
import com.lzx.frame.common.toolkit.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * <p>SHA加密工具类</p>
 */
public class ShaEncryptUtils {

    private ShaEncryptUtils() {

    }

    /**
     * SHA加密公共方法
     *
     * @param string 目标字符串
     * @param type   加密类型 {@link SHAEncryptType}
     */
    public static String encrypt(String string, SHAEncryptType type) {
        if (!StringUtils.checkValNotNull(string)) {
            return StringPool.EMPTY;
        }
        if (!StringUtils.checkValNotNull(type)) {
            type = SHAEncryptType.SHA256;
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance(type.value);
            byte[] bytes = md5.digest((string).getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (Objects.equals(StringUtils.length(temp), NumberUtils.INTEGER_ZERO)) {
                    temp = StringUtils.join(StringPool.EMPTY, StringPool.ZERO, temp);
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return StringPool.EMPTY;
    }

}
