package com.springblog.web.client;

import com.springblog.dto.ApiResponse;
import com.springblog.domain.User;
import com.springblog.service.IArticleFavoriteService;
import com.springblog.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 文章收藏控制器
 */
@Controller
public class ArticleFavoriteController {
    
    @Autowired
    private IArticleFavoriteService articleFavoriteService;
    
    @Autowired
    private IUserService userService;
    
    /**
     * 收藏文章
     */
    @PostMapping("/article/favorite/{articleId}")
    @ResponseBody
    public ApiResponse<?> favoriteArticle(@PathVariable Integer articleId) {
        try {
            // 获取当前登录用户
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ApiResponse.fail("请先登录");
            }
            
            boolean success = articleFavoriteService.favoriteArticle(currentUser.getId(), articleId);
            if (success) {
                int favoritesCount = articleFavoriteService.getArticleFavoritesCount(articleId);
                return ApiResponse.ok("收藏成功", favoritesCount);
            } else {
                return ApiResponse.fail("您已经收藏过了");
            }
        } catch (Exception e) {
            return ApiResponse.fail("收藏失败：" + e.getMessage());
        }
    }
    
    /**
     * 取消收藏
     */
    @PostMapping("/article/unfavorite/{articleId}")
    @ResponseBody
    public ApiResponse<?> unfavoriteArticle(@PathVariable Integer articleId) {
        try {
            // 获取当前登录用户
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ApiResponse.fail("请先登录");
            }
            
            boolean success = articleFavoriteService.unfavoriteArticle(currentUser.getId(), articleId);
            if (success) {
                int favoritesCount = articleFavoriteService.getArticleFavoritesCount(articleId);
                return ApiResponse.ok("取消收藏成功", favoritesCount);
            } else {
                return ApiResponse.fail("取消收藏失败");
            }
        } catch (Exception e) {
            return ApiResponse.fail("取消收藏失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查收藏状态
     */
    @GetMapping("/article/favorite/status/{articleId}")
    @ResponseBody
    public ApiResponse<?> getFavoriteStatus(@PathVariable Integer articleId) {
        try {
            // 获取当前登录用户
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                int favoritesCount = articleFavoriteService.getArticleFavoritesCount(articleId);
                return ApiResponse.ok("未登录", new Object[]{false, favoritesCount});
            }
            
            boolean isFavorited = articleFavoriteService.isFavorited(currentUser.getId(), articleId);
            int favoritesCount = articleFavoriteService.getArticleFavoritesCount(articleId);
            
            return ApiResponse.ok("获取成功", new Object[]{isFavorited, favoritesCount});
        } catch (Exception e) {
            return ApiResponse.fail("获取收藏状态失败：" + e.getMessage());
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