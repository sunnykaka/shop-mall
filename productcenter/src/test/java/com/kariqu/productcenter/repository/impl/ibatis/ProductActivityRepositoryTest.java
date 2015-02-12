package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.common.DateUtils;
import com.kariqu.productcenter.domain.ProductActivity;
import com.kariqu.productcenter.domain.ProductActivityType;
import com.kariqu.productcenter.repository.ProductActivityRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import static junit.framework.Assert.*;

/**
 * User: Asion
 * Date: 13-4-1
 * Time: 下午4:46
 */
@SpringApplicationContext({"classpath:productCenter.xml"})
public class ProductActivityRepositoryTest extends UnitilsJUnit4 {


    @SpringBean(("productActivityRepository"))
    private ProductActivityRepository productActivityRepository;

    @Test
    public void testRepository() {
        ProductActivity productActivity = new ProductActivity();
        productActivity.setActivityType(ProductActivityType.LimitTime);
        productActivity.setProductId(1);
        productActivity.setActivityId(3);
        productActivity.setActivityPrice("3.00");
        productActivity.setStartDate(DateUtils.parseDate("2013-01-01 01:01:01", DateUtils.DateFormatType.DATE_FORMAT_STR));
        productActivity.setEndDate(DateUtils.parseDate("2013-01-01 01:01:05", DateUtils.DateFormatType.DATE_FORMAT_STR));
        productActivityRepository.createProductActivity(productActivity);
        assertEquals(1, productActivityRepository.getActivityCountWrapperGivingDate(1, DateUtils.parseDate("2013-01-01 01:01:01", DateUtils.DateFormatType.DATE_FORMAT_STR)).intValue());
        assertEquals(1, productActivityRepository.getActivityCountWrapperGivingDate(1, DateUtils.parseDate("2013-01-01 01:01:02", DateUtils.DateFormatType.DATE_FORMAT_STR)).intValue());
        assertEquals(1, productActivityRepository.getActivityCountWrapperGivingDate(1, DateUtils.parseDate("2013-01-01 01:01:05", DateUtils.DateFormatType.DATE_FORMAT_STR)).intValue());
        assertEquals(0, productActivityRepository.getActivityCountWrapperGivingDate(1, DateUtils.parseDate("2013-01-01 01:01:06", DateUtils.DateFormatType.DATE_FORMAT_STR)).intValue());
        assertEquals(0, productActivityRepository.getActivityCountWrapperGivingDate(1, DateUtils.parseDate("2013-01-01 01:01:00", DateUtils.DateFormatType.DATE_FORMAT_STR)).intValue());

        assertEquals(1, productActivityRepository.getActivityCountInDateRange(1, DateUtils.parseDate("2013-01-01 01:01:00", DateUtils.DateFormatType.DATE_FORMAT_STR), DateUtils.parseDate("2013-01-01 01:01:06", DateUtils.DateFormatType.DATE_FORMAT_STR)).intValue());


        assertNotNull(productActivityRepository.getProductActivityByGivingTime(1, DateUtils.parseDate("2013-01-01 01:01:02", DateUtils.DateFormatType.DATE_FORMAT_STR)));
        assertNull(productActivityRepository.getProductActivityByGivingTime(1, DateUtils.parseDate("2013-01-01 01:01:06", DateUtils.DateFormatType.DATE_FORMAT_STR)));

        assertEquals(false, productActivityRepository.getCountOfProductJoinActivityAfterCurrentTime(1) > 0);

        productActivityRepository.deleteProductActivity(1, 3);
        assertNull(productActivityRepository.getProductActivityByGivingTime(1, DateUtils.parseDate("2013-01-01 01:01:02", DateUtils.DateFormatType.DATE_FORMAT_STR)));

    }


}
