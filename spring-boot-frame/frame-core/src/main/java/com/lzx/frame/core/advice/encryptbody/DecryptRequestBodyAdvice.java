package com.lzx.frame.core.advice.encryptbody;

import com.alibaba.fastjson.TypeReference;
import com.lzx.frame.common.annotation.decrypt.AESDecryptBody;
import com.lzx.frame.common.annotation.decrypt.DESDecryptBody;
import com.lzx.frame.common.annotation.decrypt.DecryptBody;
import com.lzx.frame.common.annotation.decrypt.RSADecryptBody;
import com.lzx.frame.common.constant.BooleanPool;
import com.lzx.frame.common.constant.StringPool;
import com.lzx.frame.common.enums.encrypt.DecryptBodyMethod;
import com.lzx.frame.common.toolkit.*;
import com.lzx.frame.common.toolkit.encrypt.AesEncryptUtils;
import com.lzx.frame.common.toolkit.encrypt.DesEncryptUtils;
import com.lzx.frame.common.toolkit.encrypt.RsaEncryptUtils;
import com.lzx.frame.core.advice.encryptbody.bean.DecryptAnnotationInfoBean;
import com.lzx.frame.core.advice.encryptbody.bean.DecryptHttpInputMessage;
import com.lzx.frame.core.config.encrypt.EncryptBodyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;


