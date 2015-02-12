package com.kariqu.tradecenter.client.impl;

import com.google.common.collect.Lists;
import com.kariqu.common.DateUtils;
import com.kariqu.common.encrypt.BCryptUtil;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.*;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.productcenter.service.*;
import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.suppliercenter.service.SupplierService;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.domain.payment.TradeInfo;
import com.kariqu.tradecenter.excepiton.*;
import com.kariqu.tradecenter.helper.SubmitOrderUtil;
import com.kariqu.tradecenter.helper.TradeObjectFactory;
import com.kariqu.tradecenter.service.*;
import com.kariqu.usercenter.domain.*;
import com.kariqu.usercenter.service.MessageTaskService;
import com.kariqu.usercenter.service.UserPointService;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

/**
 * @author Tiger
 * @version 1.0
 * @since 13-1-25 上午10:10
 */
public class TradeCenterClientUserImpl implements TradeCenterUserClient {

    private static final Logger LOGGER = Logger.getLogger(TradeCenterClientUserImpl.class);

    @Autowired
    private OrderWriteService orderWriteService;

    @Autowired
    private OrderQueryService orderQueryService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private BackGoodsWriteService backGoodsWriteService;

    @Autowired
    private BackGoodsQueryService backGoodsQueryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private SkuStorageService skuStorageService;

    @Autowired
    private SupplierService supplierService;

    /** 评论 */
    @Autowired
    private ValuationService valuationService;

    /** 积分 */
    @Autowired
    private UserPointService userPointService;

    @Autowired
    private UserService userService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private MessageTaskService messageTaskService;

    @Autowired
    private IntegralService integralService;

    @Autowired
    private IntegralActivityService integralActivityService;

    @Autowired
    private RotaryLotteryService rotaryLotteryService;

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public List<Order> submitOrder(SubmitOrderInfo submitOrderInfo) throws OrderBaseException {
        User user = userService.getUserById(submitOrderInfo.getUserId());
        user.setAccountType(submitOrderInfo.getAccountType());

        if (!user.isActive()) {
            user.setUserName(submitOrderInfo.getUserName());
            LOGGER.warn("用户Id(" + submitOrderInfo.getUserId() + ")生成订单时信息还未完善, 用户名使用的是第三方昵称("
                    + submitOrderInfo.getUserName() + ")");
        }

        List<TradeItem> tradeItemList = submitOrderInfo.getTradeItemList();
        List<OrderItem> orderItemList = new ArrayList<OrderItem>(tradeItemList.size());

        // 检查订单项及构建订单项相关数据
        long oldItemTotal = 0;
        for (TradeItem tradeItem : tradeItemList) {
            OrderItem orderItem = TradeObjectFactory.createOrderItem(tradeItem);

            // 检查并填充orderItem为初始化的数据
            orderItem.checkAndInit(productService, skuService, skuStorageService);

            orderItemList.add(orderItem);

            // 统一订单总价(方便积分和现金券计算)
            oldItemTotal += orderItem.getItemTotalPrice();
        }

        // 检查积分和现金券, 并计算整个价格(先现金券, 后积分)
        long newItemTotal = SubmitOrderUtil.reducePriceForCouponLaterIntegral(oldItemTotal, submitOrderInfo.getCoupon(),
                submitOrderInfo.getIntegral(), user, couponService, integralService);
        // 将新的价格差平摊至各种订单项
        SubmitOrderUtil.equallyOrderItem(oldItemTotal, newItemTotal, orderItemList, submitOrderInfo.getCoupon(),
                submitOrderInfo.getIntegral(), integralService);

        List<Order> orderList = SubmitOrderUtil.buildSplitOrder(orderItemList, submitOrderInfo, user, supplierService, integralService.getTradeIntegralPercent());

        // ========== 以上全部都只是校验数据及一些不操作 db 的计算, 下面开始操作 db, 一定要保证到达下面的时候数据都是完整的! ==========
        List<Long> orderNoList = new ArrayList<Long>();
        for (Order order : orderList) {
            // 创建订单
            orderWriteService.createNewOrder(order, submitOrderInfo.getMessageInfo());
            orderNoList.add(order.getOrderNo());
        }
        // 使用积分和现金券
        integralService.useIntegral(submitOrderInfo.getIntegral(), orderNoList, user.getId());
        couponService.useCoupon(submitOrderInfo.getCoupon(), orderNoList, user.getId());

        return orderList;
    }

