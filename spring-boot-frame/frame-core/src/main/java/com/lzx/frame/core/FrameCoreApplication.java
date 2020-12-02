package com.lzx.frame.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 自定义注解对参数进行加/解密
 * 也可以使用encrypt-body-spring-boot-starter插件对参数进行加/解密的处理
 * 引入坐标：
 * <dependency>
 *     <groupId>cn.licoy</groupId>
 *     <artifactId>encrypt-body-spring-boot-starter</artifactId>
 *     <version>1.0.4.RELEASE</version>
 * </dependency>
 * 插件github路径：https://github.com/Licoy/encrypt-body-spring-boot-starter/
 */
@SpringBootApplication
public class FrameCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameCoreApplication.class, args);
    }

}
