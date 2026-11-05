package com.springblog.dao;

import com.springblog.domain.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户个人资料Mapper接口
 */
@Mapper
public interface UserProfileMapper {
    
    /**
     * 根据用户ID查询个人资料
     */
    UserProfile selectProfileByUserId(@Param("userId") Integer userId);
    
    /**
     * 插入个人资料
     */
    int insertProfile(UserProfile userProfile);
    
    /**
     * 更新个人资料
     */
    int updateProfile(UserProfile userProfile);
    
    /**
     * 删除个人资料
     */
    int deleteProfile(@Param("userId") Integer userId);
}