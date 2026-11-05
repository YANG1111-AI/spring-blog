package com.zcy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zcy.dao.CommentMapper;
import com.zcy.dao.StatisticMapper;
import com.zcy.domain.Comment;
import com.zcy.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 评论Service实现类
 */
@Service
public class CommentServiceImpl implements ICommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private StatisticMapper statisticMapper;
    
    @Override
    public PageInfo<Comment> getComments(Integer aid, int page, int count) {
        PageHelper.startPage(page, count);
        List<Comment> commentList = commentMapper.selectCommentsByArticleId(aid);
        return new PageInfo<>(commentList);
    }
    
    @Override
    @Transactional
    public void pushComment(Comment comment) {
        // 设置创建时间
        if (comment.getCreated() == null) {
            comment.setCreated(new Date());
        }
        
        // 设置默认状态
        if (comment.getStatus() == null) {
            comment.setStatus("approved");
        }
        
        // 保存评论
        commentMapper.insertComment(comment);
        
        // 更新文章评论数
        statisticMapper.updateArticleComments(comment.getArticleId());
    }
    
    @Override
    public int countComment() {
        return commentMapper.countComment();
    }
    
    @Override
    public PageInfo<Comment> getAllComments(int page, int count) {
        PageHelper.startPage(page, count);
        List<Comment> commentList = commentMapper.selectAllComments();
        return new PageInfo<>(commentList);
    }
    
    @Override
    public Comment getCommentById(Integer id) {
        return commentMapper.selectCommentById(id);
    }
    
    @Override
    @Transactional
    public void updateComment(Comment comment) {
        commentMapper.updateComment(comment);
    }
    
    @Override
    @Transactional
    public void deleteComment(Integer id) {
        commentMapper.deleteComment(id);
    }
    
    @Override
    public List<Comment> getRecentComments(int limit) {
        PageHelper.startPage(1, limit);
        return commentMapper.selectAllComments();
    }
}