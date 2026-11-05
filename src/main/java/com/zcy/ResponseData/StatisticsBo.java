package com.zcy.ResponseData;

/**
 * 统计数据业务对象
 */
public class StatisticsBo {
    private Integer articles;
    private Integer comments;
    private Integer attachs;
    private Integer links;

    public Integer getArticles() {
        return articles;
    }

    public void setArticles(Integer articles) {
        this.articles = articles;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public Integer getAttachs() {
        return attachs;
    }

    public void setAttachs(Integer attachs) {
        this.attachs = attachs;
    }

    public Integer getLinks() {
        return links;
    }

    public void setLinks(Integer links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "StatisticsBo{" +
                "articles=" + articles +
                ", comments=" + comments +
                ", attachs=" + attachs +
                ", links=" + links +
                '}';
    }
}