package com.zcy.web;

import com.zcy.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 登录注册Controller
 */
@Controller
public class LoginController {
    
    @Autowired
    private IUserService userService;
    
    /**
     * 显示登录页面
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "用户名或密码错误！");
        }
        if (logout != null) {
            model.addAttribute("message", "您已成功退出！");
        }
        return "comm/login";
    }
    
    /**
     * 简单登录测试页面
     */
    @GetMapping("/test-simple-login")
    public String testSimpleLogin() {
        return "test_simple_login";
    }
    
    /**
     * 链接测试页面
     */
    @GetMapping("/test-links")
    public String testLinks() {
        return "test_links";
    }
    
    /**
     * 测试注册链接页面
     */
    @GetMapping("/test-register-link")
    public String testRegisterLink() {
        return "test_register_link";
    }
    
    /**
     * 显示注册页面
     */
    @GetMapping("/register")
    public String registerPage() {
        return "comm/register";
    }
    
    /**
     * 处理注册请求
     */
    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String email,
                          RedirectAttributes redirectAttributes) {
        // 验证用户名是否已存在
        if (userService.isUsernameExists(username)) {
            redirectAttributes.addFlashAttribute("error", "用户名已存在！");
            return "redirect:/register";
        }
        
        // 验证邮箱是否已存在
        if (userService.isEmailExists(email)) {
            redirectAttributes.addFlashAttribute("error", "邮箱已被注册！");
            return "redirect:/register";
        }
        
        // 创建用户
        com.zcy.domain.User user = new com.zcy.domain.User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        
        try {
            userService.register(user);
            redirectAttributes.addFlashAttribute("message", "注册成功！请登录！");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "注册失败：" + e.getMessage());
            return "redirect:/register";
        }
    }
}