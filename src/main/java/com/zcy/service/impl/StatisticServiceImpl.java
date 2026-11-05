package com.zcy.service.impl;

import com.zcy.domain.Statistic;
import com.zcy.service.IArticleService;
import com.zcy.service.ICommentService;
import com.zcy.service.IStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 统计服务实现类
 */
@Service
public class StatisticServiceImpl implements IStatisticService {
    
    @Autowired
    private IArticleService articleService;
    
    @Autowired
    private ICommentService commentService;
    
    @Override
    public Statistic getStatistics() {
        Statistic statistic = new Statistic();
        
        // 获取文章总数
        int articleCount = articleService.countArticle();
        statistic.setArticles(articleCount);
        
        // 获取评论总数
        int commentCount = commentService.countComment();
        statistic.setComments(commentCount);
        
        return statistic;
    }
}