import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 请求数据解密处理
 * 本类只对控制器参数中含有<strong>{@link //org.springframework.web.bind.annotation.RequestBody}</strong>
 * 以及package为<strong><code>com.lzx.frame.common.annotation.decrypt</code></strong>下的注解有效
 *
 * @see RequestBodyAdvice
 */
@Order(1)
@RestControllerAdvice
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecryptRequestBodyAdvice.class);

    private EncryptBodyConfig config;

    @Autowired
    public void setConfig(EncryptBodyConfig config) {
        this.config = config;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        Annotation[] annotations = methodParameter.getDeclaringClass().getAnnotations();

        if (ArrayUtils.isNotEmpty(annotations)) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof DecryptBody ||
                        annotation instanceof AESDecryptBody ||
                        annotation instanceof DESDecryptBody ||
                        annotation instanceof RSADecryptBody) {
                    return true;
                }
            }
        }

        return Optional.ofNullable(methodParameter.getMethod()).map(method ->
                method.isAnnotationPresent(DecryptBody.class) ||
                        method.isAnnotationPresent(AESDecryptBody.class) ||
                        method.isAnnotationPresent(DESDecryptBody.class) ||
                        method.isAnnotationPresent(RSADecryptBody.class))
                .orElse(BooleanPool.FALSE);
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (!Optional.ofNullable(inputMessage).map(x -> {
            try {
                return x.getBody();
            } catch (IOException e) {
                LOGGER.error("数据解密初始化异常,时间:{}", new Date());
                return null;
            }
        }).isPresent()) {
            return inputMessage;
        }

        Map<String, Object> req;
        String body;
        try {
            body = IOUtils.toString(inputMessage.getBody(), config.getEncoding());
            req = JsonUtils.jsonToObject(body, new TypeReference<Map<String, Object>>() {
            });
            body = (String) Optional.ofNullable(req).map(dataSecret -> req.get("dataSecret")).orElse(null);
        } catch (Exception e) {
            throw ExceptionUtils.mpe(StringUtils.join(StringPool.EMPTY, "Unable to get request body data,",
                    " please check if the sending data body or request method is in compliance with the specification.",
                    " (无法获取请求正文数据，请检查发送数据体或请求方法是否符合规范。)"));
        }

        if (!StringUtils.checkValNotNull(body)) {
            throw ExceptionUtils.mpe(StringUtils.join(StringPool.EMPTY, "The request body is NULL or an empty string, so the decryption failed.",
                    " (请求正文为NULL或为空字符串，因此解密失败。)"));
        }

        String decryptBody = null;
        //获取方法注解 执行顺序 方法 ->类
        DecryptAnnotationInfoBean methodAnnotation = this.getMethodAnnotation(parameter);
        if (StringUtils.checkValNotNull(methodAnnotation)) {
            decryptBody = switchDecrypt(body, methodAnnotation);
        } else {
            //获取方法注解 执行顺序 方法 ->类
            DecryptAnnotationInfoBean classAnnotation = this.getClassAnnotation(parameter.getDeclaringClass());
            if (!StringUtils.checkValNotNull(classAnnotation)) {
                decryptBody = switchDecrypt(body, classAnnotation);
            }
        }

        if (!StringUtils.checkValNotNull(decryptBody)) {
            throw ExceptionUtils.mpe(StringUtils.join(StringPool.EMPTY, "Decryption error, ",
                    "please check if the selected source data is encrypted correctly.",
                    " (解密错误，请检查选择的源数据的加密方式是否正确。)"));
        }

        try {
            InputStream inputStream = IOUtils.toInputStream(decryptBody, config.getEncoding());
            return new DecryptHttpInputMessage(inputStream, inputMessage.getHeaders());
        } catch (Exception e) {
            throw ExceptionUtils.mpe(StringUtils.join(StringPool.EMPTY, "The string is converted to a stream format exception.",
                    " Please check if the format such as encoding is correct.",
                    " (字符串转换成流格式异常，请检查编码等格式是否正确。)"));
        }

    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    /**
     * 获取方法控制器上的加密注解信息
     *
     * @param methodParameter 控制器方法
     * @return 加密注解信息
     */
    private DecryptAnnotationInfoBean getMethodAnnotation(MethodParameter methodParameter) {
        return Optional.ofNullable(methodParameter.getMethod()).map(method -> {
            if (method.isAnnotationPresent(DecryptBody.class)) {
                return Optional.ofNullable(methodParameter.getMethodAnnotation(DecryptBody.class))
                        .map(decryptBody -> DecryptAnnotationInfoBean.builder()
                                .decryptBodyMethod(decryptBody.value())
                                .key(decryptBody.otherKey())
                                .timeOut(decryptBody.timeOut())
                                .build()).orElse(null);
            }

            if (method.isAnnotationPresent(DESDecryptBody.class)) {
                return Optional.ofNullable(methodParameter.getMethodAnnotation(DESDecryptBody.class))
                        .map(des -> DecryptAnnotationInfoBean.builder()
                                .decryptBodyMethod(DecryptBodyMethod.DES)
                                .key(des.otherKey())
                                .timeOut(des.timeOut())
                                .build()).orElse(null);
            }

            if (method.isAnnotationPresent(AESDecryptBody.class)) {
                return Optional.ofNullable(methodParameter.getMethodAnnotation(AESDecryptBody.class))
                        .map(aes -> DecryptAnnotationInfoBean.builder()
                                .decryptBodyMethod(DecryptBodyMethod.AES)
                                .key(aes.otherKey())
                                .timeOut(aes.timeOut())
                                .build()).orElse(null);
            }

            if (method.isAnnotationPresent(RSADecryptBody.class)) {
                return Optional.ofNullable(methodParameter.getMethodAnnotation(RSADecryptBody.class))
                        .map(rsa -> DecryptAnnotationInfoBean.builder()
                                .decryptBodyMethod(DecryptBodyMethod.RSA)
                                .key(rsa.otherKey())
                                .timeOut(rsa.timeOut())
                                .build()).orElse(null);
            }

            return null;
        }).orElse(null);
    }

    /**
     * 获取类控制器上的加密注解信息
     *
     * @param clazz 控制器类
     * @return 加密注解信息
     */
    private DecryptAnnotationInfoBean getClassAnnotation(Class clazz) {
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        return Optional.ofNullable(annotations)
                .map(x -> {
                            for (Annotation annotation : x) {
                                if (annotation instanceof DecryptBody) {
                                    DecryptBody decryptBody = (DecryptBody) annotation;
                                    return DecryptAnnotationInfoBean.builder()
                                            .decryptBodyMethod(decryptBody.value())
                                            .key(decryptBody.otherKey())
                                            .build();
                                }
                                if (annotation instanceof DESDecryptBody) {
                                    return DecryptAnnotationInfoBean.builder()
                                            .decryptBodyMethod(DecryptBodyMethod.DES)
                                            .key(((DESDecryptBody) annotation).otherKey())
                                            .build();
                                }
                                if (annotation instanceof AESDecryptBody) {
                                    return DecryptAnnotationInfoBean.builder()
                                            .decryptBodyMethod(DecryptBodyMethod.AES)
                                            .key(((AESDecryptBody) annotation).otherKey())
                                            .build();
                                }
                                if (annotation instanceof RSADecryptBody) {
                                    return DecryptAnnotationInfoBean.builder()
                                            .decryptBodyMethod(DecryptBodyMethod.RSA)
                                            .key(((RSADecryptBody) annotation).otherKey())
                                            .build();
                                }
                            }

                            return null;
                        }
                )
                .orElse(null);
    }

    /**
     * 选择加密方式并进行解密
     *
     * @param formatStringBody 目标解密字符串
     * @param infoBean         加密信息
     * @return 解密结果
     */
    private String switchDecrypt(String formatStringBody, DecryptAnnotationInfoBean infoBean) {
        DecryptBodyMethod method = infoBean.getDecryptBodyMethod();

        method = Optional.ofNullable(method).orElseThrow(() -> ExceptionUtils.mpe("解密方式未定义"));

        String key = infoBean.getKey();
        String decodeData;
        if (method == DecryptBodyMethod.DES) {
            key = CheckUtils.checkAndGetKey(config.getDesKey(), key, "DES-KEY");
            decodeData = DesEncryptUtils.decrypt(formatStringBody, key);
        } else if (method == DecryptBodyMethod.AES) {
            key = CheckUtils.checkAndGetKey(config.getAesKey(), key, "AES-KEY");
            decodeData = AesEncryptUtils.decrypt(formatStringBody, key);
        } else if (method == DecryptBodyMethod.RSA) {
            key = CheckUtils.checkAndGetKey(config.getRsaPirKey(), key, "RSA-KEY");
            decodeData = RsaEncryptUtils.decrypt(formatStringBody, key);
        } else {
            LOGGER.error("解密方式未定义,不知道你是aes/ecs/rsa");
            throw ExceptionUtils.mpe("解密方式未定义,不知道你是aes/ecs/rsa");
        }

        long timestamp = Optional.ofNullable(decodeData)
                .map(x -> JsonUtils.jsonToObject(x, new TypeReference<LinkedHashMap<String, Object>>() {
                }))
                .map(x -> x.get("timestamp"))
                .map(x -> {
                    if (!NumberUtils.isCreatable(x.toString())) {
                        throw ExceptionUtils.mpe("数据加密timestamp必须为时间戳");
                    }
                    return Long.parseLong(x.toString());
                }).orElseThrow(() -> ExceptionUtils.mpe("数据加密timestamp不能为空"));

        //验证数据是否过期timestamp
        verifyTime(timestamp, infoBean.getTimeOut(), decodeData);

        return decodeData;
    }

    /**
     * 判断数据是否超时
     *
     * @param timestamp 发送来的时间戳
     * @param timeOut   过期时间
     * @param body      请求体
     */
    private void verifyTime(long timestamp, long timeOut, String body) {
        //当前时间
        long nowTime = System.currentTimeMillis();

        //判断数据加密时间 时间戳+过期时间如果小于当前时间踢飞
        if (timestamp + timeOut < nowTime) {
            LOGGER.error("时间戳:{},时间戳+过期时间:{},当前时间:{},时间差:{},数据为:{}"
                    , timestamp
                    , timestamp + timeOut
                    , nowTime
                    , timestamp + timeOut - nowTime
                    , body);
            throw ExceptionUtils.mpe("过期数据请勿重复提交");
        }
    }

}
