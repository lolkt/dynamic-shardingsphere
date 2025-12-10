package com.lolkt.demo.config;

import com.baidu.fsg.uid.worker.DisposableWorkerIdAssigner;
import com.baidu.fsg.uid.worker.WorkerIdAssigner;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;

/**
 * Ensures worker-id registration for UID uses the dedicated UID data source.
 */
public class UidDataSourceWorkerIdAssigner implements WorkerIdAssigner {

    private static final String UID_DATASOURCE_KEY = "uid";

    private final DisposableWorkerIdAssigner delegate;

    public UidDataSourceWorkerIdAssigner(DisposableWorkerIdAssigner delegate) {
        this.delegate = delegate;
    }

    @Override
    public long assignWorkerId() {
        DynamicDataSourceContextHolder.push(UID_DATASOURCE_KEY);
        try {
            return delegate.assignWorkerId();
        } finally {
            DynamicDataSourceContextHolder.clear();
        }
    }
}
