package com.zcy.web.user;

import com.github.pagehelper.PageInfo;
import com.zcy.ResponseData.ArticleResponseData;
import com.zcy.domain.Article;
import com.zcy.service.IArticleService;
import com.zcy.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 普通用户文章管理Controller
 */
@Controller("userArticleController")
@RequestMapping("/user/article")
public class ArticleController {
    
    @Autowired
    private IArticleService articleService;
    
    @Autowired
    private Commons commons;
    
    /**
     * 跳转到发布文章页面
     */
    @GetMapping("/publish")
    public String publishPage(Model model) {
        model.addAttribute("contents", new Article());
        model.addAttribute("commons", commons);
        return "user/article_publish";
    }
    
    /**
     * 跳转到简单发布文章页面（备用）
     */
    @GetMapping("/publish-simple")
    public String publishPageSimple(Model model) {
        model.addAttribute("contents", new Article());
        model.addAttribute("commons", commons);
        return "user/article_publish_simple";
    }
    
    /**
     * 跳转到新版发布文章页面
     */
    @GetMapping("/publish-new")
    public String publishPageNew(Model model) {
        model.addAttribute("contents", new Article());
        model.addAttribute("commons", commons);
        return "user/article_publish_new";
    }
    
    /**
     * 发布文章
     */
    @PostMapping("/publish")
    public String publishArticle(Article article, HttpServletRequest request, Model model) {
        try {
            System.out.println("收到发布文章请求，文章信息：" + article);
            System.out.println("请求来源IP：" + request.getRemoteAddr());
            System.out.println("User-Agent：" + request.getHeader("User-Agent"));
            
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                "anonymousUser".equals(authentication.getName())) {
                System.err.println("用户未登录或认证失败");
                model.addAttribute("error", "用户未登录，请重新登录");
                return "redirect:/login";
            }
            
            String username = authentication.getName();
            System.out.println("当前登录用户：" + username);
            
            // 设置文章作者
            article.setAuthor(username);
            
            // 验证必填字段
            if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
                model.addAttribute("error", "文章标题不能为空");
                model.addAttribute("article", article);
                return "user/article_publish_simple";
            }
            if (article.getContent() == null || article.getContent().trim().isEmpty()) {
                model.addAttribute("error", "文章内容不能为空");
                model.addAttribute("article", article);
                return "user/article_publish_simple";
            }
            
            // 验证标题长度
            if (article.getTitle().length() > 50) {
                model.addAttribute("error", "文章标题不能超过50个字符");
                model.addAttribute("article", article);
                return "user/article_publish_simple";
            }
            
            // 设置默认分类
            if (article.getCategories() == null || article.getCategories().trim().isEmpty()) {
                article.setCategories("默认分类");
            }
            
            System.out.println("开始保存文章...");
            articleService.publishArticle(article);
            System.out.println("文章保存成功！文章ID：" + article.getId());
            
