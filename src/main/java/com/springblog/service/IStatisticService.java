package com.springblog.service;

import com.springblog.domain.Statistic;

/**
 * Site-wide counters for the admin dashboard.
 */
public interface IStatisticService {

    /**
     * Aggregates the counters that have a backing count query. The per-article columns of
     * {@link Statistic} (id, articleId, hits, commentsNum) belong to a t_statistic row and stay
     * unset here; so do views and users, because no mapper exposes a total-hits or user-count
     * query.
     */
    Statistic getStatistics();
}
