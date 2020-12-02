package com.lzx.frame.common.annotation.encrypt;

import com.lzx.frame.common.constant.StringPool;
import com.lzx.frame.common.enums.encrypt.EncryptBodyMethod;
import com.lzx.frame.common.enums.encrypt.SHAEncryptType;

import java.lang.annotation.*;

/**
 * <p>加密{@link //org.springframework.web.bind.annotation.ResponseBody}响应数据，可用于整个控制类或者某个控制器上</p>
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptBody {

    /**
     * key
     */
    String otherKey() default StringPool.EMPTY;

    /**
     * 加密方式
     */
    EncryptBodyMethod value() default EncryptBodyMethod.MD5;

    /**
     * 加密类型
     */
    SHAEncryptType shaType() default SHAEncryptType.SHA256;

}
