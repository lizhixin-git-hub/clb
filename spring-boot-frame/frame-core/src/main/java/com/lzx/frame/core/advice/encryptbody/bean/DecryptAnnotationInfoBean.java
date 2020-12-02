package com.lzx.frame.core.advice.encryptbody.bean;

import com.lzx.frame.common.enums.encrypt.DecryptBodyMethod;

/**
 * <p>解密注解信息</p>
 */
public class DecryptAnnotationInfoBean {

    /**
     * key
     */
    private String key;

    /**
     * 加密方式
     */
    private DecryptBodyMethod decryptBodyMethod;

    /**
     * 数据超时时间
     */
    private long timeOut;

    private DecryptAnnotationInfoBean(Builder builder) {
        this.key = builder.key;
        this.decryptBodyMethod = builder.decryptBodyMethod;
        this.timeOut = builder.timeOut;
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

    public DecryptBodyMethod getDecryptBodyMethod() {
        return decryptBodyMethod;
    }

    public void setDecryptBodyMethod(DecryptBodyMethod decryptBodyMethod) {
        this.decryptBodyMethod = decryptBodyMethod;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public static class Builder {
        /**
         * key
         */
        private String key;

        /**
         * 加密方式
         */
        private DecryptBodyMethod decryptBodyMethod;

        /**
         * 数据超时时间
         */
        private long timeOut;

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder decryptBodyMethod(DecryptBodyMethod decryptBodyMethod) {
            this.decryptBodyMethod = decryptBodyMethod;
            return this;
        }

        public Builder timeOut(long timeOut) {
            this.timeOut = timeOut;
            return this;
        }

        public DecryptAnnotationInfoBean build() {
            return new DecryptAnnotationInfoBean(this);
        }
    }
}
