package com.springblog.web.user;

import com.github.pagehelper.PageInfo;
import com.springblog.domain.Article;
import com.springblog.dto.ApiResponse;
import com.springblog.service.IArticleService;
import com.springblog.support.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Authoring screens for signed-in users.
 *
 * Ownership and validation rules live in {@link IArticleService}; the handlers here only translate
 * between the HTTP/form view layer and the service. SecurityConfig already requires a session for
 * /user/**, so no handler repeats an authentication check.
 */
@Controller("userArticleController")
@RequestMapping("/user/article")
public class ArticleController {

    private static final String PUBLISH_VIEW = "user/article_publish";
    private static final String EDIT_VIEW = "user/article_edit";

    private final IArticleService articleService;
    private final CurrentUser currentUser;

    public ArticleController(IArticleService articleService, CurrentUser currentUser) {
        this.articleService = articleService;
        this.currentUser = currentUser;
    }

    @GetMapping("/publish")
    public String publishForm(Model model) {
        model.addAttribute("contents", new Article());
        return PUBLISH_VIEW;
    }

    @PostMapping("/publish")
    public String publish(@Valid @ModelAttribute("contents") Article article,
                          BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", firstError(bindingResult));
            return PUBLISH_VIEW;
        }
        articleService.publish(article, currentUser.actor());
        return "redirect:/user/article/my?published=true";
    }

    /** JSON twin of {@link #publish} used by the in-page editor. */
    @PostMapping("/publish-ajax")
    @ResponseBody
    public ApiResponse<Void> publishJson(@Valid Article article) {
        articleService.publish(article, currentUser.actor());
        return ApiResponse.ok("文章发布成功");
    }

    @GetMapping("/my")
    public String myArticles(@RequestParam(value = "page", defaultValue = "1") int page,
                             @RequestParam(value = "count", defaultValue = "10") int count,
                             Model model) {
        PageInfo<Article> articles =
                articleService.findPageByAuthor(currentUser.requireUsername(), page, count);
        model.addAttribute("articles", articles);
        return "user/article_list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("contents", articleService.findForEditing(id, currentUser.actor()));
        return EDIT_VIEW;
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("contents") Article article,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", firstError(bindingResult));
            return EDIT_VIEW;
        }
        articleService.update(article, currentUser.actor());
        return "redirect:/user/article/my?updated=true";
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        articleService.delete(id, currentUser.actor());
        return ApiResponse.ok("文章删除成功");
    }

    private static String firstError(BindingResult bindingResult) {
        FieldError error = bindingResult.getFieldError();
        return error == null ? "提交的内容不符合要求。" : error.getDefaultMessage();
    }
}
