package com.kariqu.suppliersystem.orderManager.logisticsGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * 支持批量自增的快递物流单号生成器
 * User: amos.zhou
 * Date: 13-9-28
 * Time: 下午3:02
 */
public class AutoIncreaseLogisticsNumGenerator implements LogisticsNumGenerator {


    @Override
    public List<String> generateNumList(String initNo, int law, int count) throws GenerateException {

        List<String> initNos = new ArrayList<String>();
        try {
            boolean hasMax = false;  //判断叠加之后是否大于9
            initNos.add(initNo);
            for (int j = 0; j < count - 1; j++) {
                String waybill = "";
                for (int i = initNo.length() - 1; i >= 0; i--) {
                    char way = initNo.charAt(i);
                    int num = Integer.valueOf(String.valueOf(way));
                    if (hasMax) {
                        num = num + 1;
                    }

                    if (i == initNo.length() - 1) {
                        num = num + law;
                    }

                    if (num > 9 && i == 0) {              //当运行到最后一位叠加还大于9就增长一位
                        waybill = "1" + String.valueOf(num % 10) + waybill;
                    } else if (num > 9) {
                        hasMax = true;
                        waybill = String.valueOf(num % 10) + waybill;
                    } else {
                        hasMax = false;
                        waybill = num + waybill;
                    }
                }
                initNos.add(waybill);
                initNo = waybill;
            }
        } catch (Exception e) {
           throw new  GenerateException("物流单号是数字串",e);
        }
        return initNos;
    }
}
