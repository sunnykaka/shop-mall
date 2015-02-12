package com.kariqu.common.jdk;

/**
 * User: Asion
 * Date: 12-4-19
 * Time: 上午2:58
 */
public interface MemoryInformation {

    String getName();

    long getTotal();

    long getUsed();

    long getFree();
}
