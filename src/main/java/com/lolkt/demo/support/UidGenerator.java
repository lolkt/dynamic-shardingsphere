package com.lolkt.demo.support;

/**
 * Abstraction over UID generation to decouple the domain layer from specific implementations.
 */
@FunctionalInterface
public interface UidGenerator {

    /**
     * Produce the next unique identifier.
     *
     * @return next UID
     */
    long nextId();
}
