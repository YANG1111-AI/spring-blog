package com.zcy.web.admin;

import com.zcy.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 后台分类管理Controller
 */
@Controller
public class CategoryController {
    
    @Autowired
    private Commons commons;
    
    /**
     * 分类管理页面
     */
    @GetMapping("/admin/category")
    public String categoryList(Model model) {
        model.addAttribute("commons", commons);
        return "back/category_list";
    }
}