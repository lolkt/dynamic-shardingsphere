package com.lolkt.demo.config;

import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

/**
 * ShardingSphere 5.x 配置
 * @author lolkt
 */
@Configuration
public class ShardingJdbcConfig {

    @Value("classpath:sharding-jdbc-main.yaml")
    private Resource shardingConfigFile;

    /**
     * 业务数据源 - ShardingSphere 5.x (分库分表 + 读写分离)
     */
    @Bean
    public DataSource shardingDataSource() throws SQLException, IOException {
        return YamlShardingSphereDataSourceFactory.createDataSource(shardingConfigFile.getFile());
    }
}
