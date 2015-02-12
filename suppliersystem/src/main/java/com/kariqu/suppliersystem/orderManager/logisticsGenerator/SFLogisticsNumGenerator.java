package com.kariqu.suppliersystem.orderManager.logisticsGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * 顺丰物流单号生成器
 * User: amos.zhou
 * Date: 13-9-28
 * Time: 下午3:00
 */
public class SFLogisticsNumGenerator implements LogisticsNumGenerator {


    @Override
    public List<String> generateNumList(String initNo, int law, int count) throws GenerateException {
        List<String> ShunFengres = new ArrayList<String>();
        String fri, Nfri, Yuandanhao;
        int num9, num10, num11, num12, Nnum9, Nnum12;

        try {
            fri = initNo.substring(0, 11);

            ShunFengres.add(initNo);
            Yuandanhao = initNo;
            for (int i = 0; i < count - 1; i++) {
                Nfri = String.valueOf(Long.valueOf(fri) + 1);
                num9 = Integer.parseInt(Yuandanhao.substring(8, 9));
                num10 = Integer.parseInt(Yuandanhao.substring(9, 10));
                num11 = Integer.parseInt(Yuandanhao.substring(10, 11));
                num12 = Integer.parseInt(Yuandanhao.substring(11, 12));  //12位没有，就11位，添加两个变量存储原始直


                Nnum9 = Integer.parseInt(Nfri.substring(8, 9));


                if ((Nnum9 - num9 == 1) && ((num9) % (2) == 1)) {
                    if (num12 - 8 >= 0)
                        Nnum12 = num12 - 8;             // -8
                    else
                        Nnum12 = num12 - 8 + 10;
                } else if ((Nnum9 - num9 == 1) && ((num9) % (2) == 0)) {
                    if (num12 - 7 >= 0)
                        Nnum12 = num12 - 7;             // -7
                    else
                        Nnum12 = num12 - 7 + 10;
                } else {
                    if (((num10 == 3) || (num10 == 6)) && (num11 == 9)) {
                        if (num12 - 5 >= 0)
                            Nnum12 = num12 - 5;             // -5
                        else
                            Nnum12 = num12 - 5 + 10;
                    } else if (num11 == 9) {
                        if (num12 - 4 >= 0)
                            Nnum12 = num12 - 4;             // -4
                        else
                            Nnum12 = num12 - 4 + 10;
                    } else {
                        if (num12 - 1 >= 0)
                            Nnum12 = num12 - 1;           // -1
                        else
                            Nnum12 = num12 - 1 + 10;
                    }
                }
                ShunFengres.add(Nfri + String.valueOf(Nnum12));
                Yuandanhao = Nfri + String.valueOf(Nnum12);
                fri = String.valueOf(Long.valueOf(fri) + 1);

            }
        } catch (Exception e) {
            throw new GenerateException("顺丰物流单号必须是12位数字,请确保输入内容正确", e);
        }

        return ShunFengres;
    }
}
