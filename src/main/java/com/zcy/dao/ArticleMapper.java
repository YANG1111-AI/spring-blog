package com.zcy.dao;

import com.zcy.domain.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章Mapper接口
 */
@Mapper
public interface ArticleMapper {
    
    /**
     * 查询所有文章
     */
    List<Article> selectArticleList();
    
    /**
     * 根据ID查询文章
     */
    Article selectArticleById(@Param("id") Integer id);
    
    /**
     * 插入文章
     */
    int insertArticle(Article article);
    
    /**
     * 更新文章
     */
    int updateArticle(Article article);
    
    /**
     * 删除文章
     */
    int deleteArticleById(@Param("id") Integer id);
    
    /**
     * 统计文章总数
     */
    int countArticle();
    
    /**
     * 根据作者查询文章列表
     */
    List<Article> selectArticleListByAuthor(@Param("author") String author);
    
    /**
     * 根据点击量查询文章（用于统计）
     */
    List<Article> selectArticleByHits();
}