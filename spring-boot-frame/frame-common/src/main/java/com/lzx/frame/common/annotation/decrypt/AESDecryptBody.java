package com.lzx.frame.common.annotation.decrypt;

import com.lzx.frame.common.constant.StringPool;
import com.lzx.frame.common.enums.encrypt.DecryptBodyMethod;

import java.lang.annotation.*;

/**
 * @see DecryptBody
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AESDecryptBody {

    /**
     * key
     */
    String otherKey() default StringPool.EMPTY;

    /**
     * 数据超时时间
     */
    long timeOut() default DecryptBodyMethod.TIME_OUT;

}
