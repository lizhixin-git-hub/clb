package com.lzx.frame.common.enums.encrypt;

/**
 * <p>解密方式</p>
 */
public enum DecryptBodyMethod {

    /**
     * 选择DES解密方式
     */
    DES,

    /**
     * 选择AES解密方式
     */
    AES,

    /**
     * 选择RSA解密方式
     */
    RSA;

    /**
     * 超时 时间为 60秒
     */
    public static final long TIME_OUT = 60L * 1000L;

}
