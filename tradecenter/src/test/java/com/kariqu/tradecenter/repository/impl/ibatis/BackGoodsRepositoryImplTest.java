package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.tradecenter.domain.BackGoods;
import com.kariqu.tradecenter.domain.BackGoodsState;
import com.kariqu.tradecenter.repository.BackGoodsItemRepository;
import com.kariqu.tradecenter.repository.BackGoodsLogRepository;
import com.kariqu.tradecenter.repository.BackGoodsRepository;
import com.kariqu.usercenter.domain.AccountType;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import static junit.framework.Assert.assertEquals;

/**
 * @author Athens(刘杰)
 * @Time 2012-11-30 09:31
 * @since 1.0.0
 */
@SpringApplicationContext({"classpath:tradeContext.xml"})
public class BackGoodsRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("backGoodsRepository")
    private BackGoodsRepository backGoodsRepository;

    @SpringBean("backGoodsItemRepository")
    private BackGoodsItemRepository backGoodsItemRepository;

    @SpringBean("backGoodsLogRepository")
    private BackGoodsLogRepository backGoodsLogRepository;

    @Test
    public void testBackGoods() {
        // 添加退货单
        BackGoods back = new BackGoods();
        back.setOrderNo(2012113094L);
        back.setOrderId(353532L);
        back.setUserId(123);
        back.setUserName("abc");
        back.setAccountType(AccountType.KRQ);
        back.setExpressNo("YT-456987512");
        back.setBackPrice(10000L);
        back.setBackReasonReal(BackGoods.BackReason.NoQualityProblem);
        back.setBackAddress("广东省深圳市宝安区");
        back.setBackReason("不喜欢这个商品");
        back.setProcessMode(BackGoods.ProcessMode.BackGood);
        back.setBackShopperName("abc");
        back.setBackPhone("13012345678");
        back.setBackState(BackGoodsState.Create);
        backGoodsRepository.insert(back);

        assertEquals(353532L,backGoodsRepository.select(back.getId()).getOrderId().longValue());

        long id = back.getId();
        BackGoods goods = new BackGoods();
        goods.setExpressNo("EMS-124578964");
        goods.setId(id);
        assertEquals(1, backGoodsRepository.update(goods));

        // 查询此退货单
        // BackGoods bg = backGoodsRepository.select(id);
        // System.out.println("time: " + Utils.formatDate(bg.getUpdateTime()));

        assertEquals(0, backGoodsItemRepository.selectByBackGoodsId(id).size());

        // 查询用户
        assertEquals(1, backGoodsRepository.selectByUserId(123).size());
        assertEquals(0, backGoodsRepository.selectByUserId(234).size());

        // 查询订单
        assertEquals(1, backGoodsRepository.selectByOrderNo(2012113094L, 123).size());
        assertEquals(0, backGoodsRepository.selectByOrderNo(2012113095L, 123).size());

        // 删除(逻辑删除)
        assertEquals(1, backGoodsRepository.delete(id));

        // 再次查询
        assertEquals(null, backGoodsRepository.select(id));

        assertEquals(0, backGoodsRepository.selectByState(BackGoodsState.Create).size());

        assertEquals(0, backGoodsLogRepository.selectByBackId(20l).size());
    }

}
