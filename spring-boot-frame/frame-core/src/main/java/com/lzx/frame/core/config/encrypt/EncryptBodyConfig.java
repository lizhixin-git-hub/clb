package com.lzx.frame.core.config.encrypt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

/**
 * <p>加密数据配置读取类</p>
 * <p>在SpringBoot项目中的application.yml中添加配置信息即可</p>
 * <pre>
 *     encrypt:
 *      body:
 *       aes-key: 12345678 # AES加密秘钥
 *       des-key: 12345678 # DES加密秘钥
 * </pre>
 */
@Configuration
@ConfigurationProperties(prefix = "encrypt.body")
public class EncryptBodyConfig {

    private String aesKey;

    private String desKey;

    public static String RSA_KEY;

    private String encoding = StandardCharsets.UTF_8.name();

    private String rsaPirKey;

    private String rsaPubKey;

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public String getDesKey() {
        return desKey;
    }

    public void setDesKey(String desKey) {
        this.desKey = desKey;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getRsaPirKey() {
        return rsaPirKey;
    }

    public void setRsaPirKey(String rsaPirKey) {
        this.rsaPirKey = rsaPirKey;
    }

    public String getRsaPubKey() {
        return rsaPubKey;
    }

    public void setRsaPubKey(String rsaPubKey) {
        this.rsaPubKey = rsaPubKey;
    }

}