    /**
     * 积分商城订单提交
     * @param submitOrderInfo
     * @return
     * @throws OrderBaseException
     */
    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public List<Order> submitCurrencyOrder(SubmitOrderInfo submitOrderInfo) throws OrderBaseException {
        User user = userService.getUserById(submitOrderInfo.getUserId());
        user.setAccountType(submitOrderInfo.getAccountType());

        if (!user.isActive()) {
            user.setUserName(submitOrderInfo.getUserName());
            LOGGER.warn("用户Id(" + submitOrderInfo.getUserId() + ")生成订单时信息还未完善, 用户名使用的是第三方昵称("
                    + submitOrderInfo.getUserName() + ")");
        }

        List<TradeItem> tradeItemList = submitOrderInfo.getTradeItemList();
        List<OrderItem> orderItemList = new ArrayList<OrderItem>(tradeItemList.size());

        Long integral = 0l;

        // 检查订单项及构建订单项相关数据
        for (TradeItem tradeItem : tradeItemList) {
            OrderItem orderItem = TradeObjectFactory.createOrderItem(tradeItem);

            int userHasJoinCount = integralActivityService.userHasJoinCount(user, orderItem.getSkuActivityType().toString(), orderItem.getActivityId());
            orderItem.checkCurrencyActivity(userHasJoinCount);

            // 检查并填充orderItem为初始化的数据
            orderItem.checkAndInit(productService, skuService, skuStorageService);

            orderItemList.add(orderItem);

            integral += orderItem.getIntegral() * tradeItem.getNumber();
        }

        if (integral > user.getPointTotal()) {
            throw new OrderNoTransactionalException("用户积分不足！");
        }
        submitOrderInfo.setIntegral(integral);

        List<Order> orderList = SubmitOrderUtil.buildSplitOrder(orderItemList, submitOrderInfo, user, supplierService, integralService.getTradeIntegralPercent());

        // ========== 以上全部都只是校验数据及一些不操作 db 的计算, 下面开始操作 db, 一定要保证到达下面的时候数据都是完整的! ==========
        List<Long> orderNoList = new ArrayList<Long>();
        for (Order order : orderList) {
            // 创建订单
            orderWriteService.createNewOrder(order, submitOrderInfo.getMessageInfo());
            orderNoList.add(order.getOrderNo());
        }
        // 使用积分
        integralService.useIntegral(submitOrderInfo.getIntegral(), orderNoList, user.getId());

        return orderList;
    }

    @Override
    public String calculateIntegralWithMoney(long integral) {
        return integralService.integralToYuan(integral);
    }

    @Override
    public List<Coupon> queryCanUseCouponByTotalPriceAndUserId(long totalPrice, int userId) {
        return couponService.queryCanUseCouponByTotalPriceAndUserId(totalPrice, userId);
    }

    @Override
    public SubmitOrderForPrice reducePriceBeforeCouponAfterIntegral(long totalPrice, String couponCode, long integral,
                                                                    User user) throws OrderNoTransactionalException {
        // 存储积分对应的钱, 现金券信息, 及 >> 使用积分和现金券后的价格
        SubmitOrderForPrice submitOrderForPrice = new SubmitOrderForPrice();

        // 先检查计算现金券
        totalPrice = couponService.reducePriceForCoupon(submitOrderForPrice, couponCode, totalPrice);
        if (totalPrice <= 0 && integral > 0) {
            throw new OrderNoTransactionalException("使用优惠券后, 无需使用积分!");
        }

        totalPrice = (totalPrice < 0) ? 0 : integralService.reducePriceForIntegral(submitOrderForPrice, integral, user, totalPrice);
        if (totalPrice < 0) totalPrice = 0;

        submitOrderForPrice.setNewTotalPrice(totalPrice);
        return submitOrderForPrice;
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void cancelOrder(long orderId, int userId) throws OrderBaseException {
        Order order = checkOrderByOrderIdAndUserId(orderId, userId);

        orderWriteService.cancelOrder(order);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("用户[" + userId + "]取消订单[" + order.getOrderNo()
                    + "](此时订单状态[" + order.getOrderState().serviceDesc() + "])");
        }
    }

