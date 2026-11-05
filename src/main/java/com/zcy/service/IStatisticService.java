package com.zcy.service;

import com.zcy.domain.Statistic;

/**
 * 统计服务接口
 */
public interface IStatisticService {
    
    /**
     * 获取网站统计信息
     */
    Statistic getStatistics();
}