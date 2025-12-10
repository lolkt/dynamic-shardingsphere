# 多数据源 + ShardingSphere 5.x 分库分表 + 百度UID 实现方案

## 概述

本项目实现了基于 `dynamic-datasource` + `ShardingSphere 5.4.1` 的多数据源方案：
- **sharding 数据源**：业务库，由 ShardingSphere 管理分库分表和读写分离
- **uid 数据源**：百度 uid-generator 专用数据源

## 核心架构

```
┌─────────────────────────────────────────────────────────┐
│                  DynamicRoutingDataSource               │
│                      (动态数据源)                        │
├─────────────────────────┬───────────────────────────────┤
│     sharding (主)       │           uid                 │
│   ShardingSphere 5.x    │    百度 uid-generator         │
├─────────────────────────┼───────────────────────────────┤
│  ┌─────────────────┐    │                               │
│  │   分库分表       │    │      worker_node 表          │
│  │  t_customer     │    │                               │
│  │  t_order        │    │                               │
│  ├─────────────────┤    │                               │
│  │   读写分离       │    │                               │
│  │  ds_master      │    │                               │
│  │  ds_slave       │    │                               │
│  └─────────────────┘    │                               │
└─────────────────────────┴───────────────────────────────┘
```

## 主要实现步骤

### 1. ShardingSphere 数据源配置

**ShardingJdbcConfig.java** - 创建 ShardingSphere 5.x 数据源

```java
@Configuration
public class ShardingJdbcConfig {

    @Value("classpath:sharding-jdbc-main.yaml")
    private Resource shardingConfigFile;

    @Bean
    public DataSource shardingDataSource() throws SQLException, IOException {
        return YamlShardingSphereDataSourceFactory.createDataSource(shardingConfigFile.getFile());
    }
}
```

### 2. 整合动态数据源

**MyDataSourceConfiguration.java** - 将 ShardingSphere 数据源注册到动态数据源

```java
@Configuration
@AutoConfigureBefore({DynamicDataSourceAutoConfiguration.class, SpringBootConfiguration.class})
public class MyDataSourceConfiguration {

    @Lazy
    @Resource
    private DataSource shardingDataSource;

    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider() {
        return () -> {
            Map<String, DataSource> dataSourceMap = new HashMap<>(4);
            dataSourceMap.put("sharding", shardingDataSource);
            return dataSourceMap;
        };
    }

    @Primary
    @Bean
    public DataSource dataSource() {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
        dataSource.setPrimary(properties.getPrimary());
        // ...
        return dataSource;
    }
}
```

### 3. 百度 UID 数据源切换

**UidWorkerNodeDataSourceAspect.java** - 通过 AOP 切换到 uid 数据源

```java
@Aspect
@Component
public class UidWorkerNodeDataSourceAspect {

    private static final String UID_DATASOURCE_KEY = "uid";

    @Pointcut("execution(* com.baidu.fsg.uid.worker.dao.WorkerNodeDAO.*(..))")
    public void workerNodeDaoPointcut() {}

    @Around("workerNodeDaoPointcut()")
    public Object forceUidDataSource(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            DynamicDataSourceContextHolder.push(UID_DATASOURCE_KEY);
            return joinPoint.proceed();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }
}
```

## 配置文件说明

### application.yml

```yaml
spring:
  datasource:
    dynamic:
      primary: sharding  # 默认使用 sharding 数据源
      datasource:
        uid:  # 百度 uid-generator 专用数据源
          driver-class-name: com.mysql.cj.jdbc.Driver
          url: jdbc:mysql://localhost:3306/tt_master
          username: root
          password: 123456
```

### sharding-jdbc-main.yaml (ShardingSphere 5.x 格式)

```yaml
mode:
  type: Standalone
  repository:
    type: JDBC

dataSources:
  ds_master: ...
  ds_slave: ...
  ds_master_0: ...
  ds_master_1: ...

rules:
  - !SHARDING
    tables:
      t_customer:
        actualDataNodes: ds_${0..1}.t_customer
        databaseStrategy:
          standard:
            shardingColumn: id
            shardingAlgorithmName: customer_db_inline
      t_order:
        actualDataNodes: ds.t_order_${0..1}
        tableStrategy:
          standard:
            shardingColumn: customer_id
            shardingAlgorithmName: order_table_inline

  - !READWRITE_SPLITTING
    dataSources:
      ds:
        writeDataSourceName: ds_master
        readDataSourceNames: [ds_slave]
```

## 数据源切换方式

| 场景 | 数据源 | 切换方式 |
|------|--------|----------|
| 业务操作 | sharding | 默认数据源 |
| UID生成 | uid | AOP 自动切换 |

## 技术栈

- **ShardingSphere 5.4.1**: 分库分表、读写分离
- **dynamic-datasource 3.6.1**: 动态数据源管理
- **baidu uid-generator**: 分布式ID生成
- **Spring Boot 2.7.x**: 基础框架
