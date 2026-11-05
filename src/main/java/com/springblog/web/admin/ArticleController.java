package com.springblog.web.admin;

import com.github.pagehelper.PageInfo;
import com.springblog.domain.Article;
import com.springblog.dto.ApiResponse;
import com.springblog.service.IArticleService;
import com.springblog.support.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 后台文章管理Controller
 *
 * <p>Handlers only translate between the view layer and {@link IArticleService}. Rules and failures
 * are handled once by the service and GlobalExceptionHandler.
 */
@Controller("adminArticleController")
@RequestMapping("/admin")
public class ArticleController {

    private static final String EDIT_VIEW = "back/article_edit";

    private final IArticleService articleService;
    private final CurrentUser currentUser;

    public ArticleController(IArticleService articleService, CurrentUser currentUser) {
        this.articleService = articleService;
        this.currentUser = currentUser;
    }

    @GetMapping("/article")
    public String articleList(@RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "count", defaultValue = "10") int count,
                              Model model) {
        PageInfo<Article> articles = articleService.findPage(page, count);
        model.addAttribute("articles", articles);
        return "back/article_list";
    }

    @GetMapping("/article/edit/{id}")
    public String editPage(@PathVariable Integer id, Model model) {
        model.addAttribute("contents", articleService.findForEditing(id, currentUser.actor()));
        return EDIT_VIEW;
    }

    @PostMapping("/article/modify")
    @ResponseBody
    public ApiResponse<Void> modifyArticle(@Valid Article article) {
        articleService.update(article, currentUser.actor());
        return ApiResponse.ok("文章更新成功");
    }

    @PostMapping("/article/publish")
    @ResponseBody
    public ApiResponse<Void> publishArticle(@Valid Article article) {
        articleService.publish(article, currentUser.actor());
        return ApiResponse.ok("文章发布成功");
    }

    @PostMapping("/article/delete/{id}")
    @ResponseBody
    public ApiResponse<Void> deleteArticle(@PathVariable Integer id) {
        articleService.delete(id, currentUser.actor());
        return ApiResponse.ok("文章删除成功");
    }
}
