package com.springblog.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.springblog.config.BlogProperties;
import com.springblog.dao.ArticleLikeMapper;
import com.springblog.dao.ArticleMapper;
import com.springblog.dao.CommentMapper;
import com.springblog.dao.ArticleFavoriteMapper;
import com.springblog.dao.StatisticMapper;
import com.springblog.domain.Article;
import com.springblog.domain.Statistic;
import com.springblog.exception.NotOwnerException;
import com.springblog.exception.ResourceNotFoundException;
import com.springblog.service.IArticleService;
import com.springblog.support.Actor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl implements IArticleService {

    private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);
    private static final String MOST_VIEWED_CACHE = "mostViewedArticles";

    private final ArticleMapper articleMapper;
    private final StatisticMapper statisticMapper;
    private final CommentMapper commentMapper;
    private final ArticleLikeMapper articleLikeMapper;
    private final ArticleFavoriteMapper articleFavoriteMapper;
    private final BlogProperties properties;

    public ArticleServiceImpl(ArticleMapper articleMapper,
                              StatisticMapper statisticMapper,
                              CommentMapper commentMapper,
                              ArticleLikeMapper articleLikeMapper,
                              ArticleFavoriteMapper articleFavoriteMapper,
                              BlogProperties properties) {
        this.articleMapper = articleMapper;
        this.statisticMapper = statisticMapper;
        this.commentMapper = commentMapper;
        this.articleLikeMapper = articleLikeMapper;
        this.articleFavoriteMapper = articleFavoriteMapper;
        this.properties = properties;
    }

    @Override
    public PageInfo<Article> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        return new PageInfo<>(articleMapper.selectArticleList());
    }

    @Override
    public PageInfo<Article> findPageByAuthor(String author, int page, int size) {
        PageHelper.startPage(page, size);
        return new PageInfo<>(articleMapper.selectArticleListByAuthor(author));
    }

    @Override
    public List<Article> findAllByAuthor(String author) {
        return articleMapper.selectArticleListByAuthor(author);
    }

    @Override
    public Article findById(Integer id) {
        return id == null ? null : articleMapper.selectArticleById(id);
    }

    @Override
    public Article findForEditing(Integer id, Actor actor) {
        Article article = requireExisting(id);
        requireOwnership(article, actor, "编辑");
        return article;
    }

    @Override
    @Transactional
    public Article openForReading(Integer id) {
        Article article = findById(id);
        if (article == null) {
            throw new ResourceNotFoundException("文章不存在: " + id);
        }
        // Count the view after the row is known to exist, and reflect it in the returned
        // snapshot instead of paying for a second SELECT.
        statisticMapper.updateArticleHits(id);
        if (article.getHits() != null) {
            article.setHits(article.getHits() + 1);
        }
        return article;
    }

    @Override
    @Cacheable(MOST_VIEWED_CACHE)
    public List<Article> findMostViewed() {
        return articleMapper.selectArticleByHits();
    }

    @Override
    public int count() {
        return articleMapper.countArticle();
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = MOST_VIEWED_CACHE, allEntries = true)
    public Article publish(Article article, Actor actor) {
        article.setId(null);
        article.setAuthor(actor.username());
        applyEditableFields(article);
        article.setCreated(new Date());
        article.setModified(new Date());
        if (article.getAllowComment() == null) {
            article.setAllowComment(true);
        }

        articleMapper.insertArticle(article);
        statisticMapper.insertStatistic(newStatistics(article.getId()));
        log.info("Published article {} by {}", article.getId(), actor.username());
        return article;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = MOST_VIEWED_CACHE, allEntries = true)
    public Article update(Article incoming, Actor actor) {
        Article existing = requireExisting(incoming.getId());
        requireOwnership(existing, actor, "编辑");

        // Only these fields are taken from the request; id, author and created keep their
        // persisted values so a crafted payload cannot reassign or rewind a row.
        incoming.setAuthor(existing.getAuthor());
        incoming.setCreated(existing.getCreated());
        applyEditableFields(incoming);
        incoming.setModified(new Date());

        articleMapper.updateArticle(incoming);
        return articleMapper.selectArticleById(incoming.getId());
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = MOST_VIEWED_CACHE, allEntries = true)
    public void delete(Integer id, Actor actor) {
        Article existing = requireExisting(id);
        requireOwnership(existing, actor, "删除");

        // Without these the rows would outlive the article and keep their unique indexes occupied.
        commentMapper.deleteByArticleId(id);
        articleLikeMapper.deleteByArticleId(id);
        articleFavoriteMapper.deleteByArticleId(id);
        statisticMapper.deleteStatisticByArticleId(id);
        articleMapper.deleteArticleById(id);
        log.info("Deleted article {} by {}", id, actor.username());
    }

    private Article requireExisting(Integer id) {
        Article existing = findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("文章不存在" + (id == null ? "" : ": " + id));
        }
        return existing;
    }

    private void requireOwnership(Article article, Actor actor, String action) {
        if (!actor.owns(article.getAuthor())) {
            throw new NotOwnerException("您没有权限" + action + "此文章");
        }
    }

    private void applyEditableFields(Article article) {
        article.setTitle(trim(article.getTitle()));
        article.setCategories(blankTo(article.getCategories(), properties.getContent().getDefaultCategory()));
        article.setTags(trimToNull(article.getTags()));
        article.setThumbnail(trimToNull(article.getThumbnail()));
    }

    private static Statistic newStatistics(Integer articleId) {
        Statistic statistic = new Statistic();
        statistic.setArticleId(articleId);
        statistic.setHits(0);
        statistic.setCommentsNum(0);
        return statistic;
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String blankTo(String value, String fallback) {
        String trimmed = trimToNull(value);
        return trimmed == null ? fallback : trimmed;
    }
}
