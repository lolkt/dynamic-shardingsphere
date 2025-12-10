package com.lolkt.demo.config;

import com.baidu.fsg.uid.impl.CachedUidGenerator;
import com.baidu.fsg.uid.worker.DisposableWorkerIdAssigner;
import com.baidu.fsg.uid.worker.WorkerIdAssigner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author zhengzy
 * @version 1.0
 * @since 2025/10/9 13:17
 */
@Configuration
public class UidGeneratorConfig {

    @Bean
    public CachedUidGenerator cachedUidGenerator(WorkerIdAssigner workerIdAssigner) {
        CachedUidGenerator cachedUidGenerator = new CachedUidGenerator();
        cachedUidGenerator.setWorkerIdAssigner(workerIdAssigner);
        // 时间戳位数
        cachedUidGenerator.setTimeBits(32);
        // 机器位数
        cachedUidGenerator.setWorkerBits(17);
        // 每毫秒生成序号位数
        cachedUidGenerator.setSeqBits(14);
        // 缓存扩容参数 (expands ring buffer size to 2^(seqBits + boostPower))
        cachedUidGenerator.setBoostPower(3);
        return cachedUidGenerator;
    }


    @Bean
    public DisposableWorkerIdAssigner disposableWorkerIdAssigner() {
        return new DisposableWorkerIdAssigner();
    }

    @Bean
    @Primary
    public WorkerIdAssigner uidWorkerIdAssigner(DisposableWorkerIdAssigner disposableWorkerIdAssigner) {
        return new UidDataSourceWorkerIdAssigner(disposableWorkerIdAssigner);
    }

}
