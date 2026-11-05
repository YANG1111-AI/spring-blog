package com.springblog.service.impl;

import com.springblog.dao.ArticleLikeMapper;
import com.springblog.domain.ArticleLike;
import com.springblog.service.IArticleLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文章点赞Service实现类
 */
@Service
public class ArticleLikeServiceImpl implements IArticleLikeService {
    
    @Autowired
    private ArticleLikeMapper articleLikeMapper;
    
    @Override
    @Transactional
    public boolean likeArticle(Integer userId, Integer articleId) {
        // 检查是否已经点赞
        if (isLiked(userId, articleId)) {
            return false;
        }
        
        ArticleLike articleLike = new ArticleLike(userId, articleId);
        return articleLikeMapper.insertLike(articleLike) > 0;
    }
    
    @Override
    @Transactional
    public boolean unlikeArticle(Integer userId, Integer articleId) {
        return articleLikeMapper.deleteLike(userId, articleId) > 0;
    }
    
    @Override
    public boolean isLiked(Integer userId, Integer articleId) {
        ArticleLike like = articleLikeMapper.selectLikeByUserAndArticle(userId, articleId);
        return like != null;
    }
    
    @Override
    public List<ArticleLike> getArticleLikes(Integer articleId) {
        return articleLikeMapper.selectLikesByArticleId(articleId);
    }
    
    @Override
    public List<ArticleLike> getUserLikes(Integer userId) {
        return articleLikeMapper.selectLikesByUserId(userId);
    }
    
    @Override
    public int getArticleLikesCount(Integer articleId) {
        return articleLikeMapper.countLikesByArticleId(articleId);
    }
    
    @Override
    public int getUserLikesCount(Integer userId) {
        return articleLikeMapper.countLikesByUserId(userId);
    }
}