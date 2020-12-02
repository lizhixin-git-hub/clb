package com.lzx.frame.common.toolkit;

import com.lzx.frame.common.exception.FrameApplicationException;

/**
 * 异常辅助工具类
 */
public final class ExceptionUtils extends org.apache.commons.lang3.exception.ExceptionUtils {

    private ExceptionUtils() {
    }

    /**
     * 返回一个新的异常，统一构建，方便统一处理
     *
     * @param msg 消息
     * @param t   异常信息
     * @return 返回异常
     */
    public static FrameApplicationException mpe(String msg, Throwable t, Object... params) {
        return new FrameApplicationException(StringUtils.format(msg, params), t);
    }

    /**
     * 重载的方法
     *
     * @param msg 消息
     * @return 返回异常
     */
    public static FrameApplicationException mpe(String msg, Object... params) {
        return new FrameApplicationException(StringUtils.format(msg, params));
    }

    /**
     * 重载的方法
     *
     * @param t 异常
     * @return 返回异常
     */
    public static FrameApplicationException mpe(Throwable t) {
        return new FrameApplicationException(t);
    }

}
