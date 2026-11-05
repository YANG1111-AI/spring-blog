package com.springblog.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Date;

/**
 * Article entity, also used as the form-backing object for the publish and edit screens.
 *
 * Do not bind id, author or created from a request: {@code ArticleServiceImpl} overwrites them
 * from the persisted row so a crafted payload cannot reassign ownership.
 */
public class Article {

    private Integer id;

    @NotBlank(message = "文章标题不能为空")
    @Size(max = 50, message = "文章标题不能超过50个字符")
    private String title;

    @NotBlank(message = "文章内容不能为空")
    @Size(max = 60000, message = "文章内容过长")
    private String content;

    private Date created;
    private Date modified;

    @Size(max = 50, message = "分类名称过长")
    private String categories;

    @Size(max = 255, message = "标签过长")
    private String tags;

    private Boolean allowComment;

    @Size(max = 255, message = "缩略图地址过长")
    private String thumbnail;

    private String author;

    private Integer likesCount;
    private Integer favoritesCount;

    // Populated by joins in ArticleMapper.xml, never by a request.
    private Integer hits;
    private Integer commentsNum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Boolean getAllowComment() {
        return allowComment;
    }

    public void setAllowComment(Boolean allowComment) {
        this.allowComment = allowComment;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public Integer getCommentsNum() {
        return commentsNum;
    }

    public void setCommentsNum(Integer commentsNum) {
        this.commentsNum = commentsNum;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Integer getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(Integer favoritesCount) {
        this.favoritesCount = favoritesCount;
    }

    @Override
    public String toString() {
        return "Article{id=" + id + ", title='" + title + "', author='" + author
                + "', categories='" + categories + "'}";
    }
}
