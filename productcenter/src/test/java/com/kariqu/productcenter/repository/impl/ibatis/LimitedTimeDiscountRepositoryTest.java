package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.common.DateUtils;
import com.kariqu.productcenter.domain.LimitedTimeDiscount;
import com.kariqu.productcenter.query.LimitedTimeDiscountQuery;
import com.kariqu.productcenter.repository.LimitedTimeDiscountRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * User: Athens
 */
@SpringApplicationContext({"classpath:productCenter.xml"})
public class LimitedTimeDiscountRepositoryTest extends UnitilsJUnit4 {

    @SpringBean(("limitedTimeDiscountRepository"))
    private LimitedTimeDiscountRepository limitedTimeDiscountRepository;

    @Test
    public void testRepository() {
        int productId = 12;
        long skuId = 120, price = 15000;

        LimitedTimeDiscount limitedTimeDiscount = new LimitedTimeDiscount();
        limitedTimeDiscount.setProductId(productId);
        limitedTimeDiscount.setDiscountType(LimitedTimeDiscount.DiscountType.Ratio);
        limitedTimeDiscount.setDiscount(20);
        limitedTimeDiscount.setBeginDate(DateUtils.parseDate("2013-01-01 01:00:00", DateUtils.DateFormatType.DATE_FORMAT_STR));
        limitedTimeDiscount.setEndDate(DateUtils.parseDate("2013-05-01 01:00:00", DateUtils.DateFormatType.DATE_FORMAT_STR));

        List<LimitedTimeDiscount.SkuDetail> skuDetailList = new ArrayList<LimitedTimeDiscount.SkuDetail>();
        LimitedTimeDiscount.SkuDetail skuDetail = new LimitedTimeDiscount.SkuDetail();
        skuDetail.setSkuId(110);
        skuDetail.setSkuPrice(13500);
        skuDetailList.add(skuDetail);

        LimitedTimeDiscount.SkuDetail skuDetail2 = new LimitedTimeDiscount.SkuDetail();
        skuDetail2.setSkuId(skuId);
        skuDetail2.setSkuPrice(1500);
        skuDetailList.add(skuDetail2);
        limitedTimeDiscount.setSkuDetailsJsonByDetail(skuDetailList);

        limitedTimeDiscountRepository.insert(limitedTimeDiscount);

        assertEquals(1, limitedTimeDiscountRepository.select(new LimitedTimeDiscountQuery()).getResult().size());
        assertEquals(1, limitedTimeDiscountRepository.selectByProductId(productId).size());
        assertEquals(0, limitedTimeDiscountRepository.selectByProductId(4578).size());

        LimitedTimeDiscount discount = selectByIdAndTime(productId, "2013-02-02 11:57:50");
        // 有数据
        assertEquals(20, discount.getDiscount());
        // 无数据
        assertEquals(null, selectByIdAndTime(productId, "2012-02-02 11:57:50"));

        List<LimitedTimeDiscount.SkuDetail> skuDetails = new ArrayList<LimitedTimeDiscount.SkuDetail>();
        for (LimitedTimeDiscount.SkuDetail detail : discount.json2Details()) {
            if (skuId == detail.getSkuId()) {
                detail.setSkuPrice(price);
            }
            skuDetails.add(detail);
        }
        discount.setSkuDetailsJsonByDetail(skuDetails);
        limitedTimeDiscountRepository.update(discount);

        LimitedTimeDiscount changeDiscount = selectByIdAndTime(productId, "2013-02-02 11:57:50");
        assertEquals(20, changeDiscount.getDiscount());

        long testPrice = 0;
        for (LimitedTimeDiscount.SkuDetail detail : changeDiscount.json2Details()) {
            if (skuId == detail.getSkuId()) {
                testPrice = detail.getSkuPrice();
            }
        }
        assertEquals(price, testPrice);
    }

    private LimitedTimeDiscount selectByIdAndTime(int productId, String date) {
        return limitedTimeDiscountRepository.selectByProductIdAndTime(productId,
                DateUtils.parseDate(date, DateUtils.DateFormatType.DATE_FORMAT_STR));
    }


}
