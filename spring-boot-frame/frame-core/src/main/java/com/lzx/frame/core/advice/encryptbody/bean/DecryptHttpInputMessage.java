package com.lzx.frame.core.advice.encryptbody.bean;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.InputStream;

/**
 * <p>解密信息输入流</p>
 */
public class DecryptHttpInputMessage implements HttpInputMessage {

    private InputStream body;

    private HttpHeaders headers;

    public DecryptHttpInputMessage() {
    }

    public DecryptHttpInputMessage(InputStream body, HttpHeaders headers) {
        this.body = body;
        this.headers = headers;
    }

    @Override
    public InputStream getBody() {
        return body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }

}
