package com.lzx.frame.core.advice.encryptbody.bean;

import com.lzx.frame.common.enums.encrypt.EncryptBodyMethod;
import com.lzx.frame.common.enums.encrypt.SHAEncryptType;

/**
 * <p>加密注解信息</p>
 */
public class EncryptAnnotationInfoBean {

    /**
     * key
     */
    private String key;

    /**
     * 加密方法
     */
    private EncryptBodyMethod encryptBodyMethod;

    /**
     * 加密类型
     */
    private SHAEncryptType shaEncryptType;

    private EncryptAnnotationInfoBean(Builder builder) {
        this.key = builder.key;
        this.encryptBodyMethod = builder.encryptBodyMethod;
        this.shaEncryptType = builder.shaEncryptType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public EncryptBodyMethod getEncryptBodyMethod() {
        return encryptBodyMethod;
    }

    public void setEncryptBodyMethod(EncryptBodyMethod encryptBodyMethod) {
        this.encryptBodyMethod = encryptBodyMethod;
    }

    public SHAEncryptType getShaEncryptType() {
        return shaEncryptType;
    }

    public void setShaEncryptType(SHAEncryptType shaEncryptType) {
        this.shaEncryptType = shaEncryptType;
    }

    public static class Builder {
        /**
         * key
         */
        private String key;

        /**
         * 加密方法
         */
        private EncryptBodyMethod encryptBodyMethod;

        /**
         * 加密类型
         */
        private SHAEncryptType shaEncryptType;

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder encryptBodyMethod(EncryptBodyMethod encryptBodyMethod) {
            this.encryptBodyMethod = encryptBodyMethod;
            return this;
        }

        public Builder shaEncryptType(SHAEncryptType shaEncryptType) {
            this.shaEncryptType = shaEncryptType;
            return this;
        }

        public EncryptAnnotationInfoBean build() {
            return new EncryptAnnotationInfoBean(this);
        }
    }
}
