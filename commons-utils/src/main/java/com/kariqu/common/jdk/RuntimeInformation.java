package com.kariqu.common.jdk;

import java.util.List;

/**
 * JVM的运行信息
 * User: Asion
 * Date: 12-4-19
 * Time: 上午2:59
 */
public interface RuntimeInformation {

    /**
     * 返回堆的大小
     *
     * @return
     */
    Long getTotalHeapMemory();

    /**
     * 返回使用了的堆内存
     *
     * @return
     */
    Long getTotalHeapMemoryUsed();

    /**
     * 返回代码缓存区的总内存
     *
     * @return
     */
    Long getTotalCodeCacheMemory();

    Long getTotalEdenSpaceMemory();

    Long getTotalSurvivorSpaceMemory();

    Long getTotalTenuredGenMemory();


    /**
     * 返回代码缓存区的使用内存
     *
     * @return
     */
    Long getTotalCodeCacheMemoryUsed();

    Long getTotalEdenSpaceMemoryUsed();

    Long getTotalSurvivorSpaceMemoryUsed();

    Long getTotalTenuredGenMemoryUsed();


    /**
     * 返回jvm参数
     *
     * @return
     */
    String getJvmInputArguments();

    /**
     * 内存信息列表
     *
     * @return
     */
    List<MemoryInformation> getMemoryPoolInformation();

    /**
     * 持久代的总大小
     *
     * @return
     */
    Long getTotalPermGenMemory();

    /**
     * 使用了的持久代内存
     *
     * @return
     */
    Long getTotalPermGenMemoryUsed();

    /**
     * 全部的非堆内存
     *
     * @return
     */
    Long getTotalNonHeapMemory();

    /**
     * 使用了的非堆内存
     *
     * @return
     */
    Long getTotalNonHeapMemoryUsed();
}
