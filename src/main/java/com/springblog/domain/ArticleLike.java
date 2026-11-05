package com.springblog.domain;

import java.util.Date;

/**
 * 文章点赞实体类
 */
public class ArticleLike {
    
    private Integer id;
    private Integer userId;
    private Integer articleId;
    private Date created;
    
    // 关联对象
    private User user;
    private Article article;
    
    public ArticleLike() {}
    
    public ArticleLike(Integer userId, Integer articleId) {
        this.userId = userId;
        this.articleId = articleId;
        this.created = new Date();
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public Integer getArticleId() {
        return articleId;
    }
    
    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }
    
    public Date getCreated() {
        return created;
    }
    
    public void setCreated(Date created) {
        this.created = created;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Article getArticle() {
        return article;
    }
    
    public void setArticle(Article article) {
        this.article = article;
    }
    
    @Override
    public String toString() {
        return "ArticleLike{" +
                "id=" + id +
                ", userId=" + userId +
                ", articleId=" + articleId +
                ", created=" + created +
                '}';
    }
}