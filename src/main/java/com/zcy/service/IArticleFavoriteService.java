package com.zcy.service;

import com.github.pagehelper.PageInfo;
import com.zcy.domain.ArticleFavorite;

import java.util.List;

/**
 * 文章收藏Service接口
 */
public interface IArticleFavoriteService {
    
    /**
     * 收藏文章
     */
    boolean favoriteArticle(Integer userId, Integer articleId);
    
    /**
     * 取消收藏
     */
    boolean unfavoriteArticle(Integer userId, Integer articleId);
    
    /**
     * 检查用户是否已收藏文章
     */
    boolean isFavorited(Integer userId, Integer articleId);
    
    /**
     * 获取文章的收藏列表
     */
    List<ArticleFavorite> getArticleFavorites(Integer articleId);
    
    /**
     * 获取用户的收藏列表（分页）
     */
    PageInfo<ArticleFavorite> getUserFavorites(Integer userId, Integer page, Integer count);
    
    /**
     * 获取用户的收藏列表
     */
    List<ArticleFavorite> getUserFavorites(Integer userId);
    
    /**
     * 获取文章收藏数
     */
    int getArticleFavoritesCount(Integer articleId);
    
    /**
     * 获取用户收藏数
     */
    int getUserFavoritesCount(Integer userId);
}