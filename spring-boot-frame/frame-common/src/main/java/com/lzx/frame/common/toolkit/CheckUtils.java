package com.lzx.frame.common.toolkit;

/**
 * <p>辅助检测工具类</p>
 */
public class CheckUtils {

    private CheckUtils() {
        throw ExceptionUtils.mpe("CheckUtils辅助检测工具类 不能实例化");
    }

    public static String checkAndGetKey(String k1, String k2, String keyName) {
        if (!StringUtils.checkValNotNull(k1) && !StringUtils.checkValNotNull(k2)) {
            throw ExceptionUtils.mpe(String.format("%s is not configured (未配置%s)", keyName, keyName));
        }
        if (!StringUtils.checkValNotNull(k1)) {
            return k2;
        }
        return k1;
    }

}
