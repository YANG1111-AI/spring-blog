package com.zcy.config;

import com.zcy.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 后台管理全局Model属性配置
 * 为所有后台管理页面自动添加commons对象
 */
@ControllerAdvice(basePackages = "com.zcy.web.admin")
public class AdminModelAttribute {
    
    @Autowired
    private Commons commons;
    
    @ModelAttribute("commons")
    public Commons getCommons() {
        return commons;
    }
}