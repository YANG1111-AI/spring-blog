package com.zcy.web.admin;

import com.github.pagehelper.PageInfo;
import com.zcy.ResponseData.ArticleResponseData;
import com.zcy.domain.Article;
import com.zcy.service.IArticleService;
import com.zcy.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 后台文章管理Controller
 */
@Controller("adminArticleController")
@RequestMapping("/admin")
public class ArticleController {
    
    @Autowired
    private IArticleService articleService;
    
    @Autowired
    private Commons commons;
    
    /**
     * 文章列表页
     */
    @GetMapping("/article")
    public String articleList(@RequestParam(value = "page", defaultValue = "1") int page,
                             @RequestParam(value = "count", defaultValue = "10") int count,
                             Model model) {
        PageInfo<Article> articles = articleService.selectArticleWithPage(page, count);
        model.addAttribute("articles", articles);
        model.addAttribute("commons", commons);
        return "back/article_list";
    }
    
    /**
     * 文章编辑页
     */
    @GetMapping("/article/edit/{id}")
    public String editPage(@PathVariable Integer id, Model model) {
        Article article = articleService.selectArticleWithId(id);
        model.addAttribute("contents", article);
        model.addAttribute("commons", commons);
        return "back/article_edit";
    }
    
    /**
     * 更新文章
     */
    @PostMapping("/article/modify")
    @ResponseBody
    public ArticleResponseData<?> modifyArticle(Article article) {
        try {
            articleService.updateArticleWithId(article);
            return ArticleResponseData.ok("文章更新成功");
        } catch (Exception e) {
            return ArticleResponseData.fail("更新文章失败：" + e.getMessage());
        }
    }
    
    /**
     * 发布新文章
     */
    @PostMapping("/article/publish")
    @ResponseBody
    public ArticleResponseData<?> publishArticle(Article article) {
        try {
            articleService.publishArticle(article);
            return ArticleResponseData.ok("文章发布成功");
        } catch (Exception e) {
            return ArticleResponseData.fail("发布文章失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除文章
     */
    @PostMapping("/article/delete/{id}")
    @ResponseBody
    public ArticleResponseData<?> deleteArticle(@PathVariable Integer id) {
        try {
            articleService.deleteArticleWithId(id);
            return ArticleResponseData.ok("文章删除成功");
        } catch (Exception e) {
            return ArticleResponseData.fail("删除文章失败：" + e.getMessage());
        }
    }
}