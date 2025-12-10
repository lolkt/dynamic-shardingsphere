package com.lolkt.demo.algorithm;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 基于日期的标准分片算法 (ShardingSphere 5.x)
 * 支持按年分表
 * shardingAlgorithms:
 *   date_year_sharding:
 *     type: CLASS_BASED
 *     props:
 *       strategy: STANDARD
 *       algorithmClassName: com.lolkt.demo.algorithm.ShardingAlgorithm
 *
 * @author lolkt
 */
@Slf4j
public class DateYearShardingAlgorithm implements StandardShardingAlgorithm<Date> {

    @Override
    public String getType() {
        return "DATE_YEAR_SHARDING";
    }

    /**
     * 生成真实表名
     *
     * @param logicTableName 逻辑表名
     * @param date           时间戳
     * @return 物理表名
     */
    private String getRealTableName(String logicTableName, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String dateStr = sdf.format(date);
        return logicTableName + "_" + dateStr;
    }

    /**
     * 精确分片算法，用于 = 和 IN
     *
     * @param availableTargetNames 所有配置的表列表
     * @param shardingValue        分片值
     * @return 匹配的表名
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Date> shardingValue) {
        if (CollectionUtils.isEmpty(availableTargetNames)) {
            throw new IllegalArgumentException("sharding jdbc not find logic table, please check config");
        }

        Date value = shardingValue.getValue();
        if (value == null) {
            throw new UnsupportedOperationException("preciseShardingValue is null");
        }

        String yearSuffix = new SimpleDateFormat("yyyy").format(value);
        Optional<String> optional = availableTargetNames.stream()
                .filter(tableName -> tableName.endsWith(yearSuffix))
                .findFirst();

        if (optional.isPresent()) {
            return optional.get();
        }
        throw new UnsupportedOperationException("No available tables according input time: " + yearSuffix);
    }

    /**
     * 范围分片算法，用于 BETWEEN AND, >, <, >=, <=
     *
     * @param availableTargetNames 所有配置的表列表
     * @param rangeShardingValue   范围分片值
     * @return 匹配的表名集合
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Date> rangeShardingValue) {
        if (CollectionUtils.isEmpty(availableTargetNames)) {
            throw new IllegalArgumentException("sharding jdbc not find logic table, please check config");
        }

        Range<Date> dateRange = rangeShardingValue.getValueRange();
        String logicTableName = rangeShardingValue.getLogicTableName();

        Date beginTime = new Date();
        Date endTime = beginTime;

        if (dateRange.hasLowerBound()) {
            beginTime = dateRange.lowerEndpoint();
            if (dateRange.lowerBoundType().equals(BoundType.OPEN)) {
                beginTime = DateUtils.addMilliseconds(beginTime, 1);
            }
        }

        if (dateRange.hasUpperBound()) {
            endTime = dateRange.upperEndpoint();
            if (dateRange.upperBoundType().equals(BoundType.OPEN)) {
                endTime = DateUtils.addMilliseconds(endTime, -1);
            }
        }

        Date beginYear = DateUtils.truncate(beginTime, Calendar.YEAR);
        Date endYear = DateUtils.truncate(endTime, Calendar.YEAR);

        Set<String> targetTables = new HashSet<>();

        while (beginYear.compareTo(endYear) <= 0) {
            String target = getRealTableName(logicTableName, beginYear);
            if (availableTargetNames.contains(target)) {
                targetTables.add(target);
            }
            beginYear = DateUtils.addYears(beginYear, 1);
        }

        return targetTables;
    }
}
