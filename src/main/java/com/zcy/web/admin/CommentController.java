package com.zcy.web.admin;

import com.github.pagehelper.PageInfo;
import com.zcy.domain.Comment;
import com.zcy.service.ICommentService;
import com.zcy.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 后台评论管理Controller
 */
@Controller("adminCommentController")
public class CommentController {
    
    @Autowired
    private ICommentService commentService;
    
    @Autowired
    private Commons commons;
    
    /**
     * 评论列表页
     */
    @GetMapping("/admin/comment")
    public String commentList(@RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "count", defaultValue = "10") int count,
                              Model model) {
        PageInfo<Comment> comments = commentService.getAllComments(page, count);
        model.addAttribute("comments", comments);
        model.addAttribute("commons", commons);
        return "back/comment_list";
    }
    
    /**
     * 评论编辑页面
     */
    @GetMapping("/admin/comment/edit/{id}")
    public String editComment(@PathVariable Integer id, Model model) {
        Comment comment = commentService.getCommentById(id);
        model.addAttribute("comment", comment);
        model.addAttribute("commons", commons);
        return "back/comment_edit";
    }
    
    /**
     * 更新评论
     */
    @PostMapping("/admin/comment/update")
    public String updateComment(Comment comment) {
        commentService.updateComment(comment);
        return "redirect:/admin/comment?success=update";
    }
    
    /**
     * 删除评论 (AJAX)
     */
    @PostMapping("/admin/comment/delete/{id}")
    @ResponseBody
    public String deleteComment(@PathVariable Integer id) {
        try {
            commentService.deleteComment(id);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }
}