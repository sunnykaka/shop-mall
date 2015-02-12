package com.kariqu.suppliersystem.orderManager.logisticsGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Ems物流单号生成器
 * User: amos.zhou
 * Date: 13-9-28
 * Time: 下午2:59
 */
public class EMSLogisticsNumGenerator implements  LogisticsNumGenerator {

    @Override
    public List<String> generateNumList(String initNo, int law, int count) throws GenerateException {
        List<String> EMSres = new ArrayList<String>();
        String fri;
        int res, num3, num4, num5, num6, num7, num8, num9, num0, mid;
        try{
        fri = initNo.substring(2, 10);
        for (int i = 0; i < count; i++) {
            num3 = Integer.parseInt(fri.substring(0, 1));
            num4 = Integer.parseInt(fri.substring(1, 2));
            num5 = Integer.parseInt(fri.substring(2, 3));
            num6 = Integer.parseInt(fri.substring(3, 4));
            num7 = Integer.parseInt(fri.substring(4, 5));
            num8 = Integer.parseInt(fri.substring(5, 6));
            num9 = Integer.parseInt(fri.substring(6, 7));
            num0 = Integer.parseInt(fri.substring(7, 8));
            mid = 8 * num3 + 6 * num4 + 4 * num5 + 2 * num6 + 3 * num7 + 5 * num8 + 9 * num9 + 7 * num0;
            res = 11 - (mid) % (11);
            if (res == 10)
                res = 0;
            if (res == 11)
                res = 5;
            //1、此处长度不足时前面需要根据情况补0  2、起始的EC 和CS请自行截取后拼接
            EMSres.add(initNo.substring(0, 2) + fri + String.valueOf(res) + initNo.substring(initNo.length() - 2, initNo.length()));
            fri = String.valueOf(Long.valueOf(fri) + 1);
        }
        }catch(Exception e){
            throw new GenerateException("EMS物流单号必须是13位数字,请确保输入内容正确", e);
        }
        return EMSres;
    }
}
