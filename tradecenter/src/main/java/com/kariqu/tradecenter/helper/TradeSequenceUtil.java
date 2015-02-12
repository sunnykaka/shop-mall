package com.kariqu.tradecenter.helper;

import com.kariqu.common.DateUtils;

import java.util.Date;

/**
 * 交易号码生成工具
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-12-13
 *        Time: 下午5:15
 */
public class TradeSequenceUtil {

    /**
     * 得到交易流水号
     *
     * @return
     */
    public static String getTradeNo() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 生成退款批次号
     *
     * @return
     */
    public static String getRefundBatchNo() {
        return DateUtils.formatDate(new Date(), DateUtils.DateFormatType.SIMPLE_DATE_FORMAT) + System.currentTimeMillis();
    }

    /**
     * 判断退货批次號是否是今天的~
     *
     * @param batchNo
     * @return 若批次号不是今天的, 则返回 true
     */
    public static boolean checkRefundBatchNoIsNotToday(String batchNo) {
        String date = batchNo.substring(0, DateUtils.DateFormatType.SIMPLE_DATE_FORMAT.getValue().length());
        return !DateUtils.formatDate(new Date(), DateUtils.DateFormatType.SIMPLE_DATE_FORMAT).equals(date);
    }

}
