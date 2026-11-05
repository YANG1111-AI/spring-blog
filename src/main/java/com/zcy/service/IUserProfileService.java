package com.zcy.service;

import com.zcy.domain.UserProfile;

/**
 * 用户个人资料Service接口
 */
public interface IUserProfileService {
    
    /**
     * 根据用户ID获取个人资料
     */
    UserProfile getUserProfile(Integer userId);
    
    /**
     * 创建个人资料
     */
    boolean createUserProfile(UserProfile userProfile);
    
    /**
     * 更新个人资料
     */
    boolean updateUserProfile(UserProfile userProfile);
    
    /**
     * 删除个人资料
     */
    boolean deleteUserProfile(Integer userId);
    
    /**
     * 获取或创建用户个人资料（如果不存在则创建默认的）
     */
    UserProfile getOrCreateUserProfile(Integer userId);
}