package com.lolkt.demo.util;

import com.lolkt.demo.service.DynamicRoutingDataSourceService;
import org.springframework.stereotype.Component;

/**
 * 获取ThreadLocal中的测试标识路由到不同的库
 *
 * @author xfq
 * @date 2023/9/12
 */
@Component
public class DataSourceRouter {
    private static final ThreadLocal<Boolean> isTestUser = new ThreadLocal<>();

    public static void setTestUser(boolean isTest) {
        isTestUser.set(isTest);
        //通知服务修改数据源
        DynamicRoutingDataSourceService bean = SpringUtils.getBean(DynamicRoutingDataSourceService.class);
        // 使用Bean进行操作
        bean.push(isTest);

    }

    public static boolean isTestUser() {
        return isTestUser.get() == null?true:isTestUser.get();
    }

    public static void clearTestUser() {
        isTestUser.remove();
        //通知服务修改数据源
        DynamicRoutingDataSourceService bean = SpringUtils.getBean(DynamicRoutingDataSourceService.class);
        bean.clear();
    }

}

