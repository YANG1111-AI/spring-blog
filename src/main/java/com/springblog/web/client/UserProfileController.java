package com.springblog.web.client;

import com.github.pagehelper.PageInfo;
import com.springblog.dto.ApiResponse;
import com.springblog.domain.ArticleFavorite;
import com.springblog.domain.User;
import com.springblog.domain.UserProfile;
import com.springblog.service.IArticleFavoriteService;
import com.springblog.service.IUserProfileService;
import com.springblog.service.IUserService;
import com.springblog.service.IArticleLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用户个人中心控制器
 */
@Controller
@RequestMapping("/user")
public class UserProfileController {
    
    @Autowired
    private IUserProfileService userProfileService;
    
    @Autowired
    private IArticleFavoriteService articleFavoriteService;
    
    @Autowired
    private IArticleLikeService articleLikeService;
    
    @Autowired
    private IUserService userService;
    
    
    /**
     * 自定义数据绑定 - 处理日期字段
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        // 允许空值，并且处理空字符串
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true) {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text == null || text.trim().isEmpty()) {
                    setValue(null);
                } else {
                    super.setAsText(text);
                }
            }
        });
    }
    
    /**
     * 个人中心首页
     */
    @GetMapping("/profile")
    public String profile(Model model) {
        try {
            // 获取当前登录用户
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return "redirect:/login";
            }
            
            // 获取或创建用户个人资料
            UserProfile profile = userProfileService.getOrCreateUserProfile(currentUser.getId());
            model.addAttribute("profile", profile);
            
            return "client/user_profile";
        } catch (Exception e) {
            model.addAttribute("error", "获取个人资料失败：" + e.getMessage());
            return "client/error";
        }
    }
    
    /**
     * 编辑个人资料页面
     */
    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        try {
            // 获取当前登录用户
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return "redirect:/login";
            }
            
            // 获取或创建用户个人资料
            UserProfile profile = userProfileService.getOrCreateUserProfile(currentUser.getId());
            model.addAttribute("profile", profile);
            
            return "client/user_profile_edit";
        } catch (Exception e) {
            model.addAttribute("error", "获取个人资料失败：" + e.getMessage());
            return "client/error";
        }
    }
    
    /**
     * 更新个人资料
     */
    @PostMapping("/profile/update")
    @ResponseBody
    public ApiResponse<?> updateProfile(UserProfile userProfile, 
                                               @RequestParam(value = "birthday", required = false) String birthdayStr) {
        try {
            // 获取当前登录用户
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ApiResponse.fail("请先登录");
            }
            
            // 设置用户ID
            userProfile.setUserId(currentUser.getId());
            
            // 处理生日字段
            if (birthdayStr != null && !birthdayStr.trim().isEmpty()) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    userProfile.setBirthday(dateFormat.parse(birthdayStr.trim()));
                } catch (Exception e) {
                    return ApiResponse.fail("生日格式不正确");
                }
            } else {
                userProfile.setBirthday(null);
            }
            
            // 数据验证
            if (userProfile.getNickname() != null && !userProfile.getNickname().trim().isEmpty()) {
                String nickname = userProfile.getNickname().trim();
                if (nickname.length() < 2 || nickname.length() > 20) {
                    return ApiResponse.fail("昵称长度应在2-20个字符之间");
                }
                userProfile.setNickname(nickname);
            }
            
            if (userProfile.getWebsite() != null && !userProfile.getWebsite().trim().isEmpty()) {
                String website = userProfile.getWebsite().trim();
                if (!isValidUrl(website)) {
                    return ApiResponse.fail("请输入有效的网站地址");
                }
                userProfile.setWebsite(website);
            }
            
            if (userProfile.getBio() != null && userProfile.getBio().length() > 500) {
                return ApiResponse.fail("个人简介不能超过500个字符");
            }
            
            // 更新个人资料
            boolean success = userProfileService.updateUserProfile(userProfile);
            
            if (success) {
                return ApiResponse.ok("个人资料更新成功");
            } else {
                return ApiResponse.fail("个人资料更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印详细错误信息用于调试
            return ApiResponse.fail("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 验证URL格式
     */
    private boolean isValidUrl(String url) {
        try {
            new java.net.URL(url);
            return url.startsWith("http://") || url.startsWith("https://");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 我的收藏页面
     */
    @GetMapping("/favorites")
    public String favorites(@RequestParam(value = "page", defaultValue = "1") int page,
                           @RequestParam(value = "count", defaultValue = "10") int count,
                           Model model) {
        try {
            // 获取当前登录用户
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return "redirect:/login";
            }
            
            // 获取用户收藏列表
            PageInfo<ArticleFavorite> favorites = articleFavoriteService.getUserFavorites(currentUser.getId(), page, count);
            model.addAttribute("favorites", favorites);
            
            return "client/user_favorites";
        } catch (Exception e) {
            model.addAttribute("error", "获取收藏列表失败：" + e.getMessage());
            return "client/error";
        }
    }
    
    /**
     * 获取当前登录用户
     */
    private User getCurrentUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return null;
            }
            
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String username = userDetails.getUsername();
            
            return userService.findByUsername(username);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取用户收藏数量
     */
    @GetMapping("/profile/favorites/count")
    @ResponseBody
    public ApiResponse<Integer> getFavoritesCount() {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ApiResponse.fail("请先登录");
            }
            
            int count = articleFavoriteService.getUserFavoritesCount(currentUser.getId());
            return ApiResponse.ok("获取成功", count);
        } catch (Exception e) {
            return ApiResponse.fail("获取收藏数量失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户点赞数量
     */
    @GetMapping("/profile/likes/count")
    @ResponseBody
    public ApiResponse<Integer> getLikesCount() {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ApiResponse.fail("请先登录");
            }
            
            int count = articleLikeService.getUserLikesCount(currentUser.getId());
            return ApiResponse.ok("获取成功", count);
        } catch (Exception e) {
            return ApiResponse.fail("获取点赞数量失败：" + e.getMessage());
        }
    }
}