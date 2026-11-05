package com.springblog.web.admin;

import com.springblog.service.IArticleService;
import com.springblog.service.ICommentService;
import com.springblog.service.IStatisticService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 后台管理主Controller
 */
@Controller
public class AdminController {

    /** Rows in the two dashboard panels. */
    private static final int DASHBOARD_LIST_SIZE = 5;

    private final IArticleService articleService;
    private final ICommentService commentService;
    private final IStatisticService statisticService;

    public AdminController(IArticleService articleService,
                           ICommentService commentService,
                           IStatisticService statisticService) {
        this.articleService = articleService;
        this.commentService = commentService;
        this.statisticService = statisticService;
    }

    @GetMapping({"/admin", "/admin/index"})
    public String index(Model model) {
        model.addAttribute("statistics", statisticService.getStatistics());
        model.addAttribute("articles", articleService.findPage(1, DASHBOARD_LIST_SIZE).getList());
        model.addAttribute("comments", commentService.getRecentComments(DASHBOARD_LIST_SIZE));
        return "back/index";
    }

    /**
     * Blank editor. {@code contents} stays absent on purpose: comm/article_editor only renders its
     * input hints, and back/article_edit only its "发布文章" heading, while the model is null.
     */
    @GetMapping({"/admin/article/toEditPage", "/admin/article/publish"})
    public String toEditPage() {
        return "back/article_edit";
    }
}