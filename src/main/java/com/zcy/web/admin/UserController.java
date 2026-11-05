package com.zcy.web.admin;

import com.github.pagehelper.PageInfo;
import com.zcy.ResponseData.ArticleResponseData;
import com.zcy.domain.User;
import com.zcy.service.IUserService;
import com.zcy.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 后台用户管理Controller
 */
@Controller("adminUserController")
@RequestMapping("/admin")
public class UserController {
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private Commons commons;
    
    /**
     * 用户管理页面
     */
    @GetMapping("/user")
    public String userList(@RequestParam(value = "page", defaultValue = "1") int page,
                          @RequestParam(value = "count", defaultValue = "10") int count,
                          Model model) {
        PageInfo<User> users = userService.selectUserWithPage(page, count);
        model.addAttribute("users", users);
        model.addAttribute("commons", commons);
        return "back/user_list";
    }
    
    /**
     * 用户编辑页面
     */
    @GetMapping("/user/edit/{id}")
    public String editUser(@PathVariable Integer id, Model model) {
        User user = userService.selectUserWithId(id);
        model.addAttribute("user", user);
        model.addAttribute("commons", commons);
        return "back/user_edit";
    }
    
    /**
     * 更新用户
     */
    @PostMapping("/user/update")
    @ResponseBody
    public ArticleResponseData<?> updateUser(User user) {
        try {
            userService.updateUser(user);
            return ArticleResponseData.ok("用户更新成功");
        } catch (Exception e) {
            return ArticleResponseData.fail("更新用户失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除用户
     */
    @PostMapping("/user/delete/{id}")
    @ResponseBody
    public ArticleResponseData<?> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return ArticleResponseData.ok("用户删除成功");
        } catch (Exception e) {
            return ArticleResponseData.fail("删除用户失败：" + e.getMessage());
        }
    }
    
    /**
     * 启用/禁用用户
     */
    @PostMapping("/user/toggle/{id}")
    @ResponseBody
    public ArticleResponseData<?> toggleUser(@PathVariable Integer id) {
        try {
            userService.toggleUserStatus(id);
            return ArticleResponseData.ok("用户状态更新成功");
        } catch (Exception e) {
            return ArticleResponseData.fail("更新用户状态失败：" + e.getMessage());
        }
    }
}