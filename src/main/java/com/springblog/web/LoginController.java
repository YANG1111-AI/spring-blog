package com.springblog.web;

import com.springblog.dto.RegisterForm;
import com.springblog.exception.ConflictException;
import com.springblog.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Sign-in and sign-up screens. Credential checking itself is handled by Spring Security's
 * form-login filter, so there is no password comparison in here.
 */
@Controller
public class LoginController {

    private static final String REGISTER_VIEW = "comm/register";

    private final IUserService userService;

    public LoginController(IUserService userService) {
        this.userService = userService;
    }

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

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return REGISTER_VIEW;
    }

    @PostMapping("/register")
    public String register(@Valid RegisterForm registerForm,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", firstError(bindingResult));
            return REGISTER_VIEW;
        }
        try {
            userService.register(registerForm);
        } catch (ConflictException e) {
            // A taken username is an expected outcome, so show it on the form rather than an error page.
            model.addAttribute("error", e.getMessage());
            return REGISTER_VIEW;
        }
        redirectAttributes.addFlashAttribute("message", "注册成功！请登录！");
        return "redirect:/login";
    }

    private static String firstError(BindingResult bindingResult) {
        FieldError error = bindingResult.getFieldError();
        return error == null ? "提交的内容不符合要求。" : error.getDefaultMessage();
    }
}
