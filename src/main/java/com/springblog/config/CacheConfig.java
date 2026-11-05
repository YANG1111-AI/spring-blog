package com.springblog.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Caching is on by default with the in-memory provider. Setting spring.cache.type=redis routes the
 * same @Cacheable annotations through Redis without touching service code.
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
