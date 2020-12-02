package com.lzx.frame.common.exception;

/**
 * frame_application 异常类
 */
public class FrameApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FrameApplicationException(String message) {
        super(message);
    }

    public FrameApplicationException(Throwable throwable) {
        super(throwable);
    }

    public FrameApplicationException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
