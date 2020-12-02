package com.lzx.frame.core.advice.encryptbody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzx.frame.common.annotation.decrypt.RSADecryptBody;
import com.lzx.frame.common.annotation.encrypt.*;
import com.lzx.frame.common.toolkit.ArrayUtils;
import com.lzx.frame.common.toolkit.CheckUtils;
import com.lzx.frame.common.toolkit.ExceptionUtils;
import com.lzx.frame.common.toolkit.StringUtils;
import com.lzx.frame.common.toolkit.encrypt.*;
import com.lzx.frame.common.enums.encrypt.EncryptBodyMethod;
import com.lzx.frame.common.enums.encrypt.SHAEncryptType;
import com.lzx.frame.core.config.encrypt.EncryptBodyConfig;
import com.lzx.frame.core.advice.encryptbody.bean.EncryptAnnotationInfoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;


/**
 * 响应数据的加密处理<br>
 * 本类只对控制器参数中含有<strong>{@link org.springframework.web.bind.annotation.ResponseBody}</strong>
 * 或者控制类上含有<strong>{@link org.springframework.web.bind.annotation.RestController}</strong>
 * 以及package为com.dtguai.app.annotation.encrypt.*下的注解有效
 */
@Order(1)
@RestControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptResponseBodyAdvice.class);

    private final ObjectMapper objectMapper;

    private final EncryptBodyConfig config;

    @Autowired
    public EncryptResponseBodyAdvice(ObjectMapper objectMapper, EncryptBodyConfig config) {
        this.objectMapper = objectMapper;
        this.config = config;
    }


    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        Annotation[] annotations = returnType.getDeclaringClass().getAnnotations();
        if (ArrayUtils.isNotEmpty(annotations)) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof EncryptBody ||
                        annotation instanceof AESEncryptBody ||
                        annotation instanceof DESEncryptBody ||
                        annotation instanceof RSAEncryptBody ||
                        annotation instanceof MD5EncryptBody ||
                        annotation instanceof SHAEncryptBody) {
                    return true;
                }
            }
        }

        return Optional.ofNullable(returnType.getMethod()).map(method ->
                method.isAnnotationPresent(EncryptBody.class) ||
                        method.isAnnotationPresent(AESEncryptBody.class) ||
                        method.isAnnotationPresent(DESEncryptBody.class) ||
                        method.isAnnotationPresent(RSAEncryptBody.class) ||
                        method.isAnnotationPresent(MD5EncryptBody.class) ||
                        method.isAnnotationPresent(SHAEncryptBody.class))
                .orElse(Boolean.FALSE);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (!StringUtils.checkValNotNull(body)) {
            return null;
        }

        response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
        String str;

        try {
            str = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            LOGGER.error("响应数据的JsonProcessingException异常,请联系管理员", e);
            throw ExceptionUtils.mpe("响应数据的JsonProcessingException异常,请联系管理员");
        }

        //获取方法注解 执行顺序 方法 ->类
        EncryptAnnotationInfoBean classAnnotation = getClassAnnotation(returnType.getDeclaringClass());
        if (StringUtils.checkValNotNull(classAnnotation) && StringUtils.checkValNotNull(str)) {
            return switchEncrypt(str, classAnnotation);
        }

        //获取类注解 执行顺序 方法 ->类
        EncryptAnnotationInfoBean methodAnnotation = getMethodAnnotation(returnType);
        if (StringUtils.checkValNotNull(methodAnnotation) && StringUtils.checkValNotNull(str)) {
            return switchEncrypt(str, methodAnnotation);
        }

        LOGGER.error("EncryptResponseBodyAdvice 加密数据失败 body:{}", body);
        throw ExceptionUtils.mpe("EncryptResponseBodyAdvice 加密数据失败 body:{}", body);
    }

    /**
     * 获取方法控制器上的加密注解信息
     *
     * @param methodParameter 控制器方法
     * @return 加密注解信息
     */
    private EncryptAnnotationInfoBean getMethodAnnotation(MethodParameter methodParameter) {
        Method method = Optional.ofNullable(methodParameter)
                .map(MethodParameter::getMethod)
                .orElseThrow(() -> {
                    LOGGER.error("获取方法控制器上的加密注解信息,为null--methodParameter:{}", methodParameter);
                    return ExceptionUtils.mpe("获取方法控制器上的加密注解信息,为null");
                });

        if (method.isAnnotationPresent(EncryptBody.class)) {
            return Optional.ofNullable(methodParameter.getMethodAnnotation(EncryptBody.class)).map(encryptBody ->
                    EncryptAnnotationInfoBean.builder()
                            .encryptBodyMethod(encryptBody.value())
                            .key(encryptBody.otherKey())
                            .shaEncryptType(encryptBody.shaType())
                            .build()).orElse(null);
        } else if (method.isAnnotationPresent(MD5EncryptBody.class)) {
            return EncryptAnnotationInfoBean.builder()
                    .encryptBodyMethod(EncryptBodyMethod.MD5)
                    .build();
        } else if (method.isAnnotationPresent(SHAEncryptBody.class)) {
            return Optional.ofNullable(methodParameter.getMethodAnnotation(SHAEncryptBody.class)).map(sha ->
                    EncryptAnnotationInfoBean.builder()
                            .encryptBodyMethod(EncryptBodyMethod.SHA)
                            .shaEncryptType(sha.value())
                            .build()).orElse(null);
        } else if (method.isAnnotationPresent(DESEncryptBody.class)) {
            return Optional.ofNullable(methodParameter.getMethodAnnotation(DESEncryptBody.class)).map(des ->
                    EncryptAnnotationInfoBean.builder()
                            .encryptBodyMethod(EncryptBodyMethod.DES)
                            .key(des.otherKey())
                            .build()).orElse(null);
        } else if (method.isAnnotationPresent(AESEncryptBody.class)) {
            return Optional.ofNullable(methodParameter.getMethodAnnotation(AESEncryptBody.class)).map(aes ->
                    EncryptAnnotationInfoBean.builder()
                            .encryptBodyMethod(EncryptBodyMethod.AES)
                            .key(aes.otherKey())
                            .build()).orElse(null);
        } else if (method.isAnnotationPresent(RSADecryptBody.class)) {
            return Optional.ofNullable(methodParameter.getMethodAnnotation(RSADecryptBody.class)).map(rsa -> EncryptAnnotationInfoBean.builder()
                    .encryptBodyMethod(EncryptBodyMethod.RSA)
                    .key(rsa.otherKey())
                    .build()).orElse(null);
        }

        return null;
    }

    /**
     * 获取类控制器上的加密注解信息
     *
     * @param clazz 控制器类
     * @return 加密注解信息
     */
    private EncryptAnnotationInfoBean getClassAnnotation(Class clazz) {
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        return Optional.ofNullable(annotations)
                .map(x -> {
                    for (Annotation annotation : x) {
                        if (annotation instanceof EncryptBody) {
                            EncryptBody encryptBody = (EncryptBody) annotation;
                            return EncryptAnnotationInfoBean.builder()
                                    .encryptBodyMethod(encryptBody.value())
                                    .key(encryptBody.otherKey())
                                    .shaEncryptType(encryptBody.shaType())
                                    .build();
                        } else if (annotation instanceof MD5EncryptBody) {
                            return EncryptAnnotationInfoBean.builder()
                                    .encryptBodyMethod(EncryptBodyMethod.MD5)
                                    .build();
                        } else if (annotation instanceof SHAEncryptBody) {
                            return EncryptAnnotationInfoBean.builder()
                                    .encryptBodyMethod(EncryptBodyMethod.SHA)
                                    .shaEncryptType(((SHAEncryptBody) annotation).value())
                                    .build();
                        } else if (annotation instanceof DESEncryptBody) {
                            return EncryptAnnotationInfoBean.builder()
                                    .encryptBodyMethod(EncryptBodyMethod.DES)
                                    .key(((DESEncryptBody) annotation).otherKey())
                                    .build();
                        } else if (annotation instanceof AESEncryptBody) {
                            return EncryptAnnotationInfoBean.builder()
                                    .encryptBodyMethod(EncryptBodyMethod.AES)
                                    .key(((AESEncryptBody) annotation).otherKey())
                                    .build();
                        } else if (annotation instanceof RSAEncryptBody) {
                            return EncryptAnnotationInfoBean.builder()
                                    .encryptBodyMethod(EncryptBodyMethod.RSA)
                                    .key(((RSAEncryptBody) annotation).otherKey())
                                    .build();
                        }
                    }
                    return null;
                }).orElse(null);
    }

    /**
     * 选择加密方式并进行加密
     *
     * @param formatStringBody 目标加密字符串
     * @param infoBean         加密信息
     * @return 加密结果
     */
    private String switchEncrypt(String formatStringBody, EncryptAnnotationInfoBean infoBean) {
        EncryptBodyMethod method = infoBean.getEncryptBodyMethod();
        if (method == null) {
            LOGGER.error("EncryptResponseBodyAdvice加密方式未定义  找不到加密的method=null  formatStringBody:{}", formatStringBody);
            throw ExceptionUtils.mpe("EncryptResponseBodyAdvice加密方式未定义  找不到加密的method");
        }

        if (Objects.equals(method, EncryptBodyMethod.MD5)) {
            return Md5EncryptUtils.encrypt(formatStringBody);
        }

        if (Objects.equals(method, EncryptBodyMethod.SHA)) {
            SHAEncryptType shaEncryptType = infoBean.getShaEncryptType();
            if (shaEncryptType == null) {
                shaEncryptType = SHAEncryptType.SHA256;
            }
            return ShaEncryptUtils.encrypt(formatStringBody, shaEncryptType);
        }

        String key = infoBean.getKey();
        if (Objects.equals(method, EncryptBodyMethod.DES)) {
            key = CheckUtils.checkAndGetKey(config.getDesKey(), key, "DES-KEY");
            return DesEncryptUtils.encrypt(formatStringBody, key);
        }

        if (Objects.equals(method, EncryptBodyMethod.AES)) {
            key = CheckUtils.checkAndGetKey(config.getAesKey(), key, "AES-KEY");
            return AesEncryptUtils.encrypt(formatStringBody, key);
        }

        if (Objects.equals(method, EncryptBodyMethod.RSA)) {
            key = CheckUtils.checkAndGetKey(config.getRsaPirKey(), key, "RSA-KEY");
            return RsaEncryptUtils.encrypt(formatStringBody, key);
        }

        LOGGER.error("EncryptResponseBodyAdvice 加密数据失败 method:{}  formatStringBody:{}", method, formatStringBody);
        throw ExceptionUtils.mpe("EncryptResponseBodyAdvice 加密数据失败");
    }

}
