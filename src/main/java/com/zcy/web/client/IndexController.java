package com.zcy.web.client;

import com.github.pagehelper.PageInfo;
import com.zcy.domain.Article;
import com.zcy.service.IArticleService;
import com.zcy.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 前台首页Controller
 */
@Controller
public class IndexController {
    
    @Autowired
    private IArticleService articleService;
    
    @Autowired
    private Commons commons;
    
    /**
     * 首页
     */
    @GetMapping("/")
    public String index(@RequestParam(value = "page", defaultValue = "1") int page,
                       @RequestParam(value = "count", defaultValue = "5") int count,
                       Model model) {
        return renderIndexPage(page, count, model);
    }
    
    /**
     * 分页首页
     */
    @GetMapping("/page/{pageNum}")
    public String indexPage(@PathVariable(value = "pageNum") Integer pageNum,
                           @RequestParam(value = "count", defaultValue = "5") int count,
                           Model model) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        System.out.println("访问分页：第" + pageNum + "页");
        return renderIndexPage(pageNum, count, model);
    }
    
    /**
     * 测试分页路由
     */
    @GetMapping("/test-pagination")
    public String testPagination() {
        return "test_pagination";
    }
    
    /**
     * 首页数据处理
     */
    private String renderIndexPage(int page, int count, Model model) {
        // 获取分页文章列表
        PageInfo<Article> articles = articleService.selectArticleWithPage(page, count);
        model.addAttribute("articles", articles);
        
        // 获取阅读排行榜（按点击量排序的前10篇文章）
        List<Article> articleList = articleService.getArticleStatistics();
        model.addAttribute("articleList", articleList);
        
        model.addAttribute("commons", commons);
        return "client/index";
    }
}