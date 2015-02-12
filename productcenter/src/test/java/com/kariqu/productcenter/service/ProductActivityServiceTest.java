package com.kariqu.productcenter.service;

import com.kariqu.productcenter.repository.ProductActivityRepository;
import com.kariqu.productcenter.service.impl.ProductActivityServiceImpl;
import org.easymock.EasyMock;
import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

/**
 * User: Asion
 * Date: 13-4-7
 * Time: 下午5:11
 */
public class ProductActivityServiceTest  {

    private ProductActivityServiceImpl productActivityService;

    @Test
    public void testService() {
        ProductActivityRepository productActivityRepository = EasyMock.createMock(ProductActivityRepository.class);
        Date start = new Date(2222);
        Date end = new Date(9999);
        productActivityService = new ProductActivityServiceImpl();
        productActivityService.setProductActivityRepository(productActivityRepository);
        expect(productActivityRepository.getActivityCountWrapperGivingDate(1, start)).andReturn(1);
        replay(productActivityRepository);
        boolean fail = false;
        try {
            productActivityService.checkProductActivityAssign(1, start, end);
        } catch (ProductActivityException e) {
            fail = true;
        }
        assertTrue(fail);
    }
}
