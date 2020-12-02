package com.lzx.frame.common.toolkit.encrypt;

import com.lzx.frame.common.toolkit.ExceptionUtils;
import com.lzx.frame.common.toolkit.NumberUtils;
import com.lzx.frame.common.toolkit.StringUtils;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <p>RSA加密处理工具类</p>
 */
public class RsaEncryptUtils {

    private RsaEncryptUtils() {

    }

    /**
     * 非对称密钥算法
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 非对称密钥算法
     */
    public static final byte[] DEF_RSA_KEY = "frameApplicationRSAKey".getBytes();

    /**
     * 密钥长度，DH算法的默认密钥长度是1024
     * 密钥长度必须是64的倍数，在512到16384位之间
     */
    private static final int KEY_SIZE = 2048;

    /**
     * 加密最大数据
     */
    private static final int DATA_MAX_SIZE = (KEY_SIZE / 8) - 11;

    /**
     * 公钥
     */
    public static final String PUBLIC_KEY = "pub";

    /**
     * 私钥
     */
    public static final String PRIVATE_KEY = "pri";


    /**
     * RSA解密 私钥解密
     *
     * @param content  字符串内容
     * @param password 密钥
     */
    public static String decrypt(String content, String password) {
        return Optional.ofNullable(decryptByPrivateKey(Base64.getDecoder().decode(content), Base64.getDecoder().decode(password)))
                .map(String::new)
                .orElse(null);
    }

    /**
     * RSA加密  私钥加密
     *
     * @param content  字符串内容
     * @param password 密钥
     */
    public static String encrypt(String content, String password) {
        return Optional.ofNullable(encryptByPrivateKey(content.getBytes(), Base64.getDecoder().decode(password)))
                .map(Base64.getEncoder()::encodeToString)
                .orElse(null);
    }


    /**
     * 初始化密钥对
     *
     * @return Map 密钥初始化
     */
    public static Map<String, Object> initKey(String resKey) {
        //实例化密钥生成器
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionUtils.mpe("rsa初始化密钥对异常");
        }
        //初始化密钥生成器
        SecureRandom secureRandom = new SecureRandom(
                Optional.ofNullable(resKey)
                        .map(String::getBytes)
                        .orElse(DEF_RSA_KEY)
        );
        keyPairGenerator.initialize(KEY_SIZE, secureRandom);
        //生成密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //甲方公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        //甲方私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //将密钥存储在map中
        Map<String, Object> keyMap = new HashMap<>(NumberUtils.INTEGER_TWO);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 公钥加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] key) {
        Optional.ofNullable(data)
                .map(x -> x.length)
                .ifPresent(x -> {
                    if (x > DATA_MAX_SIZE) {
                        throw ExceptionUtils.mpe("私钥加密数据超过最大值:" + DATA_MAX_SIZE);
                    }
                });

        try {
            //实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            //初始化公钥
            //密钥材料转换
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
            //产生公钥
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

            //数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return !StringUtils.checkValNotNull(data) ? null : cipher.doFinal(data);
        } catch (Exception e) {
            throw ExceptionUtils.mpe("公钥加密出错");
        }
    }


    /**
     * 私钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPrivateKey(byte[] data, byte[] key) {

        try {
            //取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            //生成私钥
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            //数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw ExceptionUtils.mpe("私钥解密出错");
        }

    }

    /**
     * 私钥加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] key) {
        Optional.ofNullable(data)
                .map(x -> x.length)
                .ifPresent(x -> {
                    if (x > DATA_MAX_SIZE) {
                        throw ExceptionUtils.mpe("公钥加密数据超过最大值:" + DATA_MAX_SIZE);
                    }
                });

        try {
            //取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            //生成私钥
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            //数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return !StringUtils.checkValNotNull(data) ? null : cipher.doFinal(data);
        } catch (Exception e) {
            throw ExceptionUtils.mpe("私钥加密出错");
        }
    }


    /**
     * 公钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPublicKey(byte[] data, byte[] key) {
        try {
            //实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            //初始化公钥
            //密钥材料转换
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
            //产生公钥
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
            //数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, pubKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw ExceptionUtils.mpe("公钥解密出错");
        }
    }


    /**
     * 取得私钥
     *
     * @param keyMap 密钥map
     * @return byte[] 私钥
     */
    public static byte[] getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return key.getEncoded();
    }

    /**
     * 取得公钥
     *
     * @param keyMap 密钥map
     * @return byte[] 公钥
     */
    public static byte[] getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return key.getEncoded();
    }

}
