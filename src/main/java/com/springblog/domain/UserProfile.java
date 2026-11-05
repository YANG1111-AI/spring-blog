package com.springblog.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户个人资料实体类
 */
public class UserProfile {
    
    private Integer id;
    private Integer userId;
    private String nickname;
    private String avatar;
    private String bio;
    private String website;
    private String location;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    
    private Integer gender; // 0-女，1-男
    private Date created;
    private Date modified;
    
    // 关联对象
    private User user;
    
    public UserProfile() {}
    
    public UserProfile(Integer userId) {
        this.userId = userId;
        this.created = new Date();
        this.modified = new Date();
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Date getBirthday() {
        return birthday;
    }
    
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
    
    public Integer getGender() {
        return gender;
    }
    
    public void setGender(Integer gender) {
        this.gender = gender;
    }
    
    public Date getCreated() {
        return created;
    }
    
    public void setCreated(Date created) {
        this.created = created;
    }
    
    public Date getModified() {
        return modified;
    }
    
    public void setModified(Date modified) {
        this.modified = modified;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    /**
     * 获取性别显示文本
     */
    public String getGenderText() {
        if (gender == null) return "未设置";
        return gender == 1 ? "男" : "女";
    }
    
    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", userId=" + userId +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", bio='" + bio + '\'' +
                ", website='" + website + '\'' +
                ", location='" + location + '\'' +
                ", birthday=" + birthday +
                ", gender=" + gender +
                ", created=" + created +
                ", modified=" + modified +
                '}';
    }
}