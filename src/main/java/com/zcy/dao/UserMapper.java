package com.zcy.dao;

import com.zcy.domain.Authority;
import com.zcy.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据用户名查询用户
     */
    User selectUserByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查询用户
     */
    User selectUserByEmail(@Param("email") String email);
    
    /**
     * 插入用户
     */
    int insertUser(User user);
    
    /**
     * 查询用户的权限列表
     */
    List<Authority> selectAuthoritiesByUserId(@Param("userId") Integer userId);
    
    /**
     * 查询所有用户
     */
    List<User> selectAllUsers();
    
    /**
     * 根据ID查询用户
     */
    User selectUserById(@Param("id") Integer id);
    
    /**
     * 更新用户信息
     */
    int updateUser(User user);
    
    /**
     * 删除用户
     */
    int deleteUser(@Param("id") Integer id);
    
    /**
     * 插入用户权限关联
     */
    int insertUserAuthority(@Param("userId") Integer userId, @Param("authorityId") Integer authorityId);
}