package com.zcy.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 测试Controller
 */
@Controller
public class TestController {
    
    @GetMapping("/test-login")
    public String testLogin(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        model.addAttribute("authorities", auth.getAuthorities());
        return "test_login";
    }
    
    @GetMapping("/test_auth")
    public String testAuth() {
        return "test_auth";
    }
}