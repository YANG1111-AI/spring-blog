package com.zcy.web.admin;

import com.zcy.domain.Article;
import com.zcy.domain.Comment;
import com.zcy.service.IArticleService;
import com.zcy.service.ICommentService;
import com.zcy.service.IStatisticService;
import com.zcy.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 后台管理主Controller
 */
@Controller
public class AdminController {
    
    @Autowired
    private IArticleService articleService;
    
    @Autowired
    private ICommentService commentService;
    
    @Autowired
    private IStatisticService statisticService;
    
    @Autowired
    private Commons commons;
    
    /**
     * 后台首页
     */
    @GetMapping("/admin")
    public String index(Model model) {
        // 获取统计信息
        model.addAttribute("statistics", statisticService.getStatistics());
        
        // 获取最新文章
        List<Article> recentArticles = articleService.getArticleStatistics();
        model.addAttribute("articles", recentArticles);
        
        // 获取最新评论
        List<Comment> recentComments = commentService.getRecentComments(5);
        model.addAttribute("comments", recentComments);
        
        model.addAttribute("commons", commons);
        return "back/index";
    }
    
    /**
     * 后台首页 - 别名映射
     */
    @GetMapping("/admin/index")
    public String adminIndex(Model model) {
        return index(model);
    }
    
    /**
     * 文章发布页面
     */
    @GetMapping("/admin/article/toEditPage")
    public String toEditPage(Model model) {
        model.addAttribute("commons", commons);
        return "back/article_edit";
    }
}