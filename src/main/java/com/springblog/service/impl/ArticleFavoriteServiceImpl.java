package com.springblog.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.springblog.dao.ArticleFavoriteMapper;
import com.springblog.domain.ArticleFavorite;
import com.springblog.service.IArticleFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文章收藏Service实现类
 */
@Service
public class ArticleFavoriteServiceImpl implements IArticleFavoriteService {
    
    @Autowired
    private ArticleFavoriteMapper articleFavoriteMapper;
    
    @Override
    @Transactional
    public boolean favoriteArticle(Integer userId, Integer articleId) {
        // 检查是否已经收藏
        if (isFavorited(userId, articleId)) {
            return false;
        }
        
        ArticleFavorite articleFavorite = new ArticleFavorite(userId, articleId);
        return articleFavoriteMapper.insertFavorite(articleFavorite) > 0;
    }
    
    @Override
    @Transactional
    public boolean unfavoriteArticle(Integer userId, Integer articleId) {
        return articleFavoriteMapper.deleteFavorite(userId, articleId) > 0;
    }
    
    @Override
    public boolean isFavorited(Integer userId, Integer articleId) {
        ArticleFavorite favorite = articleFavoriteMapper.selectFavoriteByUserAndArticle(userId, articleId);
        return favorite != null;
    }
    
    @Override
    public List<ArticleFavorite> getArticleFavorites(Integer articleId) {
        return articleFavoriteMapper.selectFavoritesByArticleId(articleId);
    }
    
    @Override
    public PageInfo<ArticleFavorite> getUserFavorites(Integer userId, Integer page, Integer count) {
        PageHelper.startPage(page, count);
        List<ArticleFavorite> favorites = articleFavoriteMapper.selectFavoritesByUserId(userId);
        return new PageInfo<>(favorites);
    }
    
    @Override
    public List<ArticleFavorite> getUserFavorites(Integer userId) {
        return articleFavoriteMapper.selectFavoritesByUserId(userId);
    }
    
    @Override
    public int getArticleFavoritesCount(Integer articleId) {
        return articleFavoriteMapper.countFavoritesByArticleId(articleId);
    }
    
    @Override
    public int getUserFavoritesCount(Integer userId) {
        return articleFavoriteMapper.countFavoritesByUserId(userId);
    }
}