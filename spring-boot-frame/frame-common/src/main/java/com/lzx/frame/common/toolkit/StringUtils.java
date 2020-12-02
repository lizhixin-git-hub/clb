package com.lzx.frame.common.toolkit;

/**
 * String 工具类
 */
public final class StringUtils extends org.apache.commons.lang3.StringUtils {

    private StringUtils() {
    }

    /**
     * 安全的进行字符串 format
     *
     * @param target 目标字符串
     * @param params format 参数
     * @return format 后的
     */
    public static String format(String target, Object... params) {
        return String.format(target, params);
    }

    /**
     * 判断对象是否为空
     *
     * @param object ignore
     * @return ignore
     */
    public static boolean checkValNotNull(Object object) {
        if (object instanceof CharSequence) {
            return isNotBlank((CharSequence) object);
        }
        return object != null;
    }

}
