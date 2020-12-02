package com.lzx.frame.common.annotation.auth;

import java.lang.annotation.*;

/**
 * app登录效验
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Login {
}
