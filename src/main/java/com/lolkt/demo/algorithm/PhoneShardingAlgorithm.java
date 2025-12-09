package com.lolkt.demo.algorithm;

import com.google.common.collect.Range;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @className: PhoneShardingAlgorithm
 * @description: 手机号的分片算法, 取后两位转Integer后取模
 * @author: lolkt
 * @create: 2023/7/25 17:17
 */
@Slf4j
public class PhoneShardingAlgorithm implements PreciseShardingAlgorithm<String>, RangeShardingAlgorithm<String> {


    /**
     * 需要空构造方法
     */
    public PhoneShardingAlgorithm() {
    }

    /**
     * 手机号截取后几位
     */
    private static final int digit = 2;

    /**
     * databaseNames 所有分片库的集合
     * shardingValue 为分片属性，其中 logicTableName 为逻辑表，columnName 分片健（字段），value 为从 SQL 中解析出的分片健的值
     */
    @Override
    public String doSharding(Collection<String> databaseNames, PreciseShardingValue<String> shardingValue) {

        if (CollectionUtils.isEmpty(databaseNames)) {
            throw new IllegalArgumentException("sharding jdbc not find logic table,please check config");
        }
        String phone = shardingValue.getValue();
        //截取字符串后两位数字
        String phoneLastTwoDigits = phone.substring(phone.length() - digit);
        //手机号末尾两位改为int类型
        Integer phoneLastTwoDigitsInt = Integer.valueOf(phoneLastTwoDigits);
        //取模
        int delivery = phoneLastTwoDigitsInt % databaseNames.size();
        // 手机号末尾两位取模
        String databaseName = databaseNames.stream()
                .filter(configTable -> configTable.contains(String.valueOf(delivery)))
                .findFirst().get();
//        return databaseNames.toArray()[phoneLastTwoDigitsInt % databaseNames.size()].toString();
        return databaseName;
    }

    @Override
    public Collection<String> doSharding(Collection<String> databaseNames, RangeShardingValue<String> shardingValue) {
        if(CollectionUtils.isEmpty(databaseNames)) {
            throw new IllegalArgumentException("sharding jdbc not find logic table,please check config");
        }
        Range<String> dateRange = shardingValue.getValueRange();

//        Date beginTime = new Date();
//        Date endTime = beginTime;
//
//        if(dateRange.hasLowerBound()){
//            beginTime = dateRange.lowerEndpoint();
//            if(dateRange.lowerBoundType().equals(BoundType.OPEN)){
//                beginTime = DateUtils.addMilliseconds(beginTime, 1);
//            }
//        }
//
//        if(dateRange.hasUpperBound()) {
//            endTime = dateRange.upperEndpoint();
//            if(dateRange.upperBoundType().equals(BoundType.OPEN)){
//                endTime = DateUtils.addMilliseconds(endTime, -1);
//            }
//        }

//        Date beginYear = DateUtils.truncate(beginTime, Calendar.YEAR);
//        Date endYear = DateUtils.truncate(endTime, Calendar.YEAR);

        Set<String> targetTables = new HashSet<>();

        String logicTableName = shardingValue.getLogicTableName();
//        while (beginYear.compareTo(endYear) <= 0) {
//
//            String target = getRealTableName(logicTableName, beginYear);
//
//            if(tables.contains(target)) {
//                targetTables.add(target);
//            }
//            beginYear = DateUtils.addYears(beginYear, 1);
//        }

        return databaseNames;
    }
}