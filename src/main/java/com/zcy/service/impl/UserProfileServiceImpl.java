package com.zcy.service.impl;

import com.zcy.dao.UserMapper;
import com.zcy.dao.UserProfileMapper;
import com.zcy.domain.User;
import com.zcy.domain.UserProfile;
import com.zcy.service.IUserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户个人资料Service实现类
 */
@Service
public class UserProfileServiceImpl implements IUserProfileService {
    
    @Autowired
    private UserProfileMapper userProfileMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public UserProfile getUserProfile(Integer userId) {
        return userProfileMapper.selectProfileByUserId(userId);
    }
    
    @Override
    @Transactional
    public boolean createUserProfile(UserProfile userProfile) {
        return userProfileMapper.insertProfile(userProfile) > 0;
    }
    
    @Override
    @Transactional
    public boolean updateUserProfile(UserProfile userProfile) {
        try {
            // 数据清理和验证
            if (userProfile.getNickname() != null) {
                userProfile.setNickname(userProfile.getNickname().trim());
                if (userProfile.getNickname().isEmpty()) {
                    userProfile.setNickname(null);
                }
            }
            
            if (userProfile.getBio() != null) {
                userProfile.setBio(userProfile.getBio().trim());
                if (userProfile.getBio().isEmpty()) {
                    userProfile.setBio(null);
                }
            }
            
            if (userProfile.getWebsite() != null) {
                userProfile.setWebsite(userProfile.getWebsite().trim());
                if (userProfile.getWebsite().isEmpty()) {
                    userProfile.setWebsite(null);
                }
            }
            
            if (userProfile.getLocation() != null) {
                userProfile.setLocation(userProfile.getLocation().trim());
                if (userProfile.getLocation().isEmpty()) {
                    userProfile.setLocation(null);
                }
            }
            
            if (userProfile.getAvatar() != null) {
                userProfile.setAvatar(userProfile.getAvatar().trim());
                if (userProfile.getAvatar().isEmpty()) {
                    userProfile.setAvatar(null);
                }
            }
            
            return userProfileMapper.updateProfile(userProfile) > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新用户个人资料失败", e);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteUserProfile(Integer userId) {
        return userProfileMapper.deleteProfile(userId) > 0;
    }
    
    @Override
    public UserProfile getOrCreateUserProfile(Integer userId) {
        UserProfile profile = getUserProfile(userId);
        if (profile == null) {
            // 如果个人资料不存在，创建默认的
            User user = userMapper.selectUserById(userId);
            if (user != null) {
                profile = new UserProfile(userId);
                profile.setNickname(user.getUsername());
                if (createUserProfile(profile)) {
                    profile = getUserProfile(userId);
                }
            }
        }
        return profile;
    }
}