package com.zcy.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 * CSRF全局Model属性配置
 */
@ControllerAdvice
public class CsrfModelAttribute {
    
    @ModelAttribute("_csrf")
    public Object csrf(HttpServletRequest request) {
        return request.getAttribute("_csrf");
    }
}