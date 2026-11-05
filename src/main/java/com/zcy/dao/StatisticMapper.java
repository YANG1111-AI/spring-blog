package com.zcy.dao;

import com.zcy.domain.Statistic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 统计Mapper接口
 */
@Mapper
public interface StatisticMapper {
    
    /**
     * 插入统计信息
     */
    int insertStatistic(Statistic statistic);
    
    /**
     * 更新文章点击量
     */
    int updateArticleHits(@Param("articleId") Integer articleId);
    
    /**
     * 更新文章评论数
     */
    int updateArticleComments(@Param("articleId") Integer articleId);
    
    /**
     * 根据文章ID删除统计信息
     */
    int deleteStatisticByArticleId(@Param("articleId") Integer articleId);
}