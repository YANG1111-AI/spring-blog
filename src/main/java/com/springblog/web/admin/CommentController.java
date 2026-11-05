package com.springblog.web.admin;

import com.github.pagehelper.PageInfo;
import com.springblog.domain.Comment;
import com.springblog.dto.AdminCommentForm;
import com.springblog.dto.ApiResponse;
import com.springblog.exception.ResourceNotFoundException;
import com.springblog.service.ICommentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 后台评论管理Controller
 *
 * <p>Comment moderation is site-wide, so there is no ownership check to perform here; the service
 * methods take plain ids.
 */
@Controller("adminCommentController")
public class CommentController {

    private final ICommentService commentService;

    public CommentController(ICommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/admin/comment")
    public String commentList(@RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "count", defaultValue = "10") int count,
                              Model model) {
        PageInfo<Comment> comments = commentService.getAllComments(page, count);
        model.addAttribute("comments", comments);
        return "back/comment_list";
    }

    @GetMapping("/admin/comment/edit/{id}")
    public String editComment(@PathVariable Integer id, Model model) {
        model.addAttribute("comment", requireComment(id));
        return "back/comment_edit";
    }

    @PostMapping("/admin/comment/update")
    public String updateComment(@Valid AdminCommentForm form) {
        Comment comment = requireComment(form.getId());
        form.applyTo(comment);
        commentService.updateComment(comment);
        return "redirect:/admin/comment?success=update";
    }

    @PostMapping("/admin/comment/delete/{id}")
    @ResponseBody
    public ApiResponse<Void> deleteComment(@PathVariable Integer id) {
        commentService.deleteComment(id);
        return ApiResponse.ok("评论删除成功");
    }

    /** getCommentById yields null instead of throwing, and the edit page cannot render a null model. */
    private Comment requireComment(Integer id) {
        Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            throw new ResourceNotFoundException("评论不存在: " + id);
        }
        return comment;
    }
}
