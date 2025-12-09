package com.lolkt.demo.config;

import org.apache.shardingsphere.shardingjdbc.api.yaml.YamlShardingDataSourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author lolkt
 * @description ShardingJdbcConfig
 * @date 2023/9/20 20:22
 **/

@Configuration
public class ShardingJdbcConfig {


    @Value("${sharding.jdbc.config.file}")
    private Resource shardingConfigFileTest;


    @Bean
    public DataSource dataSourceTest() throws SQLException, IOException {
        return YamlShardingDataSourceFactory.createDataSource(shardingConfigFileTest.getFile());
    }


}