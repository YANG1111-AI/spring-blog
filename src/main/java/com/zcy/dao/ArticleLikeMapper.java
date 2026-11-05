package com.zcy.dao;

import com.zcy.domain.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章点赞Mapper接口
 */
@Mapper
public interface ArticleLikeMapper {
    
    /**
     * 添加点赞
     */
    int insertLike(ArticleLike articleLike);
    
    /**
     * 取消点赞
     */
    int deleteLike(@Param("userId") Integer userId, @Param("articleId") Integer articleId);
    
    /**
     * 检查用户是否已点赞文章
     */
    ArticleLike selectLikeByUserAndArticle(@Param("userId") Integer userId, @Param("articleId") Integer articleId);
    
    /**
     * 获取文章的点赞列表
     */
    List<ArticleLike> selectLikesByArticleId(@Param("articleId") Integer articleId);
    
    /**
     * 获取用户的点赞列表
     */
    List<ArticleLike> selectLikesByUserId(@Param("userId") Integer userId);
    
    /**
     * 获取文章点赞数
     */
    int countLikesByArticleId(@Param("articleId") Integer articleId);
    
    /**
     * 获取用户点赞数
     */
    int countLikesByUserId(@Param("userId") Integer userId);
}