package com.springblog.dao;

import com.springblog.domain.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论Mapper接口
 */
@Mapper
public interface CommentMapper {
    
    /**
     * 根据文章ID查询评论列表
     */
    List<Comment> selectCommentsByArticleId(@Param("articleId") Integer articleId);
    
    /**
     * 插入评论
     */
    int insertComment(Comment comment);
    
    /**
     * 统计评论总数
     */
    int countComment();
    
    /**
     * 根据文章ID统计评论数
     */
    int countCommentByArticleId(@Param("articleId") Integer articleId);
    
    /**
     * 查询所有评论（用于后台管理）
     */
    List<Comment> selectAllComments();
    
    /**
     * 根据ID查询评论
     */
    Comment selectCommentById(@Param("id") Integer id);
    
    /**
     * 更新评论
     */
    int updateComment(Comment comment);
    
    /**
     * 删除评论
     */
    int deleteComment(@Param("id") Integer id);

    /**
     * Delete every comment on an article, so removing an article leaves no orphans.
     */
    int deleteByArticleId(@Param("articleId") Integer articleId);
}