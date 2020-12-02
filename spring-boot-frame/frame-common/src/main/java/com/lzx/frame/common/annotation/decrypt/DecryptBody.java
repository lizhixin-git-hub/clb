package com.lzx.frame.common.annotation.decrypt;

import com.lzx.frame.common.constant.StringPool;
import com.lzx.frame.common.enums.encrypt.DecryptBodyMethod;

import java.lang.annotation.*;

/**
 * <p>解密含有{@link //org.springframework.web.bind.annotation.RequestBody}
 * 注解的参数请求数据，可用于整个控制类或者某个控制器上</p>
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptBody {
    /**
     * key
     */
    String otherKey() default StringPool.EMPTY;

    /**
     * aes解密
     */
    DecryptBodyMethod value() default DecryptBodyMethod.AES;

    /**
     * 数据超时时间
     */
    long timeOut() default DecryptBodyMethod.TIME_OUT;

}
