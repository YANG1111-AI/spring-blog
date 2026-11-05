package com.zcy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zcy.dao.ArticleMapper;
import com.zcy.dao.StatisticMapper;
import com.zcy.domain.Article;
import com.zcy.domain.Statistic;
import com.zcy.service.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 文章Service实现类
 */
@Service
public class ArticleServiceImpl implements IArticleService {
    
    @Autowired
    private ArticleMapper articleMapper;
    
    @Autowired
    private StatisticMapper statisticMapper;
    
    @Override
    public PageInfo<Article> selectArticleWithPage(Integer page, Integer count) {
        // 开启分页
        PageHelper.startPage(page, count);
        List<Article> articleList = articleMapper.selectArticleList();
        return new PageInfo<>(articleList);
    }
    
    @Override
    public Article selectArticleWithId(Integer id) {
        Article article = articleMapper.selectArticleById(id);
        if (article != null) {
            // 更新点击量
            statisticMapper.updateArticleHits(id);
            // 重新查询以获取最新的点击量
            article = articleMapper.selectArticleById(id);
        }
        return article;
    }
    
    @Override
    @Transactional
    public void publishArticle(Article article) {
        System.out.println("ArticleService.publishArticle 开始执行，文章信息：" + article);
        
        // 设置创建时间和修改时间
        if (article.getCreated() == null) {
            article.setCreated(new Date());
        }
        article.setModified(new Date());
        
        // 设置默认值
        if (article.getAllowComment() == null) {
            article.setAllowComment(true);
        }
        
        // 设置作者信息（从当前登录用户获取）
        if (article.getAuthor() == null || article.getAuthor().trim().isEmpty()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                article.setAuthor(auth.getName());
            }
        }
        
        System.out.println("准备插入文章到数据库...");
        // 保存文章
        articleMapper.insertArticle(article);
        System.out.println("文章插入成功，生成的ID：" + article.getId());
        
        // 初始化统计信息
        Statistic statistic = new Statistic();
        statistic.setArticleId(article.getId());
        statistic.setHits(0);
        statistic.setCommentsNum(0);
        System.out.println("准备插入统计信息...");
        statisticMapper.insertStatistic(statistic);
        System.out.println("统计信息插入成功");
    }
    
    @Override
    @Transactional
    public void updateArticleWithId(Article article) {
        article.setModified(new Date());
        articleMapper.updateArticle(article);
    }
    
    @Override
    @Transactional
    public void deleteArticleWithId(Integer id) {
        // 删除统计信息
        statisticMapper.deleteStatisticByArticleId(id);
        // 删除文章
        articleMapper.deleteArticleById(id);
    }
    
    @Override
    public List<Article> getArticleStatistics() {
        return articleMapper.selectArticleByHits();
    }
    
    @Override
    public int countArticle() {
        return articleMapper.countArticle();
    }
    
    @Override
    public PageInfo<Article> selectArticleWithPageByAuthor(String author, Integer page, Integer count) {
        PageHelper.startPage(page, count);
        List<Article> articleList = articleMapper.selectArticleListByAuthor(author);
        return new PageInfo<>(articleList);
    }
}