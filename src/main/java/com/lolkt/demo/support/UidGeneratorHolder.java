package com.lolkt.demo.support;


import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Static holder allowing infrastructure to supply the UID generator without exposing external dependencies.
 */
public final class UidGeneratorHolder {

    private static final AtomicReference<UidGenerator> DELEGATE = new AtomicReference<>();

    private UidGeneratorHolder() {
    }

    public static void register(UidGenerator generator) {
        DELEGATE.set(Objects.requireNonNull(generator, "uidGenerator must not be null"));
    }

    public static long nextId() {
        UidGenerator generator = DELEGATE.get();
        if (generator == null) {
            throw new IllegalStateException("UidGenerator has not been registered");
        }
        return generator.nextId();
    }
}
