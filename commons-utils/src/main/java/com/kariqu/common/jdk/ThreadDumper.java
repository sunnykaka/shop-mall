package com.kariqu.common.jdk;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 线程dump器
 * User: Asion
 * Date: 12-4-19
 * Time: 上午2:52
 */
public class ThreadDumper {
    private static final Logger log = Logger.getLogger(ThreadDumper.class.getName());

    void printThreadDump() {
        Map stackTraces = Thread.getAllStackTraces();
        long timeStamp = System.currentTimeMillis();
        log.info("************* Start Thread Dump " + timeStamp + " *******************");
        log.info(" -- Memory Details --");
        long totalMemory = Runtime.getRuntime().totalMemory() / 1048576L;
        log.info("Total Memory = " + totalMemory + "MB");
        long freeMemory = Runtime.getRuntime().freeMemory() / 1048576L;
        log.info("Used Memory = " + (totalMemory - freeMemory) + "MB");
        log.info("Free Memory = " + freeMemory + "MB");
        log.info(" --- --- --- ---");
        for (Iterator iterator = stackTraces.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Thread thread = (Thread) entry.getKey();
            StackTraceElement[] stackTraceElement = (StackTraceElement[]) (StackTraceElement[]) entry.getValue();
            log.info("Thread= " + thread.getName() + " " + ((thread.isDaemon()) ? "daemon" : "") + " prio=" + thread.getPriority() + "id=" + thread.getId() + " " + thread.getState());
            for (int i = 0; i <= stackTraceElement.length - 1; ++i) {
                log.info("\t" + stackTraceElement[i]);
            }
            log.info(" --- --- --- ---");
        }
        log.info("************* End Thread Dump " + timeStamp + " *******************");
    }

    public static void main(String[] args) {
        ThreadDumper threadDumper = new ThreadDumper();
        threadDumper.printThreadDump();
    }

}
