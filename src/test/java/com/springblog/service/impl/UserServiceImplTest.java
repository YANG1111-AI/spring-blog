package com.springblog.service.impl;

import com.springblog.dao.UserMapper;
import com.springblog.dao.UserProfileMapper;
import com.springblog.domain.Article;
import com.springblog.domain.User;
import com.springblog.dto.RegisterForm;
import com.springblog.exception.ConflictException;
import com.springblog.service.IArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    private UserMapper userMapper;
    private UserProfileMapper userProfileMapper;
    private IArticleService articleService;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        userMapper = mock(UserMapper.class);
        userProfileMapper = mock(UserProfileMapper.class);
        articleService = mock(IArticleService.class);
        passwordEncoder = new BCryptPasswordEncoder(4);
        service = new UserServiceImpl(userMapper, userProfileMapper, articleService, passwordEncoder);
    }

    @Test
    void registrationGrantsTheRoleThatUserPagesRequire() {
        when(userMapper.selectAuthorityIdByName("ROLE_USER")).thenReturn(2);

        service.register(form("alice", "alice@example.com", "secret123"));

        verify(userMapper).insertUserAuthority(any(), eq(2));
    }

    @Test
    void registrationStoresAHashNotThePassword() {
        when(userMapper.selectAuthorityIdByName(anyString())).thenReturn(2);

        service.register(form("alice", "alice@example.com", "secret123"));

        org.mockito.ArgumentCaptor<User> captor = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(userMapper).insertUser(captor.capture());
        assertThat(captor.getValue().getPassword()).isNotEqualTo("secret123");
        assertThat(passwordEncoder.matches("secret123", captor.getValue().getPassword())).isTrue();
        assertThat(captor.getValue().getUsername()).isEqualTo("alice");
        assertThat(captor.getValue().getValid()).isEqualTo(1);
    }

    @Test
    void registrationRejectsAnExistingUsername() {
        when(userMapper.selectUserByUsername("alice")).thenReturn(new User());

        assertThatThrownBy(() -> service.register(form("alice", "alice@example.com", "secret123")))
                .isInstanceOf(ConflictException.class);
        verify(userMapper, never()).insertUser(any());
    }

    @Test
    void registrationRejectsAnExistingEmail() {
        when(userMapper.selectUserByEmail("alice@example.com")).thenReturn(new User());

        assertThatThrownBy(() -> service.register(form("alice", "alice@example.com", "secret123")))
                .isInstanceOf(ConflictException.class);
        verify(userMapper, never()).insertUser(any());
    }

    @Test
    void aLostRaceOnTheUniqueIndexBecomesAConflict() {
        when(userMapper.selectAuthorityIdByName(anyString())).thenReturn(2);
        when(userMapper.insertUser(any())).thenThrow(new DuplicateKeyException("duplicate"));

        assertThatThrownBy(() -> service.register(form("alice", "alice@example.com", "secret123")))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void aMissingRoleRowFailsLoudly() {
        when(userMapper.selectAuthorityIdByName("ROLE_USER")).thenReturn(null);

        assertThatThrownBy(() -> service.register(form("alice", "alice@example.com", "secret123")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void updateWithABlankPasswordKeepsTheStoredHash() {
        User existing = storedUser(1, "alice");
        when(userMapper.selectUserById(1)).thenReturn(existing);

        User edit = storedUser(1, "alice");
        edit.setPassword("");
        service.updateUser(edit);

        assertThat(edit.getPassword()).isEqualTo(existing.getPassword());
    }

    @Test
    void updateWithTheEchoedHashDoesNotReEncodeIt() {
        User existing = storedUser(1, "alice");
        when(userMapper.selectUserById(1)).thenReturn(existing);

        User edit = storedUser(1, "alice");
        edit.setPassword(existing.getPassword());
        service.updateUser(edit);

        assertThat(edit.getPassword()).isEqualTo(existing.getPassword());
    }

    @Test
    void updateWithANewPasswordHashesIt() {
        User existing = storedUser(1, "alice");
        when(userMapper.selectUserById(1)).thenReturn(existing);

        User edit = storedUser(1, "alice");
        edit.setPassword("brand-new");
        service.updateUser(edit);

        assertThat(passwordEncoder.matches("brand-new", edit.getPassword())).isTrue();
    }

    @Test
    void deletingAnAccountRemovesItsArticlesAndProfile() {
        User existing = storedUser(1, "alice");
        when(userMapper.selectUserById(1)).thenReturn(existing);
        Article article = new Article();
        article.setId(5);
        when(articleService.findAllByAuthor("alice")).thenReturn(List.of(article));

        service.deleteUser(1);

        verify(articleService).delete(eq(5), any());
        verify(userProfileMapper).deleteProfile(1);
        verify(userMapper).deleteUserAuthority(1);
        verify(userMapper).deleteUser(1);
    }

    private static User storedUser(int id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(new BCryptPasswordEncoder(4).encode("current"));
        user.setEmail(username + "@example.com");
        user.setValid(1);
        return user;
    }

    private static RegisterForm form(String username, String email, String password) {
        RegisterForm form = new RegisterForm();
        form.setUsername(username);
        form.setEmail(email);
        form.setPassword(password);
        return form;
    }
}
