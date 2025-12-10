package com.lolkt.demo.algorithm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;

/**
 * 基于字符串 Hash 取模的分片算法 (ShardingSphere 5.x)
 * 对字符串字段先 hash 再按 3 取模
 *
 * shardingAlgorithms:
 *   string_hash_mod:
 *     type: CLASS_BASED
 *     props:
 *       strategy: STANDARD
 *       algorithmClassName: com.lolkt.demo.algorithm.StringHashShardingAlgorithm
 *       sharding-count: 3
 *
 * @author lolkt
 */
@Slf4j
public class StringHashShardingAlgorithm implements StandardShardingAlgorithm<String> {

    private static final int DEFAULT_SHARDING_COUNT = 3;

    private int shardingCount = DEFAULT_SHARDING_COUNT;

    @Override
    public void init(Properties props) {
        if (props != null && props.containsKey("sharding-count")) {
            shardingCount = Integer.parseInt(props.getProperty("sharding-count"));
        }
    }

    @Override
    public String getType() {
        return "STRING_HASH_MOD";
    }

    /**
     * 计算分片后缀
     *
     * @param value 分片键值
     * @return 分片后缀 (0, 1, 2)
     */
    private int getShardingSuffix(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        // 使用 hashCode 的绝对值取模
        int hashCode = Math.abs(value.hashCode());
        return hashCode % shardingCount;
    }

    /**
     * 精确分片算法，用于 = 和 IN
     *
     * @param availableTargetNames 所有配置的表/库列表
     * @param shardingValue        分片值
     * @return 匹配的表/库名
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
        if (CollectionUtils.isEmpty(availableTargetNames)) {
            throw new IllegalArgumentException("sharding jdbc not find logic table, please check config");
        }

        String value = shardingValue.getValue();
        int suffix = getShardingSuffix(value);

        log.debug("StringHashShardingAlgorithm: value={}, hash={}, suffix={}", 
                value, value != null ? value.hashCode() : 0, suffix);

        Optional<String> optional = availableTargetNames.stream()
                .filter(name -> name.endsWith(String.valueOf(suffix)))
                .findFirst();

        if (optional.isPresent()) {
            return optional.get();
        }

        throw new UnsupportedOperationException(
                "No available target for value: " + value + ", suffix: " + suffix);
    }

    /**
     * 范围分片算法，用于 BETWEEN AND, >, <, >=, <=
     * 字符串 Hash 无法进行范围查询优化，返回所有分片
     *
     * @param availableTargetNames 所有配置的表/库列表
     * @param rangeShardingValue   范围分片值
     * @return 所有分片（无法优化）
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<String> rangeShardingValue) {
        if (CollectionUtils.isEmpty(availableTargetNames)) {
            throw new IllegalArgumentException("sharding jdbc not find logic table, please check config");
        }

        // 字符串 Hash 无法进行范围优化，返回所有分片
        log.warn("StringHashShardingAlgorithm: range sharding not supported, returning all targets");
        return new HashSet<>(availableTargetNames);
    }
}
