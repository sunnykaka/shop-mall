package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.tradecenter.domain.Lottery;
import com.kariqu.tradecenter.domain.Rotary;
import com.kariqu.tradecenter.domain.RotaryMeed;
import com.kariqu.tradecenter.repository.RotaryLotteryRepository;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

@SpringApplicationContext({"classpath:tradeContext.xml"})
public class RotaryLotteryRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("rotaryLotteryRepository")
    private RotaryLotteryRepository rotaryLotteryRepository;

    @Test
    @Rollback(false)
    public void testRotaryLotteryRepository() {
        Rotary rotary = new Rotary();
        rotary.setName("双 11 幸运大轮盘");
        rotary.setTally(20);
        rotary.setRule("每次抽奖使用 0.2 积分, 每天抽奖次数不限!");
        rotary.setDetailRule("bala bala bala bala bala");
        rotaryLotteryRepository.insertRotary(rotary);

        assertEquals(1, rotaryLotteryRepository.queryAllRotary().size());
        rotary = rotaryLotteryRepository.queryRotaryById(rotary.getId());
        rotary.setTally(10);
        rotary.setRule("每次抽奖使用 0.1 积分.");
        rotaryLotteryRepository.updateRotary(rotary);
        rotary = rotaryLotteryRepository.queryRotaryById(rotary.getId());
        assertEquals(10, rotary.getTally());


        // =================== 奖品项 ===================
        RotaryMeed meed1 = new RotaryMeed();
        meed1.setRotaryId(rotary.getId());
        meed1.setMeedIndex(1);
        meed1.setMeedProbability(200);
        meed1.setMeedType(RotaryMeed.MeedType.Product);
        meed1.setMeedValue("11-477");
        meed1.setImageUrl("http://...jpg");
        meed1.setDescription("价格 50元 商品");
        rotaryLotteryRepository.insertMeed(meed1);

        meed1.setMeedValue("54-784");
        rotaryLotteryRepository.updateMeed(meed1);

        RotaryMeed meed2 = new RotaryMeed();
        meed2.setRotaryId(rotary.getId());
        meed2.setMeedIndex(2);
        meed2.setMeedProbability(300);
        meed2.setMeedType(RotaryMeed.MeedType.Product);
        meed2.setMeedValue("9-475");
        meed2.setImageUrl("http://...jpg");
        meed2.setDescription("价格 50元 商品");
        rotaryLotteryRepository.insertMeed(meed2);

        RotaryMeed meed3 = new RotaryMeed();
        meed3.setRotaryId(rotary.getId());
        meed3.setMeedIndex(3);
        meed3.setMeedProbability(500);
        meed3.setMeedType(RotaryMeed.MeedType.Integral);
        meed3.setMeedValue("500");
        meed3.setImageUrl("http://...jpg");
        meed3.setDescription("价格 50元 商品");
        rotaryLotteryRepository.insertMeed(meed3);

        RotaryMeed meed4 = new RotaryMeed();
        meed4.setRotaryId(rotary.getId());
        meed4.setMeedIndex(4);
        meed4.setMeedProbability(1000);
        meed4.setMeedType(RotaryMeed.MeedType.Integral);
        meed4.setMeedValue("100");
        meed4.setImageUrl("http://...jpg");
        meed4.setDescription("价格 50元 商品");
        rotaryLotteryRepository.insertMeed(meed4);

        RotaryMeed meed5 = new RotaryMeed();
        meed5.setRotaryId(rotary.getId());
        meed5.setMeedIndex(5);
        meed5.setMeedProbability(1500);
        meed5.setMeedType(RotaryMeed.MeedType.Integral);
        meed5.setMeedValue("50");
        meed5.setImageUrl("http://...jpg");
        meed5.setDescription("价格 50元 商品");
        rotaryLotteryRepository.insertMeed(meed5);

        RotaryMeed meed6 = new RotaryMeed();
        meed6.setRotaryId(rotary.getId());
        meed6.setMeedIndex(6);
        meed6.setMeedProbability(2000);
        meed6.setMeedType(RotaryMeed.MeedType.Integral);
        meed6.setMeedValue("50");
        meed6.setImageUrl("http://...jpg");
        meed6.setDescription("价格 50元 商品");
        rotaryLotteryRepository.insertMeed(meed6);

        RotaryMeed meed7 = new RotaryMeed();
        meed7.setRotaryId(rotary.getId());
        meed7.setMeedIndex(7);
        meed7.setMeedProbability(4500);
        meed7.setMeedType(RotaryMeed.MeedType.Integral);
        meed7.setMeedValue("10");
        meed7.setImageUrl("http://...jpg");
        meed7.setDescription("价格 50元 商品");
        rotaryLotteryRepository.insertMeed(meed7);

        assertEquals(7, rotaryLotteryRepository.queryAllMeedByRotaryIdOrderByIndex(rotary.getId()).size());

        // =================== 中奖 ===================
        Lottery lottery1 = new Lottery();
        lottery1.setRotaryId(rotary.getId());
        lottery1.setRotaryMeedId(meed7.getId());
        lottery1.setUserName("tony");
        lottery1.setReally(true);
        lottery1.setMeedType(meed7.getMeedType());
        lottery1.setMeedValue(meed7.getMeedValue());
        rotaryLotteryRepository.insertLottery(lottery1);

        Lottery lottery2 = new Lottery();
        lottery2.setRotaryId(rotary.getId());
        lottery2.setRotaryMeedId(meed1.getId());
        lottery2.setUserName("guest");
        lottery2.setReally(false);
        lottery2.setMeedType(meed1.getMeedType());
        lottery2.setMeedValue(meed1.getMeedValue());
        rotaryLotteryRepository.insertLottery(lottery2);

        Lottery lottery3 = new Lottery();
        lottery3.setRotaryId(rotary.getId());
        lottery3.setRotaryMeedId(meed6.getId());
        lottery3.setUserName("asion");
        lottery3.setReally(true);
        lottery3.setMeedType(meed6.getMeedType());
        lottery3.setMeedValue(meed6.getMeedValue());
        rotaryLotteryRepository.insertLottery(lottery3);

        Lottery lottery4 = new Lottery();
        lottery4.setRotaryId(rotary.getId());
        lottery4.setRotaryMeedId(meed2.getId());
        lottery4.setUserName("du");
        lottery4.setReally(true);
        lottery4.setMeedType(meed2.getMeedType());
        lottery4.setMeedValue(meed2.getMeedValue());

        lottery4.setConsigneeName("杜杜");
        lottery4.setConsigneePhone("13012345678");
        lottery4.setConsigneeAddress("深圳市寶安區...");
        rotaryLotteryRepository.insertLottery(lottery4);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rotaryId", rotary.getId());
        assertEquals(4, rotaryLotteryRepository.queryLotteryByQuery(map).getResult().size());
        map.put("really", true);
        assertEquals(3, rotaryLotteryRepository.queryLotteryByQuery(map).getResult().size());
        map.put("needSend", true);
        assertEquals(1, rotaryLotteryRepository.queryLotteryByQuery(map).getResult().size());
        map.put("sendOut", true);
        assertEquals(0, rotaryLotteryRepository.queryLotteryByQuery(map).getResult().size());

        lottery4.setSendOut(true);
        rotaryLotteryRepository.updateLottery(lottery4);
        assertEquals(1, rotaryLotteryRepository.queryLotteryByQuery(map).getResult().size());
    }
}
