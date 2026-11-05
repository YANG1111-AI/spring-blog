package com.springblog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Static handlers deliberately stay narrower than the /user/** controller namespace: exposing
 * "/user/**" as a resource pattern shadowed the authenticated user pages.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");
        registry.addResourceHandler("/back/**")
                .addResourceLocations("classpath:/static/back/");
        registry.addResourceHandler("/user/img/**")
                .addResourceLocations("classpath:/static/user/img/");
        registry.addResourceHandler("/article_img/**")
                .addResourceLocations("classpath:/static/article_img/");
    }
}
