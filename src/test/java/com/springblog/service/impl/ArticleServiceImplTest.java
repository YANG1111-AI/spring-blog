package com.springblog.service.impl;

import com.springblog.config.BlogProperties;
import com.springblog.dao.ArticleFavoriteMapper;
import com.springblog.dao.ArticleLikeMapper;
import com.springblog.dao.ArticleMapper;
import com.springblog.dao.CommentMapper;
import com.springblog.dao.StatisticMapper;
import com.springblog.domain.Article;
import com.springblog.exception.NotOwnerException;
import com.springblog.exception.ResourceNotFoundException;
import com.springblog.support.Actor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The interesting rules live in the service: who owns an article, which fields a request may
 * change, and what a delete has to clean up.
 */
class ArticleServiceImplTest {

    private static final Actor ALICE = new Actor("alice", false);
    private static final Actor ADMIN = new Actor("root", true);

    private ArticleMapper articleMapper;
    private StatisticMapper statisticMapper;
    private CommentMapper commentMapper;
    private ArticleLikeMapper articleLikeMapper;
    private ArticleFavoriteMapper articleFavoriteMapper;
    private ArticleServiceImpl service;

    @BeforeEach
    void setUp() {
        articleMapper = mock(ArticleMapper.class);
        statisticMapper = mock(StatisticMapper.class);
        commentMapper = mock(CommentMapper.class);
        articleLikeMapper = mock(ArticleLikeMapper.class);
        articleFavoriteMapper = mock(ArticleFavoriteMapper.class);
        service = new ArticleServiceImpl(articleMapper, statisticMapper, commentMapper,
                articleLikeMapper, articleFavoriteMapper, new BlogProperties());
    }

    @Test
    void publishTakesTheAuthorFromTheActorAndCreatesItsCounterRow() {
        Article input = new Article();
        input.setId(99);
        input.setTitle("  Hello  ");
        input.setContent("body");
        input.setAuthor("someone-else");

        Article saved = service.publish(input, ALICE);

        assertThat(saved.getId()).isNull();
        assertThat(saved.getAuthor()).isEqualTo("alice");
        assertThat(saved.getTitle()).isEqualTo("Hello");
        assertThat(saved.getCategories()).isEqualTo("Uncategorized");
        assertThat(saved.getCreated()).isNotNull();
        verify(statisticMapper).insertStatistic(any());
    }

    @Test
    void updateKeepsPersistedAuthorAndCreationTime() {
        Article existing = existingArticle(7, "alice");
        when(articleMapper.selectArticleById(7)).thenReturn(existing);

        Article incoming = new Article();
        incoming.setId(7);
        incoming.setTitle("new title");
        incoming.setAuthor("attacker");
        incoming.setCreated(new Date(0));

        service.update(incoming, ALICE);

        ArgumentCaptor<Article> captor = ArgumentCaptor.forClass(Article.class);
        verify(articleMapper).updateArticle(captor.capture());
        assertThat(captor.getValue().getAuthor()).isEqualTo("alice");
        assertThat(captor.getValue().getCreated()).isEqualTo(existing.getCreated());
        assertThat(captor.getValue().getTitle()).isEqualTo("new title");
    }

    @Test
    void updateRejectsAnotherUsersArticle() {
        when(articleMapper.selectArticleById(7)).thenReturn(existingArticle(7, "bob"));

        Article incoming = new Article();
        incoming.setId(7);
        incoming.setTitle("hijacked");

        assertThatThrownBy(() -> service.update(incoming, ALICE))
                .isInstanceOf(NotOwnerException.class);
        verify(articleMapper, never()).updateArticle(any());
    }

    @Test
    void administratorsMayEditAnyArticle() {
        when(articleMapper.selectArticleById(7)).thenReturn(existingArticle(7, "bob"));

        Article incoming = new Article();
        incoming.setId(7);
        incoming.setTitle("edited by admin");

        service.update(incoming, ADMIN);

        verify(articleMapper).updateArticle(any());
    }

    @Test
    void updateOfMissingArticleIsNotFound() {
        when(articleMapper.selectArticleById(404)).thenReturn(null);

        Article incoming = new Article();
        incoming.setId(404);

        assertThatThrownBy(() -> service.update(incoming, ALICE))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteRemovesEveryDependentRow() {
        when(articleMapper.selectArticleById(7)).thenReturn(existingArticle(7, "alice"));

        service.delete(7, ALICE);

        verify(commentMapper).deleteByArticleId(7);
        verify(articleLikeMapper).deleteByArticleId(7);
        verify(articleFavoriteMapper).deleteByArticleId(7);
        verify(statisticMapper).deleteStatisticByArticleId(7);
        verify(articleMapper).deleteArticleById(7);
    }

    @Test
    void deleteByNonOwnerChangesNothing() {
        when(articleMapper.selectArticleById(7)).thenReturn(existingArticle(7, "bob"));

        assertThatThrownBy(() -> service.delete(7, ALICE))
                .isInstanceOf(NotOwnerException.class);
        verify(articleMapper, never()).deleteArticleById(7);
        verify(commentMapper, never()).deleteByArticleId(7);
    }

    @Test
    void findForEditingHidesAnotherUsersArticle() {
        when(articleMapper.selectArticleById(7)).thenReturn(existingArticle(7, "bob"));

        assertThatThrownBy(() -> service.findForEditing(7, ALICE))
                .isInstanceOf(NotOwnerException.class);
    }

    @Test
    void openForReadingCountsTheView() {
        when(articleMapper.selectArticleById(7)).thenReturn(existingArticle(7, "alice"));

        Article article = service.openForReading(7);

        verify(statisticMapper).updateArticleHits(7);
        assertThat(article.getHits()).isEqualTo(1);
    }

    @Test
    void openForReadingMissingArticleDoesNotTouchCounters() {
        when(articleMapper.selectArticleById(404)).thenReturn(null);

        assertThatThrownBy(() -> service.openForReading(404))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(statisticMapper, never()).updateArticleHits(404);
    }

    private static Article existingArticle(int id, String author) {
        Article article = new Article();
        article.setId(id);
        article.setTitle("original");
        article.setAuthor(author);
        article.setCreated(new Date(1000));
        article.setHits(0);
        return article;
    }
}
