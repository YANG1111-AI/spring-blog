package com.zcy.service;

import com.github.pagehelper.PageInfo;
import com.zcy.domain.Article;

import java.util.List;

/**
 * 文章Service接口
 */
public interface IArticleService {
    
    /**
     * 分页查询文章
     */
    PageInfo<Article> selectArticleWithPage(Integer page, Integer count);
    
    /**
     * 根据ID查询文章
     */
    Article selectArticleWithId(Integer id);
    
    /**
     * 发布文章
     */
    void publishArticle(Article article);
    
    /**
     * 更新文章
     */
    void updateArticleWithId(Article article);
    
    /**
     * 删除文章
     */
    void deleteArticleWithId(Integer id);
    
    /**
     * 获取文章统计信息
     */
    List<Article> getArticleStatistics();
    
    /**
     * 统计文章总数
     */
    int countArticle();
    
    /**
     * 根据作者分页查询文章
     */
    PageInfo<Article> selectArticleWithPageByAuthor(String author, Integer page, Integer count);
}