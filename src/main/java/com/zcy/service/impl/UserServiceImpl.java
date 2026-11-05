package com.zcy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zcy.dao.UserMapper;
import com.zcy.domain.User;
import com.zcy.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 用户Service实现类
 */
@Service
public class UserServiceImpl implements IUserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public User findByUsername(String username) {
        return userMapper.selectUserByUsername(username);
    }
    
    @Override
    @Transactional
    public void register(User user) {
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 设置创建时间
        user.setCreated(new Date());
        // 设置有效状态
        user.setValid(1);
        
        userMapper.insertUser(user);
    }
    
    @Override
    public boolean isUsernameExists(String username) {
        User user = userMapper.selectUserByUsername(username);
        return user != null;
    }
    
    @Override
    public boolean isEmailExists(String email) {
        User user = userMapper.selectUserByEmail(email);
        return user != null;
    }
    
    @Override
    public PageInfo<User> selectUserWithPage(Integer page, Integer count) {
        PageHelper.startPage(page, count);
        List<User> users = userMapper.selectAllUsers();
        return new PageInfo<>(users);
    }
    
    @Override
    public User selectUserWithId(Integer id) {
        return userMapper.selectUserById(id);
    }
    
    @Override
    @Transactional
    public void updateUser(User user) {
        // 如果提供了新密码，则加密
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 如果没有提供新密码，则不更新密码字段
            User existingUser = userMapper.selectUserById(user.getId());
            user.setPassword(existingUser.getPassword());
        }
        
        userMapper.updateUser(user);
    }
    
    @Override
    @Transactional
    public void deleteUser(Integer id) {
        userMapper.deleteUser(id);
    }
    
    @Override
    @Transactional
    public void toggleUserStatus(Integer id) {
        User user = userMapper.selectUserById(id);
        if (user != null) {
            user.setValid(user.getValid() == 1 ? 0 : 1);
            userMapper.updateUser(user);
        }
    }
}