package com.springblog.dto;

import com.springblog.domain.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Admin comment-edit input. Omits articleId, created and ip on purpose: the edit form renders them
 * as display text ({@code Date.toString()} for created, which no binder can parse back) and
 * {@code CommentMapper.updateComment} never rewrites them.
 */
public class AdminCommentForm {

    @NotNull(message = "评论ID不能为空")
    private Integer id;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 20000, message = "评论内容过长")
    private String content;

    @NotBlank(message = "评论作者不能为空")
    @Size(max = 200, message = "评论作者名过长")
    private String author;

    @NotBlank(message = "请选择评论状态")
    @Pattern(regexp = "approved|pending|rejected", message = "评论状态不合法")
    private String status;

    /**
     * Copies the editable fields onto the stored row; the id already comes from the loaded comment.
     */
    public void applyTo(Comment comment) {
        comment.setContent(content.trim());
        comment.setAuthor(author.trim());
        comment.setStatus(status);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
