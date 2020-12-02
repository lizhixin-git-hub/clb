package com.lzx.frame.common.annotation.log;

import com.lzx.frame.common.constant.StringPool;

import java.lang.annotation.*;

/**
 * 日志注解
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 描述,单个参数表达式为#{#key},对象参数表达式为#{#student.no}
     */
    String describe() default StringPool.EMPTY;

}
