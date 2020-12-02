package com.lzx.frame.common.annotation.encrypt;

import com.lzx.frame.common.constant.StringPool;

import java.lang.annotation.*;

/**
 * 非对称加密
 *
 * @see EncryptBody
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RSAEncryptBody {

    /**
     * key
     */
    String otherKey() default StringPool.EMPTY;

}
