package com.lzx.frame.common.annotation.encrypt;

import com.lzx.frame.common.enums.encrypt.SHAEncryptType;

import java.lang.annotation.*;

/**
 * @see EncryptBody
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SHAEncryptBody {

    /**
     * 加密类型
     */
    SHAEncryptType value() default SHAEncryptType.SHA256;

}
