package com.lolkt.demo.service.impl;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.lolkt.demo.service.DynamicRoutingDataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author lolkt
 */
@Slf4j
@Service
public class DynamicRoutingDataSourceServiceImpl implements DynamicRoutingDataSourceService {

    @Override
    public String peek() {
        String dsKey = DynamicDataSourceContextHolder.peek();
        return dsKey;
    }

    @Override
    public void push(Boolean isTestUser) {
        log.info("DynamicRoutingDataSourceServiceImpl isTestUser={}", isTestUser);
        String dsKey = "sharding";
        if (isTestUser) {
            dsKey = "test";
        }
        //执行
        DynamicDataSourceContextHolder.push(dsKey);
    }

    @Override
    public void poll() {
        DynamicDataSourceContextHolder.poll();
    }

    @Override
    public void clear() {
        DynamicDataSourceContextHolder.clear();
    }

}
