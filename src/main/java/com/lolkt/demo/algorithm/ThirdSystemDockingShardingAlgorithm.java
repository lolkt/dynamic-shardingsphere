package com.lolkt.demo.algorithm;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * 分片算法，半年分片
 *
 * @author lolkt
 * @date 2022/12/23
 */
@Slf4j
public class ThirdSystemDockingShardingAlgorithm implements PreciseShardingAlgorithm<Date>, RangeShardingAlgorithm<Date> {

    /**
     * 需要空构造方法
     */
    public ThirdSystemDockingShardingAlgorithm() {
    }

    /**
     * 时间格式
     */
    private static final String dateTimeFormat = "yyyyMM";

    /**
     * 精确分片算法类名称，用于=和IN
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Date> preciseShardingValue) {
        Date time = preciseShardingValue.getValue();
        String logicalTableName = preciseShardingValue.getLogicTableName();
        // 根据时间生成真实表名
        String realTableName = this.getRealTableName(logicalTableName, time);
        return realTableName;

    }


    /**
     * 生成真实表名
     *
     * @param logicTableName ：逻辑表名
     * @param date           ： 时间
     * @return ： 物理表名
     */
    private String getRealTableName(String logicTableName, Date date) {
        // 取得时间字段
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);
        String dateStr = sdf.format(date);
        String halfYear = dateStr.lastIndexOf(dateStr.length()) < 7 ? "01" : "02";
        // 生成真实表名
        return logicTableName + "_" + halfYear;
    }

    @Override
    public Collection<String> doSharding(Collection<String> tables, RangeShardingValue<Date> rangeShardingValue) {
        return null;
    }

}