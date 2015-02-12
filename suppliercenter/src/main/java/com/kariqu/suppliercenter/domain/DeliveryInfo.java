package com.kariqu.suppliercenter.domain;

/**
 * 配送信息
 * 包括配送类型和配送时间
 * 可能产生运费
 * User: Asion
 * Date: 11-10-11
 * Time: 下午1:13
 */
public class DeliveryInfo {

    private DeliveryType deliveryType = DeliveryType.ems;

    private DeliveryTime deliveryTime = DeliveryTime.WorkingDay_Holiday;

    /**
     * 物流单号
     */
    private String waybillNumber;

    /**
     * 运费
     */
    private String cost = "0.00";

    /**
     * 配送类型
     */
    public static enum DeliveryType {
        shunfeng("顺丰"),
        zhongtong("中通"),
        yunda("韵达"),
        zhaijisong("宅急送"),

        ems("ems"),
        yuantong("圆通"),
        shentong("申通"),
        quanritongkuaidi("全日通"),

        kuaijiesudi("快捷"),
        huitongkuaidi("汇通"),
        guotongkuaidi("国通"),
        lianbangkuaidi("联邦"),

        quanfengkuaidi("全峰"),
        suer("速尔"),
        tiantian("天天"),
        youshuwuliu("优速"),

        unknown("未知");

        private String value;
        DeliveryType(String value) {
            this.value = value;
        }

        public String toDesc() {
            return value;
        }
    }

    /**
     * 送货日期类型
     */
    public static enum DeliveryTime {
        /** 工作日送货 */
        WorkingDay("只工作日送货（双休日、假日不送）"),
        /** 假期送货 */
        Holiday("只双休日、假日送货（工作日不配送）"),
        /** 工作日和假期都可送货 */
        WorkingDay_Holiday("工作日、双休日均可配送");

        private String value;
        DeliveryTime(String value) {
            this.value = value;
        }

        public String toDesc() {
            return value;
        }
    }


    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public DeliveryTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(DeliveryTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber;
    }
}
