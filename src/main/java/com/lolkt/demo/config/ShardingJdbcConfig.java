package com.lolkt.demo.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.rule.RuleConfiguration;
import org.apache.shardingsphere.readwritesplitting.api.ReadwriteSplittingRuleConfiguration;
import org.apache.shardingsphere.readwritesplitting.api.rule.ReadwriteSplittingDataSourceRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.AlgorithmConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

/**
 * ShardingSphere 5.x 配置
 * 通过 sharding.enabled=true/false 控制是否启用
 * 从 sharding.config.* 读取配置
 *
 * @author lolkt
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "sharding")
@ConditionalOnProperty(name = "sharding.enabled", havingValue = "true", matchIfMissing = false)
public class ShardingJdbcConfig {

    /**
     * 是否启用 ShardingSphere
     */
    private boolean enabled = false;

    /**
     * ShardingSphere 配置
     */
    private Map<String, Object> config;

    /**
     * 业务数据源 - ShardingSphere 5.x
     */
    @Bean
    public DataSource shardingDataSource() throws SQLException {
        if (config == null || config.isEmpty()) {
            throw new IllegalArgumentException("sharding.config must be configured");
        }
        log.info("ShardingSphere enabled, creating datasource from sharding.config");

        // 解析数据源配置
        Map<String, DataSource> dataSourceMap = createDataSources();

        // 解析规则配置
        Collection<RuleConfiguration> ruleConfigs = createRuleConfigurations();

        // 解析属性配置
        Properties props = createProperties();

        return ShardingSphereDataSourceFactory.createDataSource(dataSourceMap, ruleConfigs, props);
    }

    @SuppressWarnings("unchecked")
    private Map<String, DataSource> createDataSources() {
        Map<String, DataSource> result = new LinkedHashMap<>();
        Map<String, Object> dataSources = (Map<String, Object>) config.get("dataSources");

        if (dataSources != null) {
            for (Map.Entry<String, Object> entry : dataSources.entrySet()) {
                Map<String, Object> dsConfig = (Map<String, Object>) entry.getValue();
                result.put(entry.getKey(), createHikariDataSource(dsConfig));
            }
        }
        return result;
    }

    private DataSource createHikariDataSource(Map<String, Object> dsConfig) {
        com.zaxxer.hikari.HikariDataSource ds = new com.zaxxer.hikari.HikariDataSource();
        ds.setDriverClassName((String) dsConfig.get("driverClassName"));
        ds.setJdbcUrl((String) dsConfig.get("jdbcUrl"));
        ds.setUsername((String) dsConfig.get("username"));
        ds.setPassword((String) dsConfig.get("password"));
        return ds;
    }

    @SuppressWarnings("unchecked")
    private Collection<RuleConfiguration> createRuleConfigurations() {
        List<RuleConfiguration> result = new ArrayList<>();
        Map<String, Object> rules = (Map<String, Object>) config.get("rules");

        if (rules != null) {
            // 分片规则
            if (rules.containsKey("sharding")) {
                result.add(createShardingRuleConfiguration((Map<String, Object>) rules.get("sharding")));
            }
            // 读写分离规则
            if (rules.containsKey("readwriteSplitting")) {
                result.add(createReadwriteSplittingRuleConfiguration((Map<String, Object>) rules.get("readwriteSplitting")));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private ShardingRuleConfiguration createShardingRuleConfiguration(Map<String, Object> shardingConfig) {
        ShardingRuleConfiguration result = new ShardingRuleConfiguration();

        // 表规则
        Map<String, Object> tables = (Map<String, Object>) shardingConfig.get("tables");
        if (tables != null) {
            for (Map.Entry<String, Object> entry : tables.entrySet()) {
                Map<String, Object> tableConfig = (Map<String, Object>) entry.getValue();
                ShardingTableRuleConfiguration tableRule = new ShardingTableRuleConfiguration(
                        entry.getKey(),
                        (String) tableConfig.get("actualDataNodes")
                );

                // 分库策略
                Map<String, Object> dbStrategy = (Map<String, Object>) tableConfig.get("databaseStrategy");
                if (dbStrategy != null && dbStrategy.containsKey("standard")) {
                    Map<String, Object> standard = (Map<String, Object>) dbStrategy.get("standard");
                    tableRule.setDatabaseShardingStrategy(new StandardShardingStrategyConfiguration(
                            (String) standard.get("shardingColumn"),
                            (String) standard.get("shardingAlgorithmName")
                    ));
                }

                // 分表策略
                Map<String, Object> tableStrategy = (Map<String, Object>) tableConfig.get("tableStrategy");
                if (tableStrategy != null && tableStrategy.containsKey("standard")) {
                    Map<String, Object> standard = (Map<String, Object>) tableStrategy.get("standard");
                    tableRule.setTableShardingStrategy(new StandardShardingStrategyConfiguration(
                            (String) standard.get("shardingColumn"),
                            (String) standard.get("shardingAlgorithmName")
                    ));
                }

                result.getTables().add(tableRule);
            }
        }

        // 分片算法
        Map<String, Object> algorithms = (Map<String, Object>) shardingConfig.get("shardingAlgorithms");
        if (algorithms != null) {
            for (Map.Entry<String, Object> entry : algorithms.entrySet()) {
                Map<String, Object> algConfig = (Map<String, Object>) entry.getValue();
                Properties algProps = new Properties();
                Map<String, Object> props = (Map<String, Object>) algConfig.get("props");
                if (props != null) {
                    algProps.putAll(props);
                }
                result.getShardingAlgorithms().put(entry.getKey(),
                        new AlgorithmConfiguration((String) algConfig.get("type"), algProps));
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private ReadwriteSplittingRuleConfiguration createReadwriteSplittingRuleConfiguration(Map<String, Object> rwConfig) {
        List<ReadwriteSplittingDataSourceRuleConfiguration> dataSources = new ArrayList<>();
        Map<String, AlgorithmConfiguration> loadBalancers = new LinkedHashMap<>();

        Map<String, Object> dsConfigs = (Map<String, Object>) rwConfig.get("dataSources");
        if (dsConfigs != null) {
            for (Map.Entry<String, Object> entry : dsConfigs.entrySet()) {
                Map<String, Object> dsConfig = (Map<String, Object>) entry.getValue();
                
                // 处理 readDataSourceNames，可能是 List 或 Map
                List<String> readDataSourceNames = new ArrayList<>();
                Object readDs = dsConfig.get("readDataSourceNames");
                if (readDs instanceof List) {
                    readDataSourceNames = (List<String>) readDs;
                } else if (readDs instanceof Map) {
                    // YAML 数组被解析为 Map 时，取 values
                    readDataSourceNames = new ArrayList<>(((Map<String, String>) readDs).values());
                }

                dataSources.add(new ReadwriteSplittingDataSourceRuleConfiguration(
                        entry.getKey(),
                        (String) dsConfig.get("writeDataSourceName"),
                        readDataSourceNames,
                        (String) dsConfig.get("loadBalancerName")
                ));
            }
        }

        Map<String, Object> lbConfigs = (Map<String, Object>) rwConfig.get("loadBalancers");
        if (lbConfigs != null) {
            for (Map.Entry<String, Object> entry : lbConfigs.entrySet()) {
                Map<String, Object> lbConfig = (Map<String, Object>) entry.getValue();
                loadBalancers.put(entry.getKey(),
                        new AlgorithmConfiguration((String) lbConfig.get("type"), new Properties()));
            }
        }

        return new ReadwriteSplittingRuleConfiguration(dataSources, loadBalancers);
    }

    @SuppressWarnings("unchecked")
    private Properties createProperties() {
        Properties result = new Properties();
        Map<String, Object> props = (Map<String, Object>) config.get("props");
        if (props != null) {
            result.putAll(props);
        }
        return result;
    }
}
