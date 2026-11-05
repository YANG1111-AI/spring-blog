package com.springblog.web.client;

import com.github.pagehelper.PageInfo;
import com.springblog.domain.Article;
import com.springblog.service.IArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Public front page: paginated article list plus the most-viewed ranking.
 */
@Controller
public class IndexController {

    private static final int RANKING_SIZE = 10;

    private final IArticleService articleService;

    public IndexController(IArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/")
    public String index(@RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "count", defaultValue = "5") int count,
                        Model model) {
        return renderIndex(page, count, model);
    }

    @GetMapping("/page/{pageNum}")
    public String indexPage(@PathVariable Integer pageNum,
                            @RequestParam(value = "count", defaultValue = "5") int count,
                            Model model) {
        return renderIndex(pageNum == null || pageNum < 1 ? 1 : pageNum, count, model);
    }

    private String renderIndex(int page, int count, Model model) {
        PageInfo<Article> articles = articleService.findPage(page, count);
        List<Article> ranking = articleService.findMostViewed().stream()
                .limit(RANKING_SIZE)
                .toList();
        model.addAttribute("articles", articles);
        model.addAttribute("articleList", ranking);
        return "client/index";
    }
}
