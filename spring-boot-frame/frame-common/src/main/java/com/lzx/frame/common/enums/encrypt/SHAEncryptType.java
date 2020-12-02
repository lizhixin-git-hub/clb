package com.lzx.frame.common.enums.encrypt;

/**
 * <p>SHA加密类型</p>
 */
public enum SHAEncryptType {

    /**
     * SHA224
     */
    SHA224("sha-224"),

    /**
     * SHA256
     */
    SHA256("sha-256"),

    /**
     * SHA384
     */
    SHA384("sha-384"),

    /**
     * SHA512
     */
    SHA512("sha-512");

    public String value;

    SHAEncryptType(String value) {
        this.value = value;
    }

}
