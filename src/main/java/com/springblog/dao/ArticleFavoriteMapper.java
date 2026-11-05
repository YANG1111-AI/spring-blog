package com.springblog.dao;

import com.springblog.domain.ArticleFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章收藏Mapper接口
 */
@Mapper
public interface ArticleFavoriteMapper {
    
    /**
     * 添加收藏
     */
    int insertFavorite(ArticleFavorite articleFavorite);
    
    /**
     * 取消收藏
     */
    int deleteFavorite(@Param("userId") Integer userId, @Param("articleId") Integer articleId);
    
    /**
     * 检查用户是否已收藏文章
     */
    ArticleFavorite selectFavoriteByUserAndArticle(@Param("userId") Integer userId, @Param("articleId") Integer articleId);
    
    /**
     * 获取文章的收藏列表
     */
    List<ArticleFavorite> selectFavoritesByArticleId(@Param("articleId") Integer articleId);
    
    /**
     * 获取用户的收藏列表（分页）
     */
    List<ArticleFavorite> selectFavoritesByUserId(@Param("userId") Integer userId);
    
    /**
     * 获取文章收藏数
     */
    int countFavoritesByArticleId(@Param("articleId") Integer articleId);
    
    /**
     * 获取用户收藏数
     */
    int countFavoritesByUserId(@Param("userId") Integer userId);

    /**
     * Delete every favourite on an article, so removing an article leaves no orphans.
     */
    int deleteByArticleId(@Param("articleId") Integer articleId);
}