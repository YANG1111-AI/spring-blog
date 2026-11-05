package com.springblog.web.client;

import com.springblog.domain.Article;
import com.springblog.service.IArticleService;
import com.springblog.service.ICommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Public article detail page.
 */
@Controller("clientArticleController")
public class ArticleController {

    private static final int COMMENTS_PER_PAGE = 10;

    private final IArticleService articleService;
    private final ICommentService commentService;

    public ArticleController(IArticleService articleService, ICommentService commentService) {
        this.articleService = articleService;
        this.commentService = commentService;
    }

    @GetMapping("/article/{id}")
    public String articleDetail(@PathVariable Integer id,
                               @RequestParam(value = "cp", defaultValue = "1") int commentPage,
                               Model model) {
        // openForReading counts the view and raises ResourceNotFoundException for a missing id,
        // which the global handler turns into comm/error_404.
        Article article = articleService.openForReading(id);
        model.addAttribute("article", article);
        model.addAttribute("comments", commentService.getComments(id, commentPage, COMMENTS_PER_PAGE));
        return "client/articleDetails";
    }
}
