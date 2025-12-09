package com.lolkt.demo.algorithm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.util.Collection;

/**
 * 基于日期的标准分片算法
 *
 * @author lolkt
 * @date 2022/8/18
 */
@Slf4j
public class StringShardingAlgorithm implements PreciseShardingAlgorithm<String>, RangeShardingAlgorithm<String> {

    /**
     * 精确分片算法类名称，用于=和IN
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<String> preciseShardingValue) {

        if (!CollectionUtils.isEmpty(collection)) {
            return collection.toArray()[(preciseShardingValue.getValue().hashCode() &
                    Integer.MAX_VALUE)% collection.size()].toString();
        } else {
            throw new IllegalArgumentException("sharding jdbc not find logic table,please check config");
        }

    }


    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<String> rangeShardingValue) {
        return null;
    }
}