package com.lzx.frame.common.annotation.auth;

import java.lang.annotation.*;

/**
 * 登录用户信息
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginUser {

}
