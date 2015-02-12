package com.kariqu.tradecenter.payment;

import com.kariqu.tradecenter.payment.alipay.AliPayRequestHandler;
import com.kariqu.tradecenter.payment.tenpay.TenPayRequestHandler;
import com.kariqu.tradecenter.service.impl.PayMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * User: amos.zhou
 * Date: 13-10-22
 * Time: 下午2:55
 */
public class PaymentManager {

    private static Map<PayMethod,Class<? extends PayRequestHandler>> payServiceMap = new HashMap<PayMethod,Class<? extends PayRequestHandler>>();

    /**
     *将默认的方式初始化管理，并提供  addPayService（）以方便运时添加
     */
    static{
        //支付宝，实现类
        addPayService(PayMethod.directPay, AliPayRequestHandler.class);
        //银行，目前也是通过支付宝来实现
        addPayService(PayMethod.bankPay, AliPayRequestHandler.class);
         //财富通，实现类
        addPayService(PayMethod.Tenpay, TenPayRequestHandler.class);
    }

    /**
     * 得到支付实例
     * @param payMethod
     * @return
     */
    public static PayRequestHandler getPayRequestHandler(PayMethod payMethod){
        try {
            return payServiceMap.get(payMethod).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 提供的对外接口,以方便运行时添加
     * @param payMethod
     * @param payService
     */
    public static void addPayService(PayMethod payMethod,Class<? extends PayRequestHandler> payService){
        payServiceMap.put(payMethod,payService);
    }

}
