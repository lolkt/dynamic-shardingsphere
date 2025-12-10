# 多数据源 + ShardingSphere 分库分表 + 读写分离 实现方案

## 概述

本项目实现了基于 `dynamic-datasource` + `ShardingSphere-JDBC` 的多数据源方案，每个数据源可独立配置分库分表和读写分离策略，并通过请求头拦截器实现动态数据源切换。

## 核心架构

```
请求 → UserHeaderInterceptor(拦截器) → DataSourceRouter(路由) 
     → DynamicRoutingDataSource(动态数据源) → ShardingSphere(分库分表/读写分离)
```

## 主要实现步骤

### 1. 配置 ShardingSphere 数据源

**ShardingJdbcConfig.java** - 创建测试数据源

```java
@Configuration
public class ShardingJdbcConfig {

    @Value("${sharding.jdbc.config.file}")
    private Resource shardingConfigFileTest;

    @Bean
    public DataSource dataSourceTest() throws SQLException, IOException {
        return YamlShardingDataSourceFactory.createDataSource(shardingConfigFileTest.getFile());
    }
}
```

通过 YAML 配置文件 `sharding-jdbc-test.yaml` 创建独立的 ShardingSphere 数据源，支持：
- 分库分表策略
- 读写分离配置

### 2. 整合动态数据源

**MyDataSourceConfiguration.java** - 将 ShardingSphere 数据源注册到动态数据源

```java
@Configuration
@AutoConfigureBefore({DynamicDataSourceAutoConfiguration.class, SpringBootConfiguration.class})
public class MyDataSourceConfiguration {

    @Lazy
    @Resource
    private DataSource shardingDataSource;  // 主数据源

    @Lazy
    @Resource
    private DataSource dataSourceTest;      // 测试数据源

    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider() {
        return new AbstractDataSourceProvider(dataSourceCreator) {
            @Override
            public Map<String, DataSource> loadDataSources() {
                Map<String, DataSource> dataSourceMap = createDataSourceMap(dataSourcePropertyMap);
                dataSourceMap.put("sharding", shardingDataSource);  // 注册主数据源
                dataSourceMap.put("test", dataSourceTest);          // 注册测试数据源
                return dataSourceMap;
            }
        };
    }
}
```

### 3. 请求拦截器实现数据源切换

**UserHeaderInterceptor.java** - 根据请求头动态切换数据源

```java
@Slf4j
@RequiredArgsConstructor
public class UserHeaderInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String headerValue = request.getHeader("isTestUser");
        
        if (StrUtil.isNotBlank(headerValue) && headerValue.equals("true")) {
            log.info("request uri:{}, isTestUser:{}", request.getRequestURI(), headerValue);
            DataSourceRouter.setTestUser(true);  // 切换到测试数据源
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        DataSourceRouter.clearTestUser();  // 清理数据源上下文
    }
}
```

### 4. 数据源路由工具

**DataSourceRouter.java** - 使用 ThreadLocal 存储数据源标识

```java
@Component
public class DataSourceRouter {
    private static final ThreadLocal<Boolean> isTestUser = new ThreadLocal<>();

    public static void setTestUser(boolean isTest) {
        isTestUser.set(isTest);
        DynamicRoutingDataSourceService bean = SpringUtils.getBean(DynamicRoutingDataSourceService.class);
        bean.push(isTest);  // 切换数据源
    }

    public static void clearTestUser() {
        isTestUser.remove();
        DynamicRoutingDataSourceService bean = SpringUtils.getBean(DynamicRoutingDataSourceService.class);
        bean.clear();  // 清理数据源上下文
    }
}
```

### 5. 动态数据源切换服务

**DynamicRoutingDataSourceServiceImpl.java** - 调用 dynamic-datasource 的 API 切换数据源

```java
@Service
public class DynamicRoutingDataSourceServiceImpl implements DynamicRoutingDataSourceService {

    @Override
    public void push(Boolean isTestUser) {
        String dsKey = isTestUser ? "test" : "sharding";
        DynamicDataSourceContextHolder.push(dsKey);  // 切换到指定数据源
    }

    @Override
    public void clear() {
        DynamicDataSourceContextHolder.clear();  // 清理上下文
    }
}
```

### 6. 注册拦截器

**WebConfig.java**

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserHeaderInterceptor());
    }
}
```

## 配置文件说明

### application.yml - 主数据源配置

```yaml
spring:
  datasource:
    dynamic:
      primary: sharding  # 默认使用 sharding 数据源
      
  shardingsphere:
    datasource:
      names: ds-master,ds-slave,ds-master-0,ds-master-1
      # 配置各数据源连接信息...
    sharding:
      # 分库分表策略...
      master-slave-rules:
        # 读写分离配置...
```

### sharding-jdbc-test.yaml - 测试数据源配置

独立的 ShardingSphere 配置文件，包含测试环境的分库分表和读写分离规则。

## 使用方式

请求时添加 Header：

```
isTestUser: true   → 使用测试数据源 (test)
isTestUser: false  → 使用主数据源 (sharding)
不传递该 Header   → 使用主数据源 (sharding)
```

## 技术栈

| 组件 | 版本 |
|------|------|
| Spring Boot | 2.7.14 |
| ShardingSphere-JDBC | 4.1.1 |
| dynamic-datasource-spring-boot-starter | 4.1.3 |
| MyBatis-Plus | 3.4.0 |
| MySQL Connector | 8.0.28 |

## 流程图

```
┌─────────────────┐
│   HTTP 请求      │
│ Header: isTestUser │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ UserHeaderInterceptor │
│   解析请求头      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  DataSourceRouter │
│  设置 ThreadLocal │
└────────┬────────┘
         │
         ▼
┌─────────────────────────┐
│ DynamicDataSourceContextHolder │
│      切换数据源上下文      │
└────────┬────────────────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
┌───────┐  ┌───────┐
│sharding│  │ test  │
│ 主数据源 │  │测试数据源│
└───┬───┘  └───┬───┘
    │         │
    ▼         ▼
┌─────────────────┐
│  ShardingSphere  │
│ 分库分表/读写分离 │
└─────────────────┘
```

# 代码

https://github.com/lolkt/dynamic-shardingsphere
