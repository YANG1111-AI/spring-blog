package com.springblog.web.admin;

import com.github.pagehelper.PageInfo;
import com.springblog.domain.User;
import com.springblog.dto.AdminUserForm;
import com.springblog.dto.ApiResponse;
import com.springblog.exception.ResourceNotFoundException;
import com.springblog.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 后台用户管理Controller
 *
 * <p>Deleting an account also removes its articles, profile and authority links; that cascade is a
 * service concern, not a sequence of calls from here. Edits bind {@link AdminUserForm} rather than
 * the {@code User} entity, so a crafted POST cannot smuggle a password hash, a created date or an
 * authority list into {@code updateUser}.
 */
@Controller("adminUserController")
@RequestMapping("/admin")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String userList(@RequestParam(value = "page", defaultValue = "1") int page,
                           @RequestParam(value = "count", defaultValue = "10") int count,
                           Model model) {
        PageInfo<User> users = userService.findPage(page, count);
        model.addAttribute("users", users);
        return "back/user_list";
    }

    @GetMapping("/user/edit/{id}")
    public String editUser(@PathVariable Integer id, Model model) {
        model.addAttribute("user", requireUser(id));
        return "back/user_edit";
    }

    @PostMapping("/user/update")
    @ResponseBody
    public ApiResponse<Void> updateUser(@Valid AdminUserForm form) {
        userService.updateUser(form.toUser());
        return ApiResponse.ok("用户更新成功");
    }

    @PostMapping("/user/delete/{id}")
    @ResponseBody
    public ApiResponse<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ApiResponse.ok("用户删除成功");
    }

    @PostMapping("/user/toggle/{id}")
    @ResponseBody
    public ApiResponse<Void> toggleUser(@PathVariable Integer id) {
        userService.toggleUserStatus(id);
        return ApiResponse.ok("用户状态更新成功");
    }

    /** findById yields null instead of throwing, and user_edit.html cannot render a null model. */
    private User requireUser(Integer id) {
        User user = userService.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("用户不存在: " + id);
        }
        return user;
    }
}
