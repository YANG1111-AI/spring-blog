package com.springblog.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.springblog.dao.UserMapper;
import com.springblog.dao.UserProfileMapper;
import com.springblog.domain.User;
import com.springblog.dto.RegisterForm;
import com.springblog.exception.ConflictException;
import com.springblog.exception.ResourceNotFoundException;
import com.springblog.service.IArticleService;
import com.springblog.service.IUserService;
import com.springblog.support.Actor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    /** Self-registration can only ever produce a plain user; ROLE_ADMIN rows are seeded in the db. */
    private static final String DEFAULT_AUTHORITY = "ROLE_USER";

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final IArticleService articleService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper,
                           UserProfileMapper userProfileMapper,
                           IArticleService articleService,
                           PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userProfileMapper = userProfileMapper;
        this.articleService = articleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.selectUserByUsername(username);
    }

    @Override
    public User findById(Integer id) {
        return requireExisting(id);
    }

    @Override
    @Transactional
    public void register(RegisterForm form) {
        String username = form.getUsername().trim();
        String email = form.getEmail().trim();

        if (isUsernameExists(username)) {
            throw new ConflictException("用户名已存在！");
        }
        if (isEmailExists(email)) {
            throw new ConflictException("邮箱已被注册！");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setCreated(new Date());
        user.setValid(1);
        try {
            userMapper.insertUser(user);
        } catch (DuplicateKeyException e) {
            // Another request won the race between the checks above and this insert.
            throw new ConflictException("用户名或邮箱已被注册！");
        }

        Integer authorityId = userMapper.selectAuthorityIdByName(DEFAULT_AUTHORITY);
        if (authorityId == null) {
            throw new IllegalStateException(
                    "缺少 " + DEFAULT_AUTHORITY + " 权限行，请先执行 db/schema.sql 中的 t_authority 种子数据");
        }
        userMapper.insertUserAuthority(user.getId(), authorityId);
        log.info("Registered user {}", username);
    }

    @Override
    public boolean isUsernameExists(String username) {
        return username != null && userMapper.selectUserByUsername(username) != null;
    }

    @Override
    public boolean isEmailExists(String email) {
        return email != null && userMapper.selectUserByEmail(email) != null;
    }

    @Override
    public PageInfo<User> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        return new PageInfo<>(userMapper.selectAllUsers());
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        User existing = requireExisting(user.getId());

        // A form that leaves the password field empty, or echoes back the stored hash, must never
        // get that value re-encoded: doing so would replace the hash with bcrypt(bcrypt(x)).
        String submitted = user.getPassword();
        if (submitted == null || submitted.isBlank() || submitted.equals(existing.getPassword())) {
            user.setPassword(existing.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(submitted));
        }
        user.setCreated(existing.getCreated());
        if (user.getValid() == null) {
            user.setValid(existing.getValid());
        }

        userMapper.updateUser(user);
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        User existing = requireExisting(id);

        // Articles carry their own comments, likes, favourites and statistics, so removing them
        // through the article service leaves nothing behind.
        Actor administrator = new Actor(existing.getUsername(), true);
        articleService.findAllByAuthor(existing.getUsername())
                .forEach(article -> articleService.delete(article.getId(), administrator));

        userProfileMapper.deleteProfile(id);
        userMapper.deleteUserAuthority(id);
        userMapper.deleteUser(id);
        log.info("Deleted user {}", existing.getUsername());
    }

    @Override
    @Transactional
    public void toggleUserStatus(Integer id) {
        User existing = requireExisting(id);
        existing.setValid(Integer.valueOf(1).equals(existing.getValid()) ? 0 : 1);
        userMapper.updateUser(existing);
    }

    private User requireExisting(Integer id) {
        User existing = id == null ? null : userMapper.selectUserById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("用户不存在" + (id == null ? "" : ": " + id));
        }
        return existing;
    }
}
