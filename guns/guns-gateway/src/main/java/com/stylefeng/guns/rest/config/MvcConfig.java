package com.stylefeng.guns.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
<<<<<<< HEAD:guns/guns-alipay/src/main/java/com/stylefeng/guns/rest/config/MvcConfig.java
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
=======
        registry.addResourceHandler("/qRCode/**").addResourceLocations("file:D:/zfb/");
>>>>>>> c1cef5c798dd960492e8349de2a5088cc90fba8b:guns/guns-gateway/src/main/java/com/stylefeng/guns/rest/config/MvcConfig.java
    }

}
