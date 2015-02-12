package com.kariqu.tradecenter.service.impl;

import com.kariqu.common.DateUtils;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.tradecenter.domain.Coupon;
import com.kariqu.tradecenter.domain.Lottery;
import com.kariqu.tradecenter.domain.Rotary;
import com.kariqu.tradecenter.domain.RotaryMeed;
import com.kariqu.tradecenter.excepiton.TradeFailException;
import com.kariqu.tradecenter.repository.RotaryLotteryRepository;
import com.kariqu.tradecenter.service.CouponService;
import com.kariqu.tradecenter.service.RotaryLotteryService;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.domain.UserPoint;
import com.kariqu.usercenter.service.UserPointService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class RotaryLotteryServiceImpl implements RotaryLotteryService {

    private static final Log logger = LogFactory.getLog(RotaryLotteryServiceImpl.class);

    @Autowired
    private RotaryLotteryRepository rotaryLotteryRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserPointService userPointService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SkuService skuService;
    
    @Override
    public void insertRotary(Rotary rotary) {
        rotaryLotteryRepository.insertRotary(rotary);
    }

    @Override
    public void updateRotary(Rotary rotary) {
        rotaryLotteryRepository.updateRotary(rotary);
    }

    @Override
    public void deleteRotary(int rotaryId) {
        rotaryLotteryRepository.deleteRotary(rotaryId);
    }

    @Override
    public List<Rotary> queryAllRotary() {
        return rotaryLotteryRepository.queryAllRotary();
    }

    @Override
    public Rotary queryRotaryById(int rotaryId) {
        return rotaryLotteryRepository.queryRotaryById(rotaryId);
    }



    @Override
    public void insertMeed(RotaryMeed meed) {
        rotaryLotteryRepository.insertMeed(meed);
    }

    @Override
    public void updateMeed(RotaryMeed meed) {
        rotaryLotteryRepository.updateMeed(meed);
    }

    @Override
    public void deleteMeed(int meedId) {
        rotaryLotteryRepository.deleteMeed(meedId);
    }

    @Override
    public List<RotaryMeed> queryAllMeedByRotaryIdOrderByIndex(int rotaryId) {
        return rotaryLotteryRepository.queryAllMeedByRotaryIdOrderByIndex(rotaryId);
    }

    @Override
    public List<RotaryMeed> queryAllMeedByRotaryIdOrderByProbability(int rotaryId) {
        return rotaryLotteryRepository.queryAllMeedByRotaryIdOrderByProbability(rotaryId);
    }

    @Override
    public RotaryMeed queryMeedById(int meedId) {
        return rotaryLotteryRepository.queryMeedById(meedId);
    }



    /** 默认的优惠券价格. 20 元 */
    private static final long DEFAULT_COUPON_PRICE = 2000;
    /** 默认的优惠券最少使用金额. 200 元 */
    private static final long DEFAULT_MIN_COUPON_PRICE = 20000;
    /** 默认的优惠券过期时间. 1 个月 */
    private static final int DEFAULT_EXPIRE_MONTH = 1;

    @Override
    @Transactional
    public Lottery insertLottery(Rotary rotary, User user, RotaryMeed meed) {
        Lottery lottery = new Lottery();
        // 抽奖结果说明, 写入积分统计表
        String lotteryDesc = "无";
        // 抽中了优惠券或积分直接将数据写入用户相关信息, 商品则等待下一个地方写入发货信息
        RotaryMeed.MeedType meedType = meed.getMeedType();
        UserPoint winPoint = null;
        Coupon coupon = null;
        switch (meedType) {
            case Null: {
                lottery.setValue(meed.getValue());
                break;
            }
            case Coupon: {
                // 生成优惠券(价格, 最低使用金额, 过期时间)
                String[] value = meed.getMeedValue().split("\\-");
                long price = NumberUtils.toLong(value[0]) * 100;
                if (price <= 0) price = DEFAULT_COUPON_PRICE;

                long miniApplyOrderPrice = NumberUtils.toLong(value.length > 1 ? value[1] : "") * 100;
                if (miniApplyOrderPrice < 0) miniApplyOrderPrice = DEFAULT_MIN_COUPON_PRICE;

                int expireMonth = NumberUtils.toInt(value.length > 2 ? value[2] : "");
                if (expireMonth <= 0) expireMonth = DEFAULT_EXPIRE_MONTH;

                Calendar calendar = Calendar.getInstance();
                Date today = DateUtils.parseDate(DateUtils.formatDate(calendar.getTime(), DateUtils.DateFormatType.SIMPLE_DATE_FORMAT_STR)
                        + " 00:00:01", DateUtils.DateFormatType.DATE_FORMAT_STR);
                calendar.add(Calendar.MONTH, expireMonth);
                // calendar.add(Calendar.DAY_OF_MONTH, -1); // 下个月的昨天
                Date month = DateUtils.parseDate(DateUtils.formatDate(calendar.getTime(), DateUtils.DateFormatType.SIMPLE_DATE_FORMAT_STR)
                        + " 23:23:59", DateUtils.DateFormatType.DATE_FORMAT_STR);

                coupon = couponService.generateCoupon(price, miniApplyOrderPrice, today, month);
                coupon.setUserId(user.getId());

                lottery.setValue(Money.getMoneyString(price) + "元" + meedType.toStr());
                lotteryDesc = meedType.toStr() + "(" + coupon.getCode() + ")";
                break;
            }
            case Integral: {
                // 给用户添加积分
                winPoint = new UserPoint();
                winPoint.setUserId(user.getId());
                winPoint.setPoint(NumberUtils.toLong(meed.getMeedValue()));
                winPoint.setType(UserPoint.PointType.InComing);
                winPoint.setInOutComingType(UserPoint.InOutComingType.Lottery);
                winPoint.setDescription("抽奖中积分");

                lottery.setValue(meed.getCurrency() + "点" + meedType.toStr());
                lotteryDesc = meedType.toStr() + "(" + meed.getCurrency() + "点)";
                break;
            }
            case Product: {
                String value = meed.getValue();
                // 记录
                String[] proArr = meed.getMeedValue().split("\\-");
                StringBuilder sbd = new StringBuilder();
                Product product = productService.getSimpleProductById(NumberUtils.toInt(proArr[0]));
                if (product != null) {
                    value = product.getName();
                    sbd.append(product.getName());
                    if (proArr.length > 1) {
                        StockKeepingUnit sku = skuService.getStockKeepingUnit(NumberUtils.toLong(proArr[1]));
                        sbd.append(sku == null ? "" : " " + skuService.getSkuPropertyToString(sku));
                    }
                }
                if (StringUtils.isBlank(sbd.toString())) {
                    if (logger.isErrorEnabled())
                        logger.error("用户(" + user.getUserName() + ")抽中了: " + meed.getValue() + ", 却无此商品数据!");
                }

                lottery.setValue(value);
                lotteryDesc = sbd.toString();
                break;
            }
        }
        // 减积分
        UserPoint disPoint = new UserPoint();
        disPoint.setUserId(user.getId());
        disPoint.setPoint(rotary.getTally());
        disPoint.setType(UserPoint.PointType.OutComing);
        disPoint.setInOutComingType(UserPoint.InOutComingType.Lottery);
        disPoint.setDescription("抽奖消耗积分! " + (meedType != RotaryMeed.MeedType.Null ? "抽中的是: " + lotteryDesc : "未抽中."));

        // 先减积分, 再写现金券或添加积分啥的
        userPointService.createUsePoint(disPoint);
        if (coupon != null) couponService.createCoupon(coupon);
        if (winPoint != null) userPointService.createUsePoint(winPoint);

        lottery.setMeedValue(meed.getMeedValue());
        lottery.setRotaryId(rotary.getId());
        lottery.setUserName(user.getUserName());
        lottery.setRotaryMeedId(meed.getId());
        lottery.setMeedType(meedType);
        lottery.setReally(true);
        rotaryLotteryRepository.insertLottery(lottery);

        return queryLotteryById(lottery.getId());
    }

    /** 更新需要发货的中奖数据 */
    @Override
    public void updateSendLottery(String userName, int lotteryId, String consigneeName, String consigneePhone, String consigneeAddress) {
        Lottery lottery = queryLotteryById(lotteryId);
        if (lottery == null)
            throw new TradeFailException("奖品不存在");
        if (!userName.equals(lottery.getUserName()))
            throw new TradeFailException("奖品不属于此用户(" + userName + ")");
        if (!lottery.isReally() || lottery.getMeedType() != RotaryMeed.MeedType.Product || lottery.isSendOut())
            throw new TradeFailException("奖品不需要发货");

        lottery.setConsigneeName(consigneeName);
        lottery.setConsigneePhone(consigneePhone);
        lottery.setConsigneeAddress(consigneeAddress);

        updateLottery(lottery);
    }

    @Override
    public void updateLottery(Lottery lottery) {
        rotaryLotteryRepository.updateLottery(lottery);
    }

    @Override
    public void insertMenLottery(int rotaryId, String userName, int meedId, Date createDate) {
        RotaryMeed meed = queryMeedById(meedId);
        if (meed == null) throw new TradeFailException("奖品Id(" + meedId + ")不存在!");

        Lottery lottery = new Lottery();
        lottery.setRotaryId(rotaryId);
        lottery.setUserName(userName);
        lottery.setRotaryMeedId(meedId);
        lottery.setMeedType(meed.getMeedType());

        RotaryMeed.MeedType meedType = meed.getMeedType();
        switch (meedType) {
            case Null: {
                break;
            }
            case Coupon: {
                String[] value = meed.getMeedValue().split("\\-");
                long price = NumberUtils.toLong(value[0]) * 100;
                if (price <= 0) price = DEFAULT_COUPON_PRICE;

                lottery.setValue(String.valueOf(price));
                break;
            }
            case Integral: {
                lottery.setValue(meed.getValue());
                break;
            }
            case Product: {
                String value = meed.getValue();
                // 记录
                String[] proArr = meed.getMeedValue().split("\\-");
                Product product = productService.getSimpleProductById(NumberUtils.toInt(proArr[0]));
                if (product != null) {
                    value = product.getName();
                }

                lottery.setValue(value);
                break;
            }
        }

        lottery.setMeedValue(meed.getMeedValue());
        lottery.setReally(false);
        lottery.setCreateDate(createDate);
        rotaryLotteryRepository.insertLottery(lottery);
    }

    @Override
    public void deleteLottery(int lotteryId) {
        rotaryLotteryRepository.deleteLottery(lotteryId);
    }

    @Override
    public void sendOutLottery(int lotteryId) {
        rotaryLotteryRepository.sendOutLottery(lotteryId);
    }

    @Override
    public List<Lottery> queryAllLotteryByRotaryIdWithOutNil(Integer rotaryId) {
        return rotaryLotteryRepository.queryAllLotteryByRotaryIdWithOutNil(rotaryId);
    }

    @Override
    public Page<Lottery> queryLotteryByQuery(Integer rotaryId, String userName, Boolean needSend, Boolean sendOut, Page<Lottery> page) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rotaryId", rotaryId);
        if (StringUtils.isNotBlank(userName)) map.put("userName", userName);
        //if (really != null) map.put("really", really);
        // 需要发货则查询出的数据得是真实的
        if (needSend != null) map.put("needSend", needSend);
        // 数据真实且需要发货
        if (sendOut != null) map.put("sendOut", sendOut);

        map.put("start", page.getStart());
        map.put("limit", page.getLimit());

        return rotaryLotteryRepository.queryLotteryByQuery(map);
    }

    @Override
    public Lottery queryLotteryById(int lotteryId) {
        return rotaryLotteryRepository.queryLotteryById(lotteryId);
    }

}