    private Order checkOrderByOrderIdAndUserId(long orderId, int userId) throws OrderNoTransactionalException {
        Order order = orderQueryService.getDetailsOrder(orderId);
        if (order == null || userId != order.getUserId())
            throw new OrderNoTransactionalException("您没有此订单!");

        return order;
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void confirmOrderSuccess(long orderId, int userId) throws OrderBaseException {
        Order order = checkOrderByOrderIdAndUserId(orderId, userId);
        orderWriteService.confirmSuccessOrder(order, order.getUserName());

        confirmSuccess(order, "用户");
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void updateOrderPayBank(long orderNo, PayBank bank) {
        orderWriteService.updateOrderPayBank(orderNo, bank);
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void changeOrderStateWhenPaySuccess(TradeInfo tradeInfo) throws OrderBaseException {
        tradeService.triggerTradeOrderPaySuccessful(tradeInfo);

        List<Long> orderIdList = queryOrderListByTradeNo(tradeInfo.getTradeNo());
        for (Long orderNo : orderIdList) {
            try {
                orderWriteService.changeOrderToPaySuccess(orderNo);
            } catch (Exception e) {
                LOGGER.error("订单[" + orderNo + "]支付成功后更改订单状态为付款成功时异常: " + e.getMessage());
                tradeService.triggerTradeOrderPayRecharge(tradeInfo.getTradeNo(), orderNo);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void changeOrderItemAppraise(Valuation valuation) throws OrderBaseException {
        OrderItem orderItem = orderQueryService.queryOrderItemsById(valuation.getOrderItemId());
        Order order = orderQueryService.getDetailsOrder(orderItem.getOrderId());
        if (valuation.getUserId() != order.getUserId()) {
            LOGGER.error("用户(" + order.getUserName() + ") 未购买过此商品("
                    + orderItem.getSkuName() + ") 对应订单(" + order.getOrderNo() + ")");
            throw new OrderNoTransactionalException("用户(" + order.getUserName()
                    + ") 未购买过此商品(" + orderItem.getSkuName() + ")");
        }

        valuation.setOrderCreateDate(orderItem.getCreateDate());

        valuationService.createValuation(valuation);
        orderWriteService.updateOrderItemAppraise(valuation.getOrderItemId());

        // 评价后增加积分
        UserGradeRule userGradeRule = userService.getGradeRule(order.getUserId());
        if (userGradeRule == null) {
            LOGGER.error("无此用户(" + order.getUserName() + ")的用户等级规则!");
            throw new OrderNoTransactionalException("缺少用户等级, 请联系管理员!");
        }
        long point = new Double(orderItem.getItemTotalPrice() / 100 * userGradeRule.getValuationRatio()).longValue();
        if (point > 0) {
            String desc = "商品评价送积分(订单号:" + order.getOrderNo() + ")";
            addIntegral(order.getUserId(), point, desc, UserPoint.InOutComingType.Valuation, UserPoint.PointType.InComing);

            // 发短信
            sendValuationSms(order);
        }
    }

    /**
     * 评价后发送短信
     */
    private void sendValuationSms(Order order) {
        User user = userService.getUserById(order.getUserId());
        if (StringUtils.isNotBlank(user.getPhone()) || StringUtils.isNotBlank(order.getLogistics().getMobile())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("point", user.getCurrency());
            params.put("pointForMoney", calculateIntegralWithMoney(user.getPointTotal()));

            // 若用户有设置手机号则发送到用户手机, 否则发送到订单收货人
            String phone = StringUtils.isBlank(user.getPhone()) ? order.getLogistics().getMobile() : user.getPhone();
            messageTaskService.sendSmsMessage(phone, params, MessageTemplateName.VALUATION_SEND_POINT);
        }
    }

    private void confirmSuccess(Order order, String identity) {
        double integralPercent = order.getIntegralPercent();
        if (integralPercent <= 0)
            integralPercent = integralService.getTradeIntegralPercent();

        // 交易完成后, 增加积分
        long point = integralService.moneyToIntegralByIntegralPercent(order.getTotalPrice(), integralPercent);
        if (point > 0) {
            String desc = identity + "确认订单送积分(订单号:" + order.getOrderNo() + ")";
            addIntegral(order.getUserId(), point, desc, UserPoint.InOutComingType.Order, UserPoint.PointType.InComing);
        }
        // 累积用户消费金额
        userService.increaseUserTotalExpense(order.getUserId(), new Money(order.getTotalPrice()).getCent());
    }

    /**
     * 添加积分
     *
     * @param userId
     * @param point           积分分
     * @param desc            说明
     * @param inOutComingType 评价 或 交易完成
     * @param pointType       收入 还是 支出
     */
    private void addIntegral(int userId, long point, String desc,
                             UserPoint.InOutComingType inOutComingType, UserPoint.PointType pointType) {
        UserPoint userPoint = new UserPoint();
        userPoint.setUserId(userId);
        userPoint.setPoint(point);
        userPoint.setDescription(desc);
        userPoint.setInOutComingType(inOutComingType);
        userPoint.setType(pointType);
        userPointService.createUsePoint(userPoint);
    }

    @Override
    public OrderMessage queryUserMessage(long orderId) {
        return orderQueryService.queryUserOrderMessage(orderId);
    }

    // ========================= 系统自动关闭及确认订单 =========================
    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void closeOrder(long orderId) throws OrderBaseException {
        orderWriteService.closeOrder(orderId, "系统");
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void confirmOrderSuccess(long orderId) throws OrderBaseException {
        Order order = orderWriteService.confirmSuccessOrder(orderId, "系统");

        confirmSuccess(order, "系统");
    }

    @Override
    public List<Long> queryNotPayOrder(int delay) {
        return orderQueryService.queryNotPayOrder(delay);
    }

    @Override
    public List<Long> queryNotSuccessOrder(int delay) {
        return orderQueryService.queryNotConfirmSuccessOrder(delay);
    }
    // ======================================================================


    @Override
    public boolean existTradeInfo(String tradeNo, String outerTradeNo) {
        return tradeService.existTradeInfo(tradeNo, outerTradeNo);
    }

    @Override
    public List<Long> queryOrderListByTradeNo(String tradeNo) {
        return tradeService.queryOrderListByTradeNo(tradeNo);
    }

    @Override
    public List<Order> queryOrdersByTradeNo(String tradeNo) {
        List<Long> orderNoList = queryOrderListByTradeNo(tradeNo);
        List<Order> orderList = new LinkedList<Order>();
        for (Long orderNo : orderNoList) {
            orderList.add(orderQueryService.getOrderByOrderNo(orderNo));
        }
        return orderList;
    }

    @Override
    public Page<Order> getOrderPageByUserId(int userId, Integer orderState, Page<Order> page) {
        return orderQueryService.queryOrderByUserIdPage(userId, orderState, page);
    }

    @Override
    public Page<Order> getNotCancelOrderPageByUserId(int userId, Page<Order> page) {
        return orderQueryService.getNotCancelOrderPageByUserId(userId, page);
    }

    @Override
    public Page<Order> getBackGoodsApplyByUserId(int userId, Page<Order> page) {
        return orderQueryService.queryBackGoodsApplyByUserId(userId, page);
    }

    @Override
    public int queryOrderCountByUserIdAndState(int userId, Integer orderState) {
        return orderQueryService.queryOrderCountByUserIdAndState(userId, orderState);
    }

    @Override
    public Order getOrderDetails(long orderNo, int userId) {
        return orderQueryService.getOrderByUserIdAndOrderNo(userId, orderNo);
    }

    @Override
    public Order getOrderBasic(long orderId, int userId) {
        Order order = orderQueryService.getSimpleOrder(orderId);
        if (order == null || userId != order.getUserId())
            return null;

        return order;
    }

    @Override
    public Progress getOrderProgress(long orderId) {
        return orderQueryService.queryOrderProgress(orderId);
    }

    @Override
    public List<ProgressDetail> getProgressDetail(int userId, long orderId, int top) {
        // 判断是当前用户的订单
        if (getOrderBasic(orderId, userId) != null)
            return orderQueryService.queryProgressDetail(orderId, top);
        return null;
    }

    @Override
    public Page<BackGoods> getBackGoodsPageByUserId(int userId, Page<BackGoods> page) {
        return backGoodsQueryService.queryBackGoodsByUserIdForPage(userId, page);
    }

    @Override
    public Map<String, Object> queryValuationCountByUserIdAndAppraise(int userId, int appraise) {
        return orderQueryService.getValuationCountByUserIdAndAppraise(userId, appraise);
    }

    @Override
    public List<OrderItem> queryCanBackOrderItemByOrderNo(long orderNo) {
        return orderQueryService.queryCanBackOrderItemByOrderNo(orderNo);
    }

    @Override
    public String backGoodsImgCompress(String newFileName, String savePosition) {
        return backGoodsWriteService.backGoodsImgCompress(newFileName, savePosition);
    }

    @Override
    public void saveUploadPicture(InputStream inputStream, String savePosition) {
        backGoodsWriteService.saveUploadPicture(inputStream, savePosition);
    }

    @Override
    public Page<OrderItem> queryValuationByUserIdAndAppraise(int userId, int appraise, Page<OrderItem> page) {
        return orderQueryService.queryValuationByUserIdAndAppraise(userId, appraise, page);
    }

    @Override
    public Page<Order> queryRecentOrderByUserId(int userId, Page<Order> page) {
        return orderQueryService.queryRecentOrderByUserId(userId, page);
    }

    @Override
    public int queryRecentOrderCountByUserId(int userId) {
        return orderQueryService.queryRecentOrderCountByUserId(userId);
    }

    @Override
    public int queryWaitPayOrderCountByUserId(int userId) {
        return orderQueryService.queryWaitPayOrderCountByUserId(userId);
    }

    @Override
    public Map<String, Object> queryOrderCountByUserIdAndOrderState(int userId, OrderState orderState) {
        return orderQueryService.queryOrderCountByUserIdAndOrderState(userId, orderState);
    }


    // =============== 退货 ===============
    @Override
    @Transactional(rollbackFor = BackGoodsTransactionalException.class)
    public void submitBackGoods(BackGoods backGoods) throws BackGoodsBaseException {
        backGoodsWriteService.createNewBackGoods(backGoods);
    }

    @Override
    @Transactional(rollbackFor = BackGoodsTransactionalException.class)
    public void cancelBackGoods(long backGoodsId, int userId) throws BackGoodsBaseException {
        backGoodsWriteService.cancelBackGoodsForUser(backGoodsId, userId);
    }

    @Override
    public BackGoods getBackGoodsDetails(long id, int userId) {
        return backGoodsQueryService.queryBackGoodsAndItemByIdAndUserId(userId, id);
    }

    @Override
    public Result<OrderItem, Order> getCanBackOrder(int userId, long orderNo) throws BackGoodsBaseException {
        return backGoodsQueryService.queryCanBackOrderItemsOfUser(orderNo, userId);
    }

    @Override
    public List<BackGoodsLog> getBackGoodsLog(long backGoodsId) {
        return backGoodsQueryService.queryBackGoodsLogByBackGoodsIdForUser(backGoodsId);
    }

    @Override
    public Progress getBackGoodsProgress(long backGoodsId) {
        return backGoodsQueryService.queryBackProgress(backGoodsId);
    }


    // =============== 抽奖活动 ===============
    @Override
    public Rotary getRotaryById(int id) {
        Rotary rotary = getSimpleRotaryById(id);
        rotary.setRotaryMeedList(rotaryLotteryService.queryAllMeedByRotaryIdOrderByIndex(id));
        return rotary;
    }

    private Rotary getSimpleRotaryById(int id) {
        Rotary rotary = rotaryLotteryService.queryRotaryById(id);
        if (rotary == null) throw new TradeFailException("无此抽奖活动");

        return rotary;
    }

    @Override
    public List<Lottery> getAllLotteryByRotaryId(int rotaryId) {
        return rotaryLotteryService.queryAllLotteryByRotaryIdWithOutNil(rotaryId);
    }

    @Override
    @Transactional
    public Map<String, Object> lottery(int rotaryId, int userId, String userName) {
        User user = userService.getUserById(userId);
        if (user == null) throw new TradeFailException("无此用户: " + userName);

        Rotary rotary = getSimpleRotaryById(rotaryId);
        int tally = rotary.getTally();

        Date now = new Date();
        if (now.before(rotary.getStartDate()) || now.after(rotary.getExpireDate()))
            throw new TradeFailException("抽奖活动只在(" + rotary.getStart() + ")到(" + rotary.getEnd() + ")之间进行");
        if (user.getPointTotal() < tally)
            throw new TradeFailException("您不够 " + rotary.getCurrency() + " 积分了");

        // 按照概率从小到大排好序的奖品项
        List<RotaryMeed> rotaryMeedList = rotaryLotteryService.queryAllMeedByRotaryIdOrderByProbability(rotaryId);
        // 将概率相加得到一个总数
        int meedCount = meedCount(rotaryMeedList);
        // 随机取一个数
        int lotteryNumber = new Random().nextInt(meedCount);
        // 判断这个数在哪个区间
        RotaryMeed rotaryMeed = calculate(lotteryNumber, rotaryMeedList);
        // LOGGER.warn("用户(" + userName + ")在最大数(" + meedCount + ")中随机到(" + lotteryNumber + "), so, 抽中的结果是(" + rotaryMeed.getValue() + ")");
        Lottery lottery = rotaryLotteryService.insertLottery(rotary, user, rotaryMeed);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rotaryMeed", rotaryMeed);
        map.put("lotteryId", lottery.getId());
        user = userService.getUserById(userId);
        map.put("integralCount", user.getCurrency());

        return map;
    }

    /**
     * 统计总概率
     */
    private int meedCount(List<RotaryMeed> rotaryMeedList) {
        int count = 0;
        for (RotaryMeed meed : rotaryMeedList) {
            count += meed.getMeedProbability();
        }
        return count;
    }

    /**
     * 计算抽中的值在哪个奖项中间, 奖项的概率需按从小到大排列
     */
    private RotaryMeed calculate(int lotteryNumber, List<RotaryMeed> rotaryMeedList) {
        int min, max = 0;
        int i = 0;
        for (RotaryMeed meed : rotaryMeedList) {
            min = (i == 0) ? 0 : (max + 1);
            max += meed.getMeedProbability();
            // 判断某个数是否在两个数值之间
            if (lotteryNumber >= min && lotteryNumber <= max)
                return meed;

            i++;
        }
        // 将最后的奖品项返回
        LOGGER.warn("数值(" + lotteryNumber + ")不在任何一个区间, 将最后一个奖项返回");
        return rotaryMeedList.get(i);
    }

    @Override
    public void updateSendLottery(String userName, int lotteryId, String consigneeName, String consigneePhone, String consigneeAddress) {
        rotaryLotteryService.updateSendLottery(userName, lotteryId, consigneeName, consigneePhone, consigneeAddress);
    }

    @Override
    @Transactional
    public void exchangeCoupon(int userId, int count) {
        User user = userService.getUserById(userId);
        if (user == null) throw new TradeFailException("无此用户信息");
        long point = 0, price = 0, miniApplyOrderPrice = 0;
        switch (count) {
            case 20:
            case 50: {
                point = count * 10;
                price = count * 100;
                miniApplyOrderPrice = 0;
                break;
            }
            case 100: {
                point = 2000;
                price = 10000;
                miniApplyOrderPrice = 50000;
                break;
            }
        }
        if (user.getPointTotal() < point) throw new TradeFailException("您不够 " + com.kariqu.usercenter.domain.Currency.IntegralToCurrency(point) + " 积分了");

        Date start = DateUtils.parseDate("2013-11-10 00:00:01", DateUtils.DateFormatType.DATE_FORMAT_STR);
        Date expire = DateUtils.parseDate("2013-11-11 23:59:59", DateUtils.DateFormatType.DATE_FORMAT_STR);

        // 生成现金券
        Coupon coupon = couponService.generateCoupon(price, miniApplyOrderPrice, start, expire);
        // 减积分
        UserPoint disPoint = new UserPoint();
        disPoint.setUserId(userId);
        disPoint.setPoint(point);
        disPoint.setType(UserPoint.PointType.OutComing);
        disPoint.setInOutComingType(UserPoint.InOutComingType.Exchange);
        disPoint.setDescription("积分兑换" + coupon.getMoney() + "元现金券(" + coupon.getCode() + ")");
        // 先减积分
        userPointService.createUsePoint(disPoint);

        coupon.setUserId(userId);
        couponService.createCoupon(coupon);
    }

    @Override
    @Transactional
    public void receiveCoupon(int userId, int count) {
        Date start = DateUtils.parseDate("2014-11-11 00:00:01", DateUtils.DateFormatType.DATE_FORMAT_STR);
        Date expire = DateUtils.parseDate("2014-11-11 23:59:59", DateUtils.DateFormatType.DATE_FORMAT_STR);

        long price = count * 100;
        long miniPrice = 0;
        switch (count) {
            case 10 : {
                miniPrice = 30000;
                break;
            }
            case 20 : {
                miniPrice = 60000;
                break;
            }
            case 50 : {
                miniPrice = 90000;
                break;
            }
        }

        // 生成现金券
        Coupon coupon = couponService.generateCoupon(price, miniPrice, start, expire);
        coupon.setUserId(userId);
        couponService.createCoupon(coupon);
    }

}
