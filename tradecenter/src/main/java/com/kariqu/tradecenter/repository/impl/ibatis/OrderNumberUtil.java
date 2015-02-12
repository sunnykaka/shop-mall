package com.kariqu.tradecenter.repository.impl.ibatis;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用 getOrderNo() 生成订单编号<br/>
 * 由多个部分组成, 且受限于 Long 的最大值 &lt;9223372036854775807&gt;<br/><br/>
 * btw: 调用此方法之前, 查询一下数据库的最大值并使用 setIncrementNum 进行赋值, 避免因为系统重启而导致订单号重复无法生成订单的错误.<br/><br/>
 *
 * <span style="color:red;">btw:如果在集群模式下, 要更改新的规则, 如配置机器编号等.</span>
 *
 * <br/>亦可以使用 SQL 中的自定义 getOrderNo() 函数进行数据库获取.
 *
 * create by Athens(刘杰) on 2012-08-22 15:15
 */
public class OrderNumberUtil {

    // >>>>> 以下参数需设置合理 <<<<<

    /** 计数的初始值 */
    private static final int INIT_INCREMENT_NUM = 21;

    /** 计数器的增长量 */
    private static final int STEP_NUM = 13;

    // >>>>>

    /** 前缀, 年月日格式 */
    private static final String PREFIX_PATTERN = "yyyyMMdd";

    /** 自增时需要用到锁. */
    private static final ReentrantLock LOCK = new ReentrantLock();

    /** 自增计数器. */
    private static AtomicInteger incrementNum = new AtomicInteger(INIT_INCREMENT_NUM);

    /** 自增的最大值(亿以内) */
    private static final int MAX_INCREMENT_NUM = 100000000;

    /** 机器码 */
    private static AtomicInteger machineCode = new AtomicInteger(18);

    /** 赋值自增计数器(若需要每天的订单都从 0 开始, 则有必要建一个调度器, 每天凌晨将此值变更为 0) */
    public static void setIncrementNum(int incrementNum) {
        LOCK.lock();
        try {
            // 赋值也必须保证同步
            OrderNumberUtil.incrementNum.set(incrementNum);
        } finally {
            LOCK.unlock();
        }
    }

    /** 使用订单编号赋值计数器, 截取字符串的过程在此处完成! 若在别处编号规则有可能会变化 */
    public static void setIncrementNum(String orderNo) {
        LOCK.lock();
        try {
            if (StringUtils.isBlank(orderNo))
                incrementNum.set(INIT_INCREMENT_NUM);
            else if (orderNo.length() < PREFIX_PATTERN.length())
                incrementNum.set(NumberUtils.toInt(orderNo));
            else
                // 传入订单编号赋值给 计数器, 截取后面的数字放在此处进行操作.
                incrementNum.set(NumberUtils.toInt(orderNo.substring(PREFIX_PATTERN.length())));
        } finally {
            LOCK.unlock();
        }
    }

    /** 判断自增数是否是初始值, 若是初始值, 则需要去数据库查询并重新为初始值赋值. */
    public static boolean isInitNum() {
        LOCK.lock();
        try {
            return (incrementNum.get() == INIT_INCREMENT_NUM);
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 订单号由多部分组成, 受限于 Long.MAX_VALUE, 只能有 19 位.<br/>
     * 1. 年月日, 8 位. <br/>
     * 2. 自增数, 最多 8 位. <br/>
     * 3. 机器码, 2 位. <br/>
     */
    public static long getOrderNo() {
        return NumberUtils.toLong(getStringFromNow() + lockNum() + machineCode);
    }

    /** 生成自增数. 当达到了最大数, 则又从 初始值 开始计数. */
    private static int lockNum() {
        LOCK.lock();
        try {
            // 如果达到了最大数, 则又从 初始值 开始计数.
            if (incrementNum.get() >= MAX_INCREMENT_NUM) {
                incrementNum.set(INIT_INCREMENT_NUM);
                machineCode.addAndGet(STEP_NUM);
            }

            incrementNum.addAndGet(STEP_NUM);
        } finally {
            LOCK.unlock();
        }
        return incrementNum.get();
    }

    /**
     * 年月日, 异常则返回 空字符串
     */
    public static String getStringFromNow() {
        try {
            return new SimpleDateFormat(PREFIX_PATTERN).format(new Date());
        } catch (Exception e) {
            return "";
        }
    }

}
