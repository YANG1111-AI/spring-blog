package com.zcy.service;

import com.github.pagehelper.PageInfo;
import com.zcy.domain.Comment;

import java.util.List;

/**
 * 评论Service接口
 */
public interface ICommentService {
    
    /**
     * 根据文章ID分页查询评论
     */
    PageInfo<Comment> getComments(Integer aid, int page, int count);
    
    /**
     * 发布评论
     */
    void pushComment(Comment comment);
    
    /**
     * 统计评论总数
     */
    int countComment();
    
    /**
     * 获取所有评论列表（用于后台管理）
     */
    PageInfo<Comment> getAllComments(int page, int count);
    
    /**
     * 根据ID获取评论详情
     */
    Comment getCommentById(Integer id);
    
    /**
     * 更新评论
     */
    void updateComment(Comment comment);
    
    /**
     * 删除评论
     */
    void deleteComment(Integer id);
    
    /**
     * 获取最新评论列表
     */
    List<Comment> getRecentComments(int limit);
}