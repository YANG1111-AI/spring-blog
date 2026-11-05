package com.zcy.service;

import com.github.pagehelper.PageInfo;
import com.zcy.domain.User;

/**
 * 用户Service接口
 */
public interface IUserService {
    
    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);
    
    /**
     * 用户注册
     */
    void register(User user);
    
    /**
     * 检查用户名是否存在
     */
    boolean isUsernameExists(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean isEmailExists(String email);
    
    /**
     * 分页查询用户
     */
    PageInfo<User> selectUserWithPage(Integer page, Integer count);
    
    /**
     * 根据ID查询用户
     */
    User selectUserWithId(Integer id);
    
    /**
     * 更新用户信息
     */
    void updateUser(User user);
    
    /**
     * 删除用户
     */
    void deleteUser(Integer id);
    
    /**
     * 切换用户状态
     */
    void toggleUserStatus(Integer id);
}