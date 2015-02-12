package com.kariqu.common.jdk;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * User: Asion
 * Date: 12-4-19
 * Time: 上午3:03
 */
public class RuntimeInformationBean
        implements RuntimeInformation {
    private static final Logger log = Logger.getLogger(RuntimeInformationBean.class.getName());

    private final MemoryMXBean memoryBean;
    private final RuntimeMXBean runtimeBean;

    public RuntimeInformationBean() {
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.runtimeBean = ManagementFactory.getRuntimeMXBean();
    }

    public Long getTotalHeapMemory() {
        return this.memoryBean.getHeapMemoryUsage().getMax();
    }

    public Long getTotalHeapMemoryUsed() {
        return this.memoryBean.getHeapMemoryUsage().getUsed();
    }

    @Override
    public Long getTotalCodeCacheMemory() {
        return getCodeCache().getTotal();
    }

    @Override
    public Long getTotalCodeCacheMemoryUsed() {
        return getCodeCache().getUsed();
    }

    public List<MemoryInformation> getMemoryPoolInformation() {
        List<MemoryPoolMXBean> mxBeans = ManagementFactory.getMemoryPoolMXBeans();
        List result = new ArrayList(mxBeans.size());
        for (MemoryPoolMXBean mxBean : mxBeans) {
            result.add(new MemoryInformationBean(mxBean));
        }
        return Collections.unmodifiableList(result);
    }

    public Long getTotalPermGenMemory() {
        return getPermGen().getTotal();
    }

    public Long getTotalPermGenMemoryUsed() {
        return getPermGen().getUsed();
    }

    public Long getTotalNonHeapMemory() {
        return this.memoryBean.getNonHeapMemoryUsage().getMax();
    }

    public Long getTotalNonHeapMemoryUsed() {
        return this.memoryBean.getNonHeapMemoryUsage().getUsed();
    }

    public String getJvmInputArguments() {
        StringBuilder sb = new StringBuilder();
        for (String argument : this.runtimeBean.getInputArguments()) {
            sb.append(argument).append(" ");
        }
        return sb.toString();
    }

    private MemoryInformation getCodeCache() {
        for (MemoryInformation info : getMemoryPoolInformation()) {
            String name = info.getName().toLowerCase();
            if (name.contains("code cache")) {
                return info;
            }
        }
        return new MemoryInformation() {
            public String getName() {
                return "";
            }

            public long getTotal() {
                return -1L;
            }

            public long getUsed() {
                return -1L;
            }

            public long getFree() {
                return -1L;
            }
        };
    }

    @Override
    public Long getTotalEdenSpaceMemory() {
        return getEdenSpace().getTotal();
    }

    @Override
    public Long getTotalSurvivorSpaceMemory() {
        return getSurvivorSpace().getTotal();
    }

    @Override
    public Long getTotalTenuredGenMemory() {
        return getTenuredGen().getTotal();
    }

    @Override
    public Long getTotalEdenSpaceMemoryUsed() {
        return getEdenSpace().getUsed();
    }

    @Override
    public Long getTotalSurvivorSpaceMemoryUsed() {
        return getSurvivorSpace().getUsed();
    }

    @Override
    public Long getTotalTenuredGenMemoryUsed() {
        return getTenuredGen().getUsed();
    }

    private MemoryInformation getEdenSpace() {
        for (MemoryInformation info : getMemoryPoolInformation()) {
            String name = info.getName().toLowerCase();
            if (name.contains("eden space")) {
                return info;
            }
        }
        return new MemoryInformation() {
            public String getName() {
                return "";
            }

            public long getTotal() {
                return -1L;
            }

            public long getUsed() {
                return -1L;
            }

            public long getFree() {
                return -1L;
            }
        };
    }

    private MemoryInformation getSurvivorSpace() {
        for (MemoryInformation info : getMemoryPoolInformation()) {
            String name = info.getName().toLowerCase();
            if (name.contains("survivor space")) {
                return info;
            }
        }
        return new MemoryInformation() {
            public String getName() {
                return "";
            }

            public long getTotal() {
                return -1L;
            }

            public long getUsed() {
                return -1L;
            }

            public long getFree() {
                return -1L;
            }
        };
    }

    private MemoryInformation getTenuredGen() {
        for (MemoryInformation info : getMemoryPoolInformation()) {
            String name = info.getName().toLowerCase();
            if (name.contains("tenured gen")) {
                return info;
            }
        }
        return new MemoryInformation() {
            public String getName() {
                return "";
            }

            public long getTotal() {
                return -1L;
            }

            public long getUsed() {
                return -1L;
            }

            public long getFree() {
                return -1L;
            }
        };
    }

    private MemoryInformation getPermGen() {
        for (MemoryInformation info : getMemoryPoolInformation()) {
            String name = info.getName().toLowerCase();
            if (name.contains("perm gen")) {
                return info;
            }
        }
        return new MemoryInformation() {
            public String getName() {
                return "";
            }

            public long getTotal() {
                return -1L;
            }

            public long getUsed() {
                return -1L;
            }

            public long getFree() {
                return -1L;
            }
        };
    }

    /**
     * 堆：年轻代+年老代
     * 持久代
     * 代码缓存区：32M?
     *
     * @param args
     */

    public static void main(String[] args) {
        RuntimeInformationBean runtimeInformationBean = new RuntimeInformationBean();
        log.info("JVM启动参数:" + runtimeInformationBean.getJvmInputArguments());
        log.info(runtimeInformationBean.getMemoryPoolInformation().toString());
        log.info("堆的大小:" + runtimeInformationBean.getTotalHeapMemory() / 1048576L + "M");
        log.info("堆的使用:" + runtimeInformationBean.getTotalHeapMemoryUsed() / 1048576L + "M");
        log.info("代码缓存区:" + runtimeInformationBean.getTotalCodeCacheMemory() / 1048576L + "M");
        log.info("代码缓存区使用:" + runtimeInformationBean.getTotalCodeCacheMemoryUsed() / 1048576L + "M");
        log.info("伊甸园大小:" + runtimeInformationBean.getTotalEdenSpaceMemory() / 1048576L + "M");
        log.info("伊甸园使用:" + runtimeInformationBean.getTotalCodeCacheMemoryUsed() / 1048576L + "M");
        log.info("伊甸园交换大小:" + runtimeInformationBean.getTotalSurvivorSpaceMemory() / 1048576L + "M");
        log.info("伊甸园交换使用:" + runtimeInformationBean.getTotalSurvivorSpaceMemoryUsed() / 1048576L + "M");
        log.info("年老代大小:" + runtimeInformationBean.getTotalTenuredGenMemory() / 1048576L + "M");
        log.info("年老代使用:" + runtimeInformationBean.getTotalTenuredGenMemoryUsed() / 1048576L + "M");
        log.info("非堆内存:" + runtimeInformationBean.getTotalNonHeapMemory() / 1048576L + "M");
        log.info("非堆内存使用:" + runtimeInformationBean.getTotalNonHeapMemoryUsed() / 1048576L + "M");
        log.info("持久代:" + runtimeInformationBean.getTotalPermGenMemory() / 1048576L + "M");
        log.info("持久代使用:" + runtimeInformationBean.getTotalPermGenMemoryUsed() / 1048576L + "M");
    }
}
