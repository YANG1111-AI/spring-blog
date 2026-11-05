package com.springblog.service;

import com.springblog.domain.ArticleLike;

import java.util.List;

/**
 * 文章点赞Service接口
 */
public interface IArticleLikeService {
    
    /**
     * 点赞文章
     */
    boolean likeArticle(Integer userId, Integer articleId);
    
    /**
     * 取消点赞
     */
    boolean unlikeArticle(Integer userId, Integer articleId);
    
    /**
     * 检查用户是否已点赞文章
     */
    boolean isLiked(Integer userId, Integer articleId);
    
    /**
     * 获取文章的点赞列表
     */
    List<ArticleLike> getArticleLikes(Integer articleId);
    
    /**
     * 获取用户的点赞列表
     */
    List<ArticleLike> getUserLikes(Integer userId);
    
    /**
     * 获取文章点赞数
     */
    int getArticleLikesCount(Integer articleId);
    
    /**
     * 获取用户点赞数
     */
    int getUserLikesCount(Integer userId);
}