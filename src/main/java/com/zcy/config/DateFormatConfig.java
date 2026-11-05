package com.zcy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 日期格式配置类
 */
@Configuration
public class DateFormatConfig implements WebMvcConfigurer {
    
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 添加日期格式化器，支持空值
        DateFormatter dateFormatter = new DateFormatter("yyyy-MM-dd");
        registry.addFormatter(dateFormatter);
    }
}