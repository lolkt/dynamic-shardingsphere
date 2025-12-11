/*
 * Copyright © ${project.inceptionYear} organization baomidou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lolkt.demo.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 多数据源配置 - 整合 ShardingSphere 5.x 与 dynamic-datasource
 * 
 * 数据源说明：
 * - sharding: 业务数据源，由 ShardingSphere 管理分库分表和读写分离 (可选，通过 sharding.enabled 控制)
 * - uid: 百度 uid-generator 专用数据源
 * 
 * @author lolkt
 */
@Slf4j
@Configuration
@AutoConfigureBefore({DynamicDataSourceAutoConfiguration.class, SpringBootConfiguration.class})
public class MyDataSourceConfiguration {

    private final DynamicDataSourceProperties properties;

    /**
     * ShardingSphere 数据源 (可选，当 sharding.enabled=true 时存在)
     */
    @Lazy
    @Autowired(required = false)
    private DataSource shardingDataSource;

    public MyDataSourceConfiguration(DynamicDataSourceProperties properties) {
        this.properties = properties;
    }

    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider() {
        return new DynamicDataSourceProvider() {
            @Override
            public Map<String, DataSource> loadDataSources() {
                Map<String, DataSource> dataSourceMap = new HashMap<>(4);
                
                // 如果 ShardingSphere 数据源存在，则注册
                if (shardingDataSource != null) {
                    log.info("Registering ShardingSphere datasource as 'sharding'");
                    dataSourceMap.put("sharding", shardingDataSource);
                } else {
                    log.info("ShardingSphere datasource not found, skipping registration");
                }
                
                return dataSourceMap;
            }
        };
    }

    /**
     * 将动态数据源设置为首选的
     * 当spring存在多个数据源时, 自动注入的是首选的对象
     */
    @Primary
    @Bean
    public DataSource dataSource() {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
        dataSource.setPrimary(properties.getPrimary());
        dataSource.setStrict(properties.getStrict());
        dataSource.setStrategy(properties.getStrategy());
        dataSource.setP6spy(properties.getP6spy());
        dataSource.setSeata(properties.getSeata());
        return dataSource;
    }
}
