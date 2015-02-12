package com.kariqu.om.util;

import com.kariqu.common.jdk.RuntimeInformationBean;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-9-4
 *        Time: 上午10:29
 */
public class SystemMonitor {

    /**
     * 搜集jvm信息
     *
     * @return
     */
    public static Map<String, String> gatherJvmInfo() {
        Map<String, String> info = new HashMap<String, String>();
        RuntimeInformationBean runtimeInformationBean = new RuntimeInformationBean();
        info.put("jvm", runtimeInformationBean.getJvmInputArguments());

        info.put("heap", runtimeInformationBean.getTotalHeapMemory() / 1048576L + "M");
        info.put("heapUsed", runtimeInformationBean.getTotalHeapMemoryUsed() / 1048576L + "M");

        info.put("codeCache", runtimeInformationBean.getTotalCodeCacheMemory() / 1048576L + "M");
        info.put("codeCacheUsed", runtimeInformationBean.getTotalCodeCacheMemoryUsed() / 1048576L + "M");

        info.put("eden", runtimeInformationBean.getTotalEdenSpaceMemory() / 1048576L + "M");
        info.put("edenUsed", runtimeInformationBean.getTotalEdenSpaceMemoryUsed() / 1048576L + "M");

        info.put("survivor", runtimeInformationBean.getTotalSurvivorSpaceMemory() / 1048576L + "M");
        info.put("survivorUsed", runtimeInformationBean.getTotalSurvivorSpaceMemoryUsed() / 1048576L + "M");

        info.put("tenured", runtimeInformationBean.getTotalTenuredGenMemory() / 1048576L + "M");
        info.put("tenuredUsed", runtimeInformationBean.getTotalTenuredGenMemoryUsed() / 1048576L + "M");

        info.put("perm", runtimeInformationBean.getTotalPermGenMemory() / 1048576L + "M");
        info.put("permUsed", runtimeInformationBean.getTotalPermGenMemoryUsed() / 1048576L + "M");

        info.put("nonHeap", runtimeInformationBean.getTotalNonHeapMemory() / 1048576L + "M");
        info.put("nonHeapUsed", runtimeInformationBean.getTotalNonHeapMemoryUsed() / 1048576L + "M");
        return info;
    }
}
