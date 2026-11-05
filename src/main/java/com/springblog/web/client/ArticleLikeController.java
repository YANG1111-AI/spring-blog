package com.springblog.web.client;

import com.springblog.dto.ApiResponse;
import com.springblog.domain.User;
import com.springblog.service.IArticleLikeService;
import com.springblog.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 文章点赞控制器
 */
@Controller
public class ArticleLikeController {
    
    @Autowired
    private IArticleLikeService articleLikeService;
    
    @Autowired
    private IUserService userService;
    
    /**
     * 点赞文章
     */
    @PostMapping("/article/like/{articleId}")
    @ResponseBody
    public ApiResponse<?> likeArticle(@PathVariable Integer articleId) {
        try {
            // 获取当前登录用户
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ApiResponse.fail("请先登录");
            }
            
            boolean success = articleLikeService.likeArticle(currentUser.getId(), articleId);
            if (success) {
                int likesCount = articleLikeService.getArticleLikesCount(articleId);
                return ApiResponse.ok("点赞成功", likesCount);
            } else {
                return ApiResponse.fail("您已经点过赞了");
            }
        } catch (Exception e) {
            return ApiResponse.fail("点赞失败：" + e.getMessage());
        }
    }
    
    /**
     * 取消点赞
     */
    @PostMapping("/article/unlike/{articleId}")
    @ResponseBody
    public ApiResponse<?> unlikeArticle(@PathVariable Integer articleId) {
        try {
            // 获取当前登录用户
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ApiResponse.fail("请先登录");
            }
            
            boolean success = articleLikeService.unlikeArticle(currentUser.getId(), articleId);
            if (success) {
                int likesCount = articleLikeService.getArticleLikesCount(articleId);
                return ApiResponse.ok("取消点赞成功", likesCount);
            } else {
                return ApiResponse.fail("取消点赞失败");
            }
        } catch (Exception e) {
            return ApiResponse.fail("取消点赞失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查点赞状态
     */
    @GetMapping("/article/like/status/{articleId}")
    @ResponseBody
    public ApiResponse<?> getLikeStatus(@PathVariable Integer articleId) {
        try {
            // 获取当前登录用户
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                int likesCount = articleLikeService.getArticleLikesCount(articleId);
                return ApiResponse.ok("未登录", new Object[]{false, likesCount});
            }
            
            boolean isLiked = articleLikeService.isLiked(currentUser.getId(), articleId);
            int likesCount = articleLikeService.getArticleLikesCount(articleId);
            
            return ApiResponse.ok("获取成功", new Object[]{isLiked, likesCount});
        } catch (Exception e) {
            return ApiResponse.fail("获取点赞状态失败：" + e.getMessage());
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
}