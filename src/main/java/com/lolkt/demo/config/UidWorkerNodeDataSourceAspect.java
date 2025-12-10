package com.lolkt.demo.config;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Ensure UID worker node mapper operations always run against the dedicated UID data source.
 */
@Slf4j
@Aspect
@Component
public class UidWorkerNodeDataSourceAspect {

    private static final String UID_DATASOURCE_KEY = "uid";

    @Pointcut("execution(* com.baidu.fsg.uid.worker.dao.WorkerNodeDAO.*(..))")
    public void workerNodeDaoPointcut() {
    }

    @Around("workerNodeDaoPointcut()")
    public Object forceUidDataSource(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            DynamicDataSourceContextHolder.push(UID_DATASOURCE_KEY);
            log.info("Switching to UID data source before executing {}", joinPoint.getSignature());
            return joinPoint.proceed();
        } finally {
            DynamicDataSourceContextHolder.clear();
            log.info("Restored previous data source after executing {}", joinPoint.getSignature());
        }
    }
}
