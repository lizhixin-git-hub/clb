package com.lzx.frame.core.config.mvc;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * <p>响应体数据处理，防止数据类型为String时再进行JSON数据转换，那么产生最终的结果可能被双引号包含...</p>
 */
@Configuration
public class HttpConverterConfig extends WebMvcConfigurationSupport {

    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter() {
            @Override
            protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
                if (object instanceof String) {
                    Charset charset = Optional.ofNullable(this.getDefaultCharset()).orElse(Charset.defaultCharset());
                    StreamUtils.copy((String) object, charset, outputMessage.getBody());
                } else {
                    super.writeInternal(object, type, outputMessage);
                }
            }
        };
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = mappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(new LinkedList<MediaType>() {{
            add(MediaType.TEXT_HTML);
            add(MediaType.APPLICATION_JSON);
        }});
        converters.add(new StringHttpMessageConverter());
        converters.add(converter);
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

}
