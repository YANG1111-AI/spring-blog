package com.zcy.utils;

import com.zcy.domain.Article;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 公共工具类
 */
@Component
public class Commons {
    
    /**
     * 格式化日期
     */
    public String dateFormat(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
    
    /**
     * 截取文章内容
     */
    public String article(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        // 移除HTML标签
        String text = content.replaceAll("<[^>]+>", "");
        // 截取前150个字符
        if (text.length() > 150) {
            return text.substring(0, 150) + "...";
        }
        return text;
    }
    
    /**
     * 显示完整文章内容（保留HTML标签和图片）
     */
    public String fullArticle(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        // 简单的 Markdown 转 HTML 处理
        String htmlContent = content;
        
        // 处理图片：![alt](url) -> <img src="url" alt="alt"/>
        htmlContent = htmlContent.replaceAll("!\\[([^\\]]*)\\]\\(([^\\)]+)\\)", "<img src=\"$2\" alt=\"$1\" style=\"max-width: 100%; height: auto;\"/>");
        
        // 处理粗体：**text** -> <strong>text</strong>
        htmlContent = htmlContent.replaceAll("\\*\\*([^\\*]+)\\*\\*", "<strong>$1</strong>");
        
        // 处理代码块：```language\ncode\n``` -> <pre><code>code</code></pre>
        htmlContent = htmlContent.replaceAll("```[a-zA-Z]*\\s*([\\s\\S]*?)```", "<pre><code>$1</code></pre>");
        
        // 处理换行：\r\n -> <br/>
        htmlContent = htmlContent.replaceAll("\\r\\n", "<br/>");
        htmlContent = htmlContent.replaceAll("\\n", "<br/>");
        
        // 处理空格实体
        htmlContent = htmlContent.replaceAll("&ensp;", " ");
        htmlContent = htmlContent.replaceAll("&emsp;", "  ");
        
        return htmlContent;
    }
    
    /**
     * 获取文章摘要
     */
    public String intro(Article article) {
        if (article == null || article.getContent() == null) {
            return "";
        }
        return article(article.getContent());
    }
    
    /**
     * 获取文章摘要（指定长度）
     */
    public String intro(Article article, int length) {
        if (article == null || article.getContent() == null) {
            return "";
        }
        String content = article.getContent();
        // 移除HTML标签
        String text = content.replaceAll("<[^>]+>", "");
        // 截取指定长度
        if (text.length() > length) {
            return text.substring(0, length) + "...";
        }
        return text;
    }
    
    /**
     * 获取文章永久链接
     */
    public String permalink(Integer articleId) {
        if (articleId == null) {
            return "/";
        }
        return "/article/" + articleId;
    }
    
    /**
     * 显示文章缩略图（首页使用）
     */
    public String show_thumb(Article article) {
        if (article == null) {
            return "/user/img/rand/1.png"; // 默认图片
        }
        
        // 如果文章有缩略图，返回缩略图路径
        if (article.getThumbnail() != null && !article.getThumbnail().isEmpty()) {
            return article.getThumbnail();
        }
        
        // 首页使用 /user/img/rand/ 目录下的图片（1-25张）
        int imageIndex = (article.getId() % 25) + 1;
        return "/user/img/rand/" + imageIndex + ".png";
    }
    
    /**
     * 显示阅读排行榜文章缩略图
     */
    public String show_ranking_thumb(Article article) {
        if (article == null) {
            return "/article_img/docker/1.png"; // 默认图片
        }
        
        // 如果文章有缩略图，返回缩略图路径
        if (article.getThumbnail() != null && !article.getThumbnail().isEmpty()) {
            return article.getThumbnail();
        }
        
        // 定义所有可用的文章图片（用于阅读排行榜）
        String[] articleImages = {
            // Docker相关图片
            "/article_img/docker/1.png",
            "/article_img/docker/2.jpg",
            "/article_img/docker/3.png",
            "/article_img/docker/4.png",
            "/article_img/docker/5.png",
            "/article_img/docker/6.png",
            "/article_img/docker/7.png",
            "/article_img/docker/8.png",
            // Lambda相关图片
            "/article_img/lambda/1.jpg",
            "/article_img/lambda/2.jpg",
            "/article_img/lambda/3.jpg",
            "/article_img/lambda/4.jpg",
            // Roadmap相关图片
            "/article_img/roadmap/1.jpg",
            "/article_img/roadmap/2.jpg",
            "/article_img/roadmap/3.jpg",
            "/article_img/roadmap/4.jpg",
            "/article_img/roadmap/5.jpg",
            // Frontend map相关图片
            "/article_img/frmap/1.jpg",
            "/article_img/frmap/2.jpg",
            "/article_img/frmap/3.jpg",
            "/article_img/frmap/4.jpg",
            "/article_img/frmap/5.jpg",
            // Go map相关图片
            "/article_img/gomap/1.jpg",
            "/article_img/gomap/2.jpg",
            "/article_img/gomap/3.jpg",
            "/article_img/gomap/4.jpg",
            "/article_img/gomap/5.jpg",
            "/article_img/gomap/6.jpg",
            // PHP map相关图片
            "/article_img/phmap/1.jpg",
            "/article_img/phmap/2.jpg",
            "/article_img/phmap/3.jpg",
            "/article_img/phmap/4.jpg",
            "/article_img/phmap/5.jpg",
            "/article_img/phmap/6.jpg",
            // PHP map相关图片
            "/article_img/phpmap/1.jpg",
            "/article_img/phpmap/2.jpg",
            "/article_img/phpmap/3.jpg",
            "/article_img/phpmap/4.jpg",
            "/article_img/phpmap/5.jpg"
        };
        
        // 使用文章ID进行循环匹配
        int imageIndex = article.getId() % articleImages.length;
        return articleImages[imageIndex];
    }
    
    /**
     * 获取网站URL
     */
    public String site_url() {
        return "http://localhost:8010";
    }
    
    /**
     * 获取网站URL（带路径）
     */
    public String site_url(String path) {
        if (path == null || path.isEmpty()) {
            return site_url();
        }
        String baseUrl = "http://localhost:8010";
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return baseUrl + path;
    }
    
    /**
     * 获取网站标题
     */
    public String site_title() {
        return "zcy个人博客小站Blog";
    }
    
    /**
     * 获取网站描述
     */
    public String site_description() {
        return "一个简洁的个人博客系统";
    }
}