package com.lzx.frame.common.annotation.encrypt;

import com.lzx.frame.common.constant.StringPool;

import java.lang.annotation.*;

/**
 * @author guo
 * EncryptBody
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DESEncryptBody {

    /**
     * key
     */
    String otherKey() default StringPool.EMPTY;

}
