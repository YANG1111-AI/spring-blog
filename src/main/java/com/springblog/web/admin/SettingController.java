package com.springblog.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 后台系统设置Controller
 *
 * <p>back/setting.html is a static overview, and it is reached from the sidebar and the dashboard
 * cards, so the mapping has to keep resolving. It reads nothing because there is nothing to read:
 * site title, url, excerpt length and thumbnails come from {@code blog.*} in application.yml via
 * BlogProperties, and the project has no settings table an administrator could persist to.
 */
@Controller
public class SettingController {

    @GetMapping("/admin/setting")
    public String settingPage() {
        return "back/setting";
    }
}
