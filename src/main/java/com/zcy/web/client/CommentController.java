package com.zcy.web.client;

import com.zcy.ResponseData.ArticleResponseData;
import com.zcy.domain.Comment;
import com.zcy.service.ICommentService;
import com.zcy.utils.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 前台评论Controller
 */
@Controller
public class CommentController {
    
    @Autowired
    private ICommentService commentService;
    
    /**
     * 发布评论
     */
    @PostMapping("/comment")
    @ResponseBody
    public ArticleResponseData<?> publishComment(@RequestParam("aid") Integer articleId,
                                               @RequestParam("text") String content,
                                               HttpServletRequest request) {
        try {
            System.out.println("收到评论请求 - 文章ID: " + articleId + ", 内容: " + content);
            
            if (MyUtils.isEmpty(content)) {
                System.out.println("评论内容为空");
                return ArticleResponseData.fail("评论内容不能为空");
            }
            
            // 从当前登录用户获取作者信息
            String author = "匿名用户";
            if (request.getUserPrincipal() != null) {
                author = request.getUserPrincipal().getName();
                System.out.println("当前登录用户: " + author);
            } else {
                System.out.println("用户未登录");
            }
            
            Comment comment = new Comment();
            comment.setArticleId(articleId);
            comment.setContent(content);
            comment.setAuthor(author);
            comment.setIp(MyUtils.getIpAddr(request));
            
            System.out.println("准备保存评论: " + comment);
            commentService.pushComment(comment);
            System.out.println("评论保存成功");
            
            return ArticleResponseData.ok("评论发布成功");
        } catch (Exception e) {
            System.err.println("评论发布失败: " + e.getMessage());
            e.printStackTrace();
            return ArticleResponseData.fail("评论发布失败：" + e.getMessage());
        }
    }
}