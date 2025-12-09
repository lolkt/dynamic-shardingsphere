package com.lolkt.demo.service;


/**
 * @author maq
 */
public interface DynamicRoutingDataSourceService {
    /**
     * 从ThreadLocal中获取当前数据源
     *
     * @return
     */
    String peek();

    /**
     * 切换数据源
     */
    void push(Boolean isTestUser);

    /**
     * 取出并移除
     */
    void poll();


    /**
     * 清除
     */
    void clear();
}
