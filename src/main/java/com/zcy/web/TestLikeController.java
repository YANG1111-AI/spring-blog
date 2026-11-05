package com.zcy.web;

import com.zcy.ResponseData.ArticleResponseData;
import com.zcy.domain.User;
import com.zcy.service.IArticleLikeService;
import com.zcy.service.IArticleFavoriteService;
import com.zcy.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 测试点赞收藏功能的控制器
 */
@Controller
@RequestMapping("/test")
public class TestLikeController {
    
    @Autowired
    private IArticleLikeService articleLikeService;
    
    @Autowired
    private IArticleFavoriteService articleFavoriteService;
    
    @Autowired
    private IUserService userService;
    
    /**
     * 测试页面
     */
    @GetMapping("/like-favorite")
    public String testPage() {
        return "test_like_favorite";
    }
    
    /**
     * 测试点赞功能
     */
    @PostMapping("/like/{articleId}")
    @ResponseBody
    public ArticleResponseData<?> testLike(@PathVariable Integer articleId) {
        try {
            System.out.println("测试点赞功能，文章ID: " + articleId);
            
            // 获取当前登录用户
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                System.out.println("用户未登录");
                return ArticleResponseData.fail("请先登录");
            }
            
            System.out.println("当前用户: " + currentUser.getUsername() + ", ID: " + currentUser.getId());
            
            // 检查是否已点赞
            boolean isLiked = articleLikeService.isLiked(currentUser.getId(), articleId);
            System.out.println("是否已点赞: " + isLiked);
            
            if (isLiked) {
                // 取消点赞
                boolean success = articleLikeService.unlikeArticle(currentUser.getId(), articleId);
                System.out.println("取消点赞结果: " + success);
                if (success) {
                    int likesCount = articleLikeService.getArticleLikesCount(articleId);
                    System.out.println("当前点赞数: " + likesCount);
                    return ArticleResponseData.ok("取消点赞成功", likesCount);
                } else {
                    return ArticleResponseData.fail("取消点赞失败");
                }
            } else {
                // 点赞
                boolean success = articleLikeService.likeArticle(currentUser.getId(), articleId);
                System.out.println("点赞结果: " + success);
                if (success) {
                    int likesCount = articleLikeService.getArticleLikesCount(articleId);
                    System.out.println("当前点赞数: " + likesCount);
                    return ArticleResponseData.ok("点赞成功", likesCount);
                } else {
                    return ArticleResponseData.fail("您已经点过赞了");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ArticleResponseData.fail("点赞失败：" + e.getMessage());
        }
    }
    
    /**
     * 测试收藏功能
     */
    @PostMapping("/favorite/{articleId}")
    @ResponseBody
    public ArticleResponseData<?> testFavorite(@PathVariable Integer articleId) {
        try {
            System.out.println("测试收藏功能，文章ID: " + articleId);
            
            // 获取当前登录用户
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                System.out.println("用户未登录");
                return ArticleResponseData.fail("请先登录");
            }
            
            System.out.println("当前用户: " + currentUser.getUsername() + ", ID: " + currentUser.getId());
            
            // 检查是否已收藏
            boolean isFavorited = articleFavoriteService.isFavorited(currentUser.getId(), articleId);
            System.out.println("是否已收藏: " + isFavorited);
            
            if (isFavorited) {
                // 取消收藏
                boolean success = articleFavoriteService.unfavoriteArticle(currentUser.getId(), articleId);
                System.out.println("取消收藏结果: " + success);
                if (success) {
                    int favoritesCount = articleFavoriteService.getArticleFavoritesCount(articleId);
                    System.out.println("当前收藏数: " + favoritesCount);
                    return ArticleResponseData.ok("取消收藏成功", favoritesCount);
                } else {
                    return ArticleResponseData.fail("取消收藏失败");
                }
            } else {
                // 收藏
                boolean success = articleFavoriteService.favoriteArticle(currentUser.getId(), articleId);
                System.out.println("收藏结果: " + success);
                if (success) {
                    int favoritesCount = articleFavoriteService.getArticleFavoritesCount(articleId);
                    System.out.println("当前收藏数: " + favoritesCount);
                    return ArticleResponseData.ok("收藏成功", favoritesCount);
                } else {
                    return ArticleResponseData.fail("您已经收藏过了");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ArticleResponseData.fail("收藏失败：" + e.getMessage());
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
            e.printStackTrace();
            return null;
        }
    }
}