# dynamic-datasource-sharding

基于 `dynamic-datasource` + `ShardingSphere 5.4.1` 实现多数据源、分库分表、读写分离，并集成百度 uid-generator。

## 功能特性

- 多数据源动态切换（dynamic-datasource）
- 分库分表（ShardingSphere 5.x）
- 读写分离
- 百度 uid-generator 分布式ID生成

## 技术栈

| 组件 | 版本 |
|------|------|
| Spring Boot | 2.7.14 |
| ShardingSphere | 5.4.1 |
| dynamic-datasource | 3.6.1 |
| MyBatis-Plus | 3.5.3.2 |
| baidu uid-generator | 1.0.0-release |

## 数据源架构

```
┌─────────────────────────────────────────────────┐
│           DynamicRoutingDataSource              │
├────────────────────────┬────────────────────────┤
│    sharding (默认)      │         uid            │
│   ShardingSphere 5.x   │   百度 uid-generator   │
├────────────────────────┼────────────────────────┤
│  分库: t_customer      │                        │
│    ├─ tt_shard_01      │    uid_center 库       │
│    └─ tt_shard_02      │    WORKER_NODE 表      │
│  分表: t_order         │                        │
│    ├─ t_order_0        │                        │
│    └─ t_order_1        │                        │
│  读写分离:              │                        │
│    ├─ tt_master (写)   │                        │
│    └─ tt_slave (读)    │                        │
└────────────────────────┴────────────────────────┘
```

## 分片规则

| 表 | 分片类型 | 分片键 | 分片算法 |
|---|---------|-------|---------|
| t_customer | 分库 | id | id % 2 → ds_0/ds_1 |
| t_order | 分表 | customer_id | customer_id % 2 → t_order_0/t_order_1 |

## 核心配置

### 1. ShardingJdbcConfig.java
```java
@Bean
public DataSource shardingDataSource() throws SQLException, IOException {
    return YamlShardingSphereDataSourceFactory.createDataSource(shardingConfigFile.getFile());
}
```

### 2. MyDataSourceConfiguration.java
```java
@Bean
public DynamicDataSourceProvider dynamicDataSourceProvider() {
    return () -> {
        Map<String, DataSource> map = new HashMap<>();
        map.put("sharding", shardingDataSource);
        return map;
    };
}
```

### 3. UidWorkerNodeDataSourceAspect.java
```java
@Around("execution(* com.baidu.fsg.uid.worker.dao.WorkerNodeDAO.*(..))")
public Object forceUidDataSource(ProceedingJoinPoint joinPoint) throws Throwable {
    try {
        DynamicDataSourceContextHolder.push("uid");
        return joinPoint.proceed();
    } finally {
        DynamicDataSourceContextHolder.poll();
    }
}
```

## 验证结果

### t_order 分表
| 表 | id | customer_id | remark |
|---|---|---|---|
| t_order_0 | 3 | 647848975721660418 | veniam consequat quis |
| t_order_1 | 3 | 647847788163170309 | fugiat |

### t_customer 分库
| 库 | id | mobile | age |
|---|---|---|---|
| tt_shard_01 | 647847788163170306 | 14356405472 | 39 |
| tt_shard_01 | 647847788163170308 | 15868107405 | 63 |
| tt_shard_01 | 647848975721660418 | 16021243608 | 14 |
| tt_shard_02 | 647847788163170307 | 13725143820 | 76 |
| tt_shard_02 | 647847788163170309 | 17824752474 | 44 |
| tt_shard_02 | 647848975721660417 | 16021243608 | 14 |

## 快速开始

1. 创建数据库，执行 `doc/db/schema.sql`
2. 修改 `application.yml` 和 `sharding-jdbc-main.yaml` 中的数据库连接
3. 启动应用：`mvn spring-boot:run`

## 注意事项

- 使用百度 uid 生成的 ID 为 64 位长整型，数据库字段需使用 `bigint` 类型
- uid 数据源通过 AOP 自动切换，无需手动指定
