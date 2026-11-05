package com.springblog.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * Mapper scanning lives here rather than on the application class so that a
 * {@code @WebMvcTest} slice does not try to create repository beans it never uses.
 */
@Configuration
@MapperScan("com.springblog.dao")
public class MyBatisConfig {
}
