package com.springblog.dto;

import com.springblog.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Admin user-edit input. Deliberately narrower than the {@link User} entity: anything the back
 * office must not move (password hash, created, authorities) either cannot be sent or is passed
 * through {@code UserServiceImpl.updateUser}, which owns the hashing rules.
 */
public class AdminUserForm {

    @NotNull(message = "用户ID不能为空")
    private Integer id;

    @NotBlank(message = "用户名不能为空")
    @Size(max = 200, message = "用户名过长")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱过长")
    private String email;

    @Pattern(regexp = "^$|^.{8,64}$", message = "新密码长度需在8到64个字符之间")
    private String password;

    @NotNull(message = "请选择账户状态")
    @Min(value = 0, message = "账户状态不合法")
    @Max(value = 1, message = "账户状态不合法")
    private Integer valid;

    /**
     * A blank password becomes null so the service keeps the stored hash; hashing an empty field
     * would lock the account out. The username is carried for symmetry only — the update statement
     * keys on the id and never rewrites it, so this form cannot rename an account.
     */
    public User toUser() {
        User user = new User();
        user.setId(id);
        user.setUsername(username.trim());
        user.setEmail(email.trim());
        user.setValid(valid);
        user.setPassword(password == null || password.isBlank() ? null : password);
        return user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }
}
