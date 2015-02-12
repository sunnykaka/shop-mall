package com.kariqu.usercenter.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 外部账号类型
 * User: Alec
 * Date: 12-12-11
 * Time: 下午3:17
 */
public enum AccountType {

    Anonymous,

    KRQ,

    QQ,

    RenRen,

    Sina,

    TaoBao,

    Alipay,

    NetEase,

    WeiXin,

    JD;

    /**
     * 客服可见
     */
    private static Map<AccountType, String> mapping = new HashMap<AccountType, String>();


    static {
        mapping.put(Anonymous, "匿名");
        mapping.put(KRQ, "喀日曲");
        mapping.put(QQ, "腾讯");
        mapping.put(RenRen, "人人网");
        mapping.put(Sina, "新浪");
        mapping.put(TaoBao, "淘宝");
        mapping.put(NetEase, "网易");
        mapping.put(Alipay, "支付宝");
        mapping.put(WeiXin, "微信");
        mapping.put(JD, "京东");
    }

    public String toDesc() {
        return mapping.get(this);
    }


}
