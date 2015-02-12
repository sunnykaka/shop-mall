package com.kariqu.om;

import com.kariqu.om.domain.Const;
import com.kariqu.om.repository.ConstRepository;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static junit.framework.Assert.assertEquals;

/**
 * @author Athens(刘杰)
 * @Time 2012-11-30 09:31
 * @since 1.0.0
 */
@ContextConfiguration(locations = {"/omCenter.xml"})
public class ConstRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private ConstRepository constRepository;

    @Test
    public void testConst() {
        Const constInfo = new Const();
        constInfo.setConstKey("integral");
        constInfo.setConstValue("1");
        constInfo.setConstComment("积分兑换点数, 值对应的点数兑换一元");

        constRepository.insertConst(constInfo);

        constInfo = constRepository.getConstByKey("integral");
        assertEquals("1", constInfo.getConstValue());

        Const conInfo = new Const();
        conInfo.setConstKey("coupon");
        conInfo.setConstValue("5");
        conInfo.setConstComment("现金券发送短信提醒的时限, 系统会在每天上午 10 点向此值对应的天数失效的优惠券用户发送短信提醒");
        constRepository.insertConst(conInfo);

        assertEquals(6, constRepository.getAllConst().size());

        constInfo.setConstValue("10");
        constRepository.update(constInfo);
        constInfo = constRepository.getConstByKey("integral");
        assertEquals("10", constInfo.getConstValue());
    }

}
