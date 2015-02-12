package com.kariqu.categorycenter.domain.util;

import java.util.*;

/**
 * 封装pdivid列表，这个类也用于转换到json
 * 数据结构采用有序列表和有序map保证顺序
 * User: Asion
 * Date: 11-7-19
 * Time: 下午3:18
 */
public class PidVid {

    private List<Long> pidvid = new ArrayList<Long>();  //全部pidvid

    private Map<Integer, Long> singlePidVidMap = new LinkedHashMap<Integer, Long>(); //单值的pidvid

    private Map<Integer, List<Long>> multiPidVidMap = new LinkedHashMap<Integer, List<Long>>(); //多值的pidvid

    /**
     * 加入pid,vid，并且指定是否多值
     * @param pid
     * @param vid
     * @param multiProperty
     */
    public void add(int pid, int vid, boolean multiProperty) {
        long pv = PropertyValueUtil.mergePidVidToLong(pid, vid);
        pidvid.add(pv);
        if (multiProperty) {
            List<Long> pidvids = multiPidVidMap.get(pid);
            if (pidvids == null) {
                pidvids = new LinkedList<Long>();
                pidvids.add(pv);
                multiPidVidMap.put(pid, pidvids);
            } else {
                pidvids.add(pv);
            }
        } else {
            singlePidVidMap.put(pid, pv);
        }
    }

    public List<Long> readMultiPv(int pid) {//读取多值pidvid
        return multiPidVidMap.get(pid);
    }

    public long readSinglePv(int pid) { //读单值的pidvid
        return singlePidVidMap.get(pid);
    }

    public int size() {
        return pidvid.size();
    }

    public boolean checkEmpty() {
        return pidvid.isEmpty();
    }

    //被json工具读取
    public List<Long> getPidvid() {
        return pidvid;
    }

    public void setPidvid(List<Long> pidvid) {
        this.pidvid = pidvid;
    }

    public Map<Integer, Long> getSinglePidVidMap() {
        return singlePidVidMap;
    }

    public void setSinglePidVidMap(Map<Integer, Long> singlePidVidMap) {
        this.singlePidVidMap = singlePidVidMap;
    }

    public Map<Integer, List<Long>> getMultiPidVidMap() {
        return multiPidVidMap;
    }

    public void setMultiPidVidMap(Map<Integer, List<Long>> multiPidVidMap) {
        this.multiPidVidMap = multiPidVidMap;
    }
}