            // 重定向到我的文章页面
            return "redirect:/user/article/my?success=true";
        } catch (Exception e) {
            System.err.println("发布文章失败：" + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "发布文章失败：" + e.getMessage());
            model.addAttribute("article", article);
            return "user/article_publish_simple";
        }
    }
    
    /**
     * 发布文章 - AJAX版本
     */
    @PostMapping("/publish-ajax")
    @ResponseBody
    public ArticleResponseData<?> publishArticleAjax(Article article, HttpServletRequest request) {
        try {
            System.out.println("收到AJAX发布文章请求，文章信息：" + article);
            System.out.println("请求来源IP：" + request.getRemoteAddr());
            System.out.println("User-Agent：" + request.getHeader("User-Agent"));
            
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                "anonymousUser".equals(authentication.getName())) {
                System.err.println("用户未登录或认证失败");
                return ArticleResponseData.fail("用户未登录，请重新登录");
            }
            
            String username = authentication.getName();
            System.out.println("当前登录用户：" + username);
            
            // 设置文章作者
            article.setAuthor(username);
            
            // 验证必填字段
            if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
                return ArticleResponseData.fail("文章标题不能为空");
            }
            if (article.getContent() == null || article.getContent().trim().isEmpty()) {
                return ArticleResponseData.fail("文章内容不能为空");
            }
            
            // 验证标题长度
            if (article.getTitle().length() > 50) {
                return ArticleResponseData.fail("文章标题不能超过50个字符");
            }
            
            // 设置默认分类
            if (article.getCategories() == null || article.getCategories().trim().isEmpty()) {
                article.setCategories("默认分类");
            }
            
            System.out.println("开始保存文章...");
            articleService.publishArticle(article);
            System.out.println("文章保存成功！文章ID：" + article.getId());
            
            return ArticleResponseData.ok("文章发布成功");
        } catch (Exception e) {
            System.err.println("发布文章失败：" + e.getMessage());
            e.printStackTrace();
            return ArticleResponseData.fail("发布文章失败：" + e.getMessage());
        }
    }
    
    /**
     * 我的文章列表
     */
    @GetMapping("/my")
    public String myArticles(@RequestParam(value = "page", defaultValue = "1") int page,
                            @RequestParam(value = "count", defaultValue = "10") int count,
                            Model model) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // 根据用户名查询文章
        PageInfo<Article> articles = articleService.selectArticleWithPageByAuthor(username, page, count);
        model.addAttribute("articles", articles);
        model.addAttribute("commons", commons);
        return "user/article_list";
    }
    
    /**
     * 跳转到编辑文章页面
     */
    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable("id") Integer id, Model model) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // 获取文章信息
            Article article = articleService.selectArticleWithId(id);
            if (article == null) {
                model.addAttribute("error", "文章不存在");
                return "redirect:/user/article/my";
            }
            
            // 检查文章是否属于当前用户
            if (!username.equals(article.getAuthor())) {
                model.addAttribute("error", "您没有权限编辑此文章");
                return "redirect:/user/article/my";
            }
            
            model.addAttribute("contents", article);
            model.addAttribute("commons", commons);
            return "user/article_edit";
        } catch (Exception e) {
            System.err.println("获取文章编辑页面失败：" + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "获取文章信息失败");
            return "redirect:/user/article/my";
        }
    }
    
    /**
     * 更新文章
     */
    @PostMapping("/update")
    public String updateArticle(Article article, HttpServletRequest request, Model model) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // 获取原文章信息
            Article originalArticle = articleService.selectArticleWithId(article.getId());
            if (originalArticle == null) {
                model.addAttribute("error", "文章不存在");
                return "redirect:/user/article/my";
            }
            
            // 检查文章是否属于当前用户
            if (!username.equals(originalArticle.getAuthor())) {
                model.addAttribute("error", "您没有权限编辑此文章");
                return "redirect:/user/article/my";
            }
            
            // 验证必填字段
            if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
                model.addAttribute("error", "文章标题不能为空");
                model.addAttribute("contents", article);
                return "user/article_edit";
            }
            if (article.getContent() == null || article.getContent().trim().isEmpty()) {
                model.addAttribute("error", "文章内容不能为空");
                model.addAttribute("contents", article);
                return "user/article_edit";
            }
            
            // 验证标题长度
            if (article.getTitle().length() > 50) {
                model.addAttribute("error", "文章标题不能超过50个字符");
                model.addAttribute("contents", article);
                return "user/article_edit";
            }
            
            // 设置默认分类
            if (article.getCategories() == null || article.getCategories().trim().isEmpty()) {
                article.setCategories("默认分类");
            }
            
            // 保持原有的作者和创建时间
            article.setAuthor(originalArticle.getAuthor());
            article.setCreated(originalArticle.getCreated());
            
            System.out.println("开始更新文章...");
            articleService.updateArticleWithId(article);
            System.out.println("文章更新成功！文章ID：" + article.getId());
            
            // 重定向到我的文章页面
            return "redirect:/user/article/my?updated=true";
        } catch (Exception e) {
            System.err.println("更新文章失败：" + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "更新文章失败：" + e.getMessage());
            model.addAttribute("contents", article);
            return "user/article_edit";
        }
    }
    
    /**
     * 删除文章
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public ArticleResponseData<?> deleteArticle(@PathVariable("id") Integer id) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // 获取文章信息
            Article article = articleService.selectArticleWithId(id);
            if (article == null) {
                return ArticleResponseData.fail("文章不存在");
            }
            
            // 检查文章是否属于当前用户
            if (!username.equals(article.getAuthor())) {
                return ArticleResponseData.fail("您没有权限删除此文章");
            }
            
            System.out.println("开始删除文章，ID：" + id);
            articleService.deleteArticleWithId(id);
            System.out.println("文章删除成功！");
            
            return ArticleResponseData.ok("文章删除成功");
        } catch (Exception e) {
            System.err.println("删除文章失败：" + e.getMessage());
            e.printStackTrace();
            return ArticleResponseData.fail("删除文章失败：" + e.getMessage());
        }
    }
}