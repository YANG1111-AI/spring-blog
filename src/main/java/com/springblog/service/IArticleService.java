package com.springblog.service;

import com.github.pagehelper.PageInfo;
import com.springblog.domain.Article;
import com.springblog.support.Actor;

import java.util.List;

/**
 * Article use cases. Every write takes an {@link Actor} so ownership is enforced here rather than
 * in each controller.
 */
public interface IArticleService {

    PageInfo<Article> findPage(int page, int size);

    PageInfo<Article> findPageByAuthor(String author, int page, int size);

    /** Every article by one author, used when an account is removed. */
    List<Article> findAllByAuthor(String author);

    /** Read without side effects: used by the edit and delete screens. */
    Article findById(Integer id);

    /**
     * Read for the edit form. Enforces ownership so opening the form is not itself a leak.
     *
     * @throws com.springblog.exception.NotOwnerException the actor is not the author
     */
    Article findForEditing(Integer id, Actor actor);

    /** Read for the public detail page; counts a view and returns the refreshed row. */
    Article openForReading(Integer id);

    /** Most-viewed articles for the sidebar ranking, cached. */
    List<Article> findMostViewed();

    int count();

    Article publish(Article article, Actor actor);

    /**
     * Replace the editable fields of an existing article.
     *
     * @throws com.springblog.exception.ResourceNotFoundException the article does not exist
     * @throws com.springblog.exception.NotOwnerException         the actor is not the author
     */
    Article update(Article article, Actor actor);

    void delete(Integer id, Actor actor);
}
