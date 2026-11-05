package com.zcy.web.client;

import com.github.pagehelper.PageInfo;
import com.zcy.domain.Article;
import com.zcy.service.IArticleService;
import com.zcy.service.ICommentService;
import com.zcy.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 前台文章Controller
 */
@Controller("clientArticleController")
public class ArticleController {
    
    @Autowired
    private IArticleService articleService;
    
    @Autowired
    private ICommentService commentService;
    
    @Autowired
    private Commons commons;
    
    /**
     * 文章详情页
     */
    @GetMapping("/article/{id}")
    public String articleDetail(@PathVariable Integer id,
                               @RequestParam(value = "cp", defaultValue = "1") int cp,
                               Model model) {
        Article article = articleService.selectArticleWithId(id);
        if (article == null) {
            return "comm/error_404";
        }
        
        model.addAttribute("article", article);
        model.addAttribute("comments", commentService.getComments(id, cp, 10));
        model.addAttribute("commons", commons);
        return "client/articleDetails";
    }
}