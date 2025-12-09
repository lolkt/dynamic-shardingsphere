package com.lolkt.demo.algorithm;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 基于日期的标准分片算法
 *
 * @author lolkt
 * @date 2022/8/18
 */
@Slf4j
public class ShardingAlgorithm implements PreciseShardingAlgorithm<Date>, RangeShardingAlgorithm<Date> {


    /**
     * 生成真实表名
     * @param logicTableName ：逻辑表名
     * @param date ： 时间戳
     * @return ： 物理表名
     */
    private String getRealTableName(String logicTableName, Date date) {
        // 取得时间字段
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String dateStr = sdf.format(date);
        // 生成真实表名
        return logicTableName + "_" + dateStr;
    }

    /**
     * 精确分片算法类名称，用于=和IN
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Date> preciseShardingValue) {

        if (!CollectionUtils.isEmpty(collection)) {
            // 根据创建时间的年份分库
            String tableName = collection.stream()
                .filter(configTable -> configTable
                    .contains(new SimpleDateFormat("yyyy").format(preciseShardingValue.getValue())))
                .findFirst().get();
            return tableName;
        } else {
            throw new IllegalArgumentException("sharding jdbc not find logic table,please check config");
        }

    }


    @Override
    public Collection<String> doSharding(Collection<String> tables, RangeShardingValue<Date> rangeShardingValue) {
        if(CollectionUtils.isEmpty(tables)) {
            throw new IllegalArgumentException("sharding jdbc not find logic table,please check config");
        }
        Range<Date> dateRange = rangeShardingValue.getValueRange();

        Date beginTime = new Date();
        Date endTime = beginTime;

        if(dateRange.hasLowerBound()){
            beginTime = dateRange.lowerEndpoint();
            if(dateRange.lowerBoundType().equals(BoundType.OPEN)){
                beginTime = DateUtils.addMilliseconds(beginTime, 1);
            }
        }

        if(dateRange.hasUpperBound()) {
            endTime = dateRange.upperEndpoint();
            if(dateRange.upperBoundType().equals(BoundType.OPEN)){
                endTime = DateUtils.addMilliseconds(endTime, -1);
            }
        }

        Date beginYear = DateUtils.truncate(beginTime, Calendar.YEAR);
        Date endYear = DateUtils.truncate(endTime, Calendar.YEAR);

        Set<String> targetTables = new HashSet<>();

        String logicTableName = rangeShardingValue.getLogicTableName();
        while (beginYear.compareTo(endYear) <= 0) {

            String target = getRealTableName(logicTableName, beginYear);

            if(tables.contains(target)) {
                targetTables.add(target);
            }
            beginYear = DateUtils.addYears(beginYear, 1);
        }

        return targetTables;
    }
}