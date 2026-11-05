package com.springblog.service.impl;

import com.springblog.domain.Statistic;
import com.springblog.service.IArticleService;
import com.springblog.service.ICommentService;
import com.springblog.service.IStatisticService;
import org.springframework.stereotype.Service;

@Service
public class StatisticServiceImpl implements IStatisticService {

    private final IArticleService articleService;
    private final ICommentService commentService;

    public StatisticServiceImpl(IArticleService articleService, ICommentService commentService) {
        this.articleService = articleService;
        this.commentService = commentService;
    }

    @Override
    public Statistic getStatistics() {
        // Two COUNT(*) reads, run per dashboard paint rather than cached: a stale counter on an
        // admin screen is worse than the query, and the tables are small.
        Statistic statistic = new Statistic();
        statistic.setArticles(articleService.count());
        statistic.setComments(commentService.countComment());
        return statistic;
    }
}
