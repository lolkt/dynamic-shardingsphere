package com.lolkt.demo.config;

import com.baidu.fsg.uid.impl.CachedUidGenerator;
import com.lolkt.demo.support.UidGeneratorHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Bridges the UID generator from infrastructure into the domain holder.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UidGeneratorRegistrar {

    private final CachedUidGenerator cachedUidGenerator;

    @PostConstruct
    public void registerGenerator() {
        UidGeneratorHolder.register(cachedUidGenerator::getUID);
        log.info("Registered CachedUidGenerator for domain UID generation");
    }
}
