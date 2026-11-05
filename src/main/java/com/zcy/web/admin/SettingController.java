package com.zcy.web.admin;

import com.zcy.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 后台系统设置Controller
 */
@Controller
public class SettingController {
    
    @Autowired
    private Commons commons;
    
    /**
     * 系统设置页面
     */
    @GetMapping("/admin/setting")
    public String settingPage(Model model) {
        model.addAttribute("commons", commons);
        return "back/setting";
    }
}