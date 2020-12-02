package com.lzx.frame.common.annotation.encrypt;

import java.lang.annotation.*;

/**
 * EncryptBody
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MD5EncryptBody {
}
