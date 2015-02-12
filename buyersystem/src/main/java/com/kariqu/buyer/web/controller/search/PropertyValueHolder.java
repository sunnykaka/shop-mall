package com.kariqu.buyer.web.controller.search;

import com.kariqu.categorycenter.domain.util.PropertyValueUtil;

import java.util.*;

/**
 * 使用pidvid筛选时候的帮助类
 * User: Asion
 * Date: 12-5-24
 * Time: 下午1:16
 */
public class PropertyValueHolder {

    /**
     * 某次请求的所有pidvid集合，不重复
     */
    private Set<Long> pidvidList = new HashSet<Long>();

    /**
     * 每个属性被选中只有一个，用这个map来保存
     */
    private Map<Integer, Long> map = new HashMap<Integer, Long>();


    /**
     * 添加某个pidvid
     * 如果发现这个pidvid所在的属性已经被保存过，则替换掉map和pidvidlist中的值
     * 因为一个属性的值只只能在筛选计算中出现一次
     *
     * @param pv
     */
    public void addPV(long pv) {
        int pid = PropertyValueUtil.parseLongToPidVid(pv).pid;
        if (map.get(pid) != null) {
            pidvidList.remove(map.get(pid));
        }
        pidvidList.add(pv);
        map.put(pid, pv);
    }


    /**
     * 判断某个属性是否有选中
     *
     * @param propertyId
     * @return
     */
    public boolean containsPid(int propertyId) {
        return map.containsKey(propertyId);
    }

    /**
     * 判断选中的集合中是否有某个pidvid
     *
     * @param pv
     * @return
     */
    public boolean contains(long pv) {
        return pidvidList.contains(pv);
    }


    /**
     * 判断是否仅仅只包含传入的属性ID
     *
     * @return
     */
    public boolean emptyPV(int propertyId) {
        if (map.size() == 0) {
            return true;
        }
        if (map.size() == 1 && map.containsKey(propertyId)) {
            return true;
        }
        return false;
    }


    /**
     * 在已选pidvid里去掉某个属性对应的pidvid
     *
     * @param propertyId
     * @return
     */
    public String toNotLimitedString(int propertyId) {
        Long deleteLong = map.get(propertyId);
        List<Long> newList = new ArrayList<Long>();
        for (Long aLong : pidvidList) {
            if (!aLong.equals(deleteLong)) {
                newList.add(aLong);
            }
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Long aLong : newList) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(aLong);
            i++;
        }
        return sb.toString();
    }

    /**
     * 加入一个pidvid生成一个逗号分隔的pidvid串，类似：4294977296,8589944594
     *
     * @param pv
     * @return
     */
    public String toString(long pv) {
        int pid = PropertyValueUtil.parseLongToPidVid(pv).pid;
        Set<Long> newList = new HashSet<Long>();
        for (Long aLong : pidvidList) {
            newList.add(aLong);
        }
        Long p_exist = map.get(pid);
        if (p_exist != null) {
            newList.remove(p_exist);
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Long aLong : newList) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(aLong);
            i++;
        }
        if (!newList.contains(pv)) {
            if (newList.size() > 0) {
                sb.append(",");
            }
            sb.append(pv);
        }
        return sb.toString();
    }


    /**
     * 返回一个已经选中的pidvid串
     *
     * @return
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Long aLong : pidvidList) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(aLong);
            i++;
        }
        return sb.toString();
    }


}
