package com.kariqu.tradecenter.domain;

import com.kariqu.productcenter.domain.Money;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.excepiton.OrderNoTransactionalException;
import com.kariqu.tradecenter.helper.OrderAssert;
import com.kariqu.tradecenter.service.CouponService;
import com.kariqu.tradecenter.service.OrderQueryService;
import com.kariqu.tradecenter.service.OrderWriteService;
import com.kariqu.usercenter.domain.AccountType;
import com.kariqu.usercenter.domain.UserPoint;
import com.kariqu.usercenter.service.UserPointService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 订单对象
 * 这个对象是否有效必须系统仔细确认，比如可能发生如下情况：
 * 1，没货了
 * 2，等待在线付款
 * 订单真正生效就进入实际的物流流程，订单从入库开始在它上面就会发生一系列事件，同时也开始了状态迁移
 * User: Asion
 * Date: 11-10-11
 * Time: 下午12:36
 */
public class Order {

    private static final Logger LOGGER = Logger.getLogger(Order.class);

    private long id;

    //商品条目
    private List<OrderItem> orderItemList = new ArrayList<OrderItem>();

    //物流
    private Logistics logistics;

    //支付信息
    private PayType payType;

    //支付银行
    private PayBank payBank;

    //发票信息
    private InvoiceInfo invoiceInfo;

    //是否有效，如果被用户取消或者没有付款都是无效的
    private boolean valid;

    //订单是永久存储的，所以这里保存了这次订单总价格
    private String totalPrice;

    //订单每时每刻都必然对应一个状态
    private OrderState orderState;

    //谁下的单
    private int userId;

    private String userName;

    //账户类型，可区别出来自什么网站，比如QQ，sina，KRQ代表我们自己
    private AccountType accountType;

    //下单时间
    private Date createDate;

    /**
     * 更新时间
     */
    private Date modifyDate;

    /**
     * 付款时间
     */
    private Date payDate;

    /**
     * 最终时间(取消 关闭 交易完成)
     */
    private Date endDate;

    // 取消时间
    private Date cancelDate;

    //下单时间的long型表示
    private long milliDate;

    /**
     * 商家Id
     */
    private int customerId;

    /**
     * 商家名(快照冗余)
     */
    private String supplierName;

    /**
     * 仓库Id
     */
    private int storageId;

    /**
     * 仓库名(快照冗余)
     */
    private String storageName;


    /**
     * 关联的虚拟订单ID
     */
    private long virtualOrderId;

    /**
     * 订单编号
     */
    private long orderNo;

    /**
     * 订单所发的物流公司(顺风, 中通等), 与订单物流表中冗余
     */
    private DeliveryInfo.DeliveryType deliveryType;

    /**
     * 必须的订单前置状态, 只在更新 SQL 时有效, 不写入数据库
     */
    private OrderState mustPreviousState;

    /**
     * 优惠劵
     */
    private Coupon coupon;

    /**
     * 使用现金券或积分时记录其订单价格相关说明
     */
    private String priceMessage;

    /**
     * 使用现金券或积分时记录其订单价格相关详细说明
     */
    private String priceMessageDetail;

    /**
     * 积分比例
     */
    private double integralPercent;

    /**
     * 计算订单总价.
     *
     * @throws OrderNoTransactionalException
     */
    public void calculateTotalPrice() throws OrderNoTransactionalException {
        long total = 0;
        for (OrderItem orderItem : orderItemList) {
            total += orderItem.getItemTotalPrice();
        }
        totalPrice = Money.getMoneyString(total);
    }

    /**
     * 计算原始订单总价.
     *
     * @return
     */
    public long calculateOldTotalPrice() {
        long total = 0;
        for (OrderItem orderItem : orderItemList) {
            total += orderItem.totalPrice();
        }
        return total;
    }

    public boolean checkCanNBack() {
        return new Money(totalPrice).getCent() > 0 && !this.orderState.checkCanNotBack();
    }

    /**
     * 提供给前台显示原始订单总价
     */
    public String calculateOldTotalPriceMoney() {
        return Money.getMoneyString(this.calculateOldTotalPrice());
    }

    // ====================================== 优惠券积分独用表来管理了, 此段需要重构 ======================================

    /**
     * 计算订单总价.
     */
    public void calculateTotalPrice(Coupon coupon, long integral, int orderListSize) {
        long total = 0;
        for (OrderItem orderItem : orderItemList) {
            total += orderItem.getItemTotalPrice();
        }
        totalPrice = Money.getMoneyString(total);

        // 如果有使用优惠券或积分, 则两个价格不可能一致
        if (coupon != null || integral > 0){
            StringBuilder priceMessageDetail = new StringBuilder();
            StringBuilder priceMessage = new StringBuilder();
            // ~满减~

            // 记录当前订单使用的优惠券信息
            if (coupon != null) {
                priceMessage.append(ChangePriceElement.coupon);

                priceMessageDetail.append("现金").append(ChangePriceElement.coupon.toDesc()).append("(")
                        .append(coupon.getCode()).append(")").append(ChangePriceElement.money.toDesc()).append("(").append(coupon.getMoney()).append(")");
                if (orderListSize > 1) {
                    priceMessageDetail.append(",平摊回的").append(ChangePriceElement.money.toDesc()).append("(");
                    long couponRatio = 0;
                    for (OrderItem orderItem : orderItemList) {
                        couponRatio += orderItem.getCouponApportion();
                    }
                    priceMessageDetail.append(Money.getMoneyString(couponRatio)).append(")");
                }
            }

            // 记录当前订单使用的积分
            if (integral > 0) {
                if (StringUtils.isNotBlank(priceMessageDetail.toString())) {
                    priceMessage.append(";");
                    priceMessageDetail.append(";");
                }
                priceMessage.append(ChangePriceElement.integral);

                priceMessageDetail.append(ChangePriceElement.integral.toDesc()).append("(").append(com.kariqu.usercenter.domain.Currency.IntegralToCurrency(integral)).append(")点");
                if (orderListSize > 1) {
                    priceMessageDetail.append(",平摊回的").append(ChangePriceElement.integral.toDesc()).append("(");
                    long integralRatio = 0;
                    for (OrderItem orderItem : orderItemList) {
                        integralRatio += orderItem.getIntegralApportion();
                    }
                    priceMessageDetail.append(com.kariqu.usercenter.domain.Currency.IntegralToCurrency(integralRatio)).append(")点");
                }
            }
            this.priceMessage = priceMessage.toString();
            this.priceMessageDetail = priceMessageDetail.toString();
        }
    }

    /**
     * 回加积分和让用过的现金券可用
     */
    public void backToCouponAndIntegral(CouponService couponService, UserPointService userPointService, OrderQueryService orderQueryService) {
        // 现金券
        String couponCode = getCouponCode();
        if (StringUtils.isNotBlank(couponCode)) {
            // 将现金券置为可用
            Coupon cp = couponService.getCouponByCode(couponCode);
            if (cp != null && cp.isUsed() && cp.getCouponType() == Coupon.CouponType.Normal) {
                // 查询此现金券对应的其他订单是不是已经都取消了
                List<Map<String, Object>> orderList = orderQueryService.queryCountByCouponIdWithoutId(cp.getId(), id);
                if (orderList == null || orderList.size() == 0) {
                    cp.setUsed(false);
                    couponService.updateCoupon(cp);

                    LOGGER.warn("操作订单(" + orderNo + ")时, 将现金券 (" + couponCode + ") 置回可用, 此订单对应用户(id: " + userId + ", name: " + userName + ").");
                } else {
                    StringBuilder sbd = new StringBuilder();
                    int i = 0;
                    for (Map order : orderList) {
                        sbd.append(order.get("orderNo")).append("/").append(order.get("orderState"));
                        i++;
                        if (i != orderList.size())
                            sbd.append(",");
                    }
                    LOGGER.warn("操作订单(" + orderNo + ")时, 现金券 (" + couponCode + ") 有作用在其他还没取消的订单(" + sbd.toString()
                            + ")上, 不能被置回可用. 此订单对应用户(id: " + userId + ", name: " + userName + ").");
                }
            }
        }

        // 积分
        long integral = getIntegral();
        if (integral > 0) {
            UserPoint userPoint = new UserPoint();
            userPoint.setUserId(userId);
            userPoint.setPoint(Math.abs(integral));
            userPoint.setType(UserPoint.PointType.InComing);
            userPoint.setInOutComingType(UserPoint.InOutComingType.Cancel);
            userPoint.setDescription("取消订单(" + orderNo + ")时回加积分");
            userPointService.createUsePoint(userPoint);

            LOGGER.warn("操作订单(" + orderNo + ")时, 回加用户(id: " + userId + ", name: " + userName + ")积分(" + Money.getMoneyString(integral) + "点)");
        }

        // 满减
    }

    private enum ChangePriceElement {
        /**
         * 现金券
         */
        coupon,

        money,

        /**
         * 积分
         */
        integral;

        // 满减

        private static Map<ChangePriceElement, String> map = new HashMap<ChangePriceElement, String>();

        static {
            map.put(coupon, "券");
            map.put(money, "金额");
            map.put(integral, "积分");
        }

        private String toDesc() {
            return map.get(this);
        }

        /**
         * 获取当前因素对应的价格值
         *
         * @param priceMessageDetail 订单的价格信息说明
         * @return
         */
        private String getPriceValueByOrderPriceMessageDetail(String priceMessageDetail) {
            if (StringUtils.isBlank(priceMessageDetail))
                return "";

            Matcher matcher = Pattern.compile(toDesc() + "\\(([^)]*)\\)").matcher(priceMessageDetail);
            String find = "";
            // 使用最后一个匹配(如下面这种情况时, 取后面的金额或积分数: "优惠券(om677735)金额(50.00),平摊回的金额(30.00);积分(47),平摊回的积分(30)点")
            while (matcher.find())
                find = matcher.group(1);

            return find;
        }
    }

    /**
     * 订单使用的优惠券编号
     */
    public String getCouponCode() {
        return ChangePriceElement.coupon.getPriceValueByOrderPriceMessageDetail(priceMessageDetail);
    }

    /**
     * 订单使用的优惠券平摊的价格
     */
    public String getCouponMoney() {
        return ChangePriceElement.money.getPriceValueByOrderPriceMessageDetail(priceMessageDetail);
    }

    /**
     * 订单使用的积分点数
     */
    public long getIntegral() {
        String currency = ChangePriceElement.integral.getPriceValueByOrderPriceMessageDetail(priceMessageDetail);
        return com.kariqu.usercenter.domain.Currency.CurrencyToIntegral(StringUtils.isBlank(currency) ? "0" : currency);
    }

    // ====================================== 优惠券积分单独用表来管理了, 此段需要重构 ======================================

    /**
     * 添加订单项.
     *
     * @param orderItem
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItemList.add(orderItem);
    }

    /**
     * 检查订单项对应的商品, 操作库存, 创建订单项
     *
     * @param orderWriteService 创建订单, 订单项, 历史记录
     * @param skuService        查询 Sku 信息 及 扣减库存
     */
    public void createOrderItemsAndOperateStorage(OrderWriteService orderWriteService, SkuService skuService, OrderQueryService orderQueryService) {
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderId(id);
            // 订单项状态
            orderItem.setOrderState(orderState);

            OrderAssert.assertOrderItem(orderItem, this);
            // 创建订单项
            orderWriteService.createOrderItem(orderItem);

            // 通过策略操作库存
            orderItem.getStoreStrategy().operateStorageWhenCreateOrder(skuService, orderItem.getSkuId(),
                    orderItem.getStorageId(), orderItem.getNumber(), payType != PayType.OnLine);

            try {
                orderItem.recordPayNumber(orderWriteService, orderQueryService);
            } catch (Exception e) {
                LOGGER.error("附加(skuId:" + orderItem.getSkuId() + ", productId:" + orderItem.getProductId() + ")付款数量(" + orderItem.getNumber() + ")时异常:", e);
            }
        }
    }

    /**
     * 订单项状态置为取消或关闭, 操作库存
     *
     * @param orderWriteService 创建订单项, 历史记录
     * @param skuService        操作库存
     */
    public void cancelOrCloseOrderItems(OrderWriteService orderWriteService, SkuService skuService) {
        for (OrderItem orderItem : orderItemList) {
            rebuildOperateOrderItem(orderWriteService, orderItem);
            // 操作库存
            orderItem.getStoreStrategy().operateStorageWhenCancelOrder(skuService, orderItem.getSkuId(),
                    orderItem.getStorageId(), orderItem.getNumber(), payType != PayType.OnLine);
        }
    }

    /**
     * 更新订单项状态为已支付, 操作库存
     *
     * @param orderWriteService 创建订单, 订单项, 历史记录
     * @param skuService        操作库存
     */
    public void payOrderItems(OrderWriteService orderWriteService, SkuService skuService, OrderQueryService orderQueryService) {
        for (OrderItem orderItem : orderItemList) {
            rebuildOperateOrderItem(orderWriteService, orderItem);
            // 操作库存
            orderItem.getStoreStrategy().operateStorageWhenPayOrder(skuService, orderItem.getSkuId(), orderItem.getStorageId(), orderItem.getNumber());

            try {
                orderItem.recordPayNumber(orderWriteService, orderQueryService);
            } catch (Exception e) {
                LOGGER.error("附加(skuId:" + orderItem.getSkuId() + ", productId:" + orderItem.getProductId() + ")付款数量(" + orderItem.getNumber() + ")时异常:", e);
            }
        }
    }

    /**
     * 订单交易完成. 统计销售量.
     *
     * @param orderWriteService
     * @param orderQueryService
     */
    public void successOrderItems(OrderWriteService orderWriteService, OrderQueryService orderQueryService) {
        for (OrderItem orderItem : orderItemList) {
            rebuildOperateOrderItem(orderWriteService, orderItem);

            // 统计销售量, 若统计时出现异常, 不能影响前面的订单状态更新.
            try {
                orderItem.recordSalesNumber(orderWriteService, orderQueryService);
            } catch (Exception e) {
                LOGGER.error("附加(skuId:" + orderItem.getSkuId() + ", productId:" + orderItem.getProductId() + ")销售数量(" + orderItem.getNumber() + ")时异常:", e);
            }
        }
    }

    private void rebuildOperateOrderItem(OrderWriteService orderWriteService, OrderItem orderItem) {
        OrderState oldItemState = orderItem.getOrderState();
        orderItem.setOrderState(orderState);
        orderItem.setMustPreviousState(mustPreviousState);
        if (orderWriteService.updateOrderItemState(orderItem.getId(), orderItem.getOrderState(), orderItem.getMustPreviousState()) != 1) {
            LOGGER.error("订单[" + orderNo + "]项[" + orderItem.getSkuName() + "]不能从["
                    + oldItemState.serviceDesc() + "]变更为[" + orderItem.getOrderState().serviceDesc()
                    + "](只能从[" + orderItem.getMustPreviousState().serviceDesc() + "]变更)");
            // 订单项的值可能会不一样, 此时只需要记录一下即可. 不能往外抛出异常
        }
    }

    /**
     * 更新订单项状态(以 order 的 mustPreviousState 和 orderState 值为基础进行更新)
     *
     * @param orderWriteService
     */
    public void updateOrderItemsState(OrderWriteService orderWriteService) {
        for (OrderItem orderItem : orderItemList) {
            rebuildOperateOrderItem(orderWriteService, orderItem);
        }
    }

    /**
     * 查询订单是否有评价, 若所有的订单项都已评, 则返回 true.
     *
     * @return
     */
    public boolean isAppraise() {
        for (OrderItem orderItem : orderItemList) {
            // 发现有未评价的则返回 false
            if (!orderItem.isAppraise())
                return false;
        }
        return true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(long orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 订单所发的物流公司(顺风, 中通等), 与订单物流表中冗余
     */
    public DeliveryInfo.DeliveryType getDeliveryType() {
        return deliveryType;
    }

    /**
     * 订单所发的物流公司(顺风, 中通等), 与订单物流表中冗余
     */
    public void setDeliveryType(DeliveryInfo.DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    /**
     * 原先的订单状态, 只在更新 SQL 时有效, 不写入数据库
     */
    public OrderState getMustPreviousState() {
        return mustPreviousState;
    }

    /**
     * 原先的订单状态, 只在更新 SQL 时有效, 不写入数据库
     */
    public void setMustPreviousState(OrderState mustPreviousState) {
        this.mustPreviousState = mustPreviousState;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    /**
     * 获取单个订单项
     */
    public OrderItem getOrderItem(long orderItemId) {
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getId() == orderItemId) {
                return orderItem;
            }
        }
        return null;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Logistics getLogistics() {
        return logistics;
    }

    public void setLogistics(Logistics logistics) {
        this.logistics = logistics;
    }

    public PayType getPayType() {
        return payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }

    public long getMilliDate() {
        return milliDate;
    }

    public void setMilliDate(long milliDate) {
        this.milliDate = milliDate;
    }

    public InvoiceInfo getInvoiceInfo() {
        return invoiceInfo;
    }

    public void setInvoiceInfo(InvoiceInfo invoiceInfo) {
        this.invoiceInfo = invoiceInfo;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
        this.milliDate = createDate.getTime();

    }

    /**
     * 更新时间
     */
    public Date getModifyDate() {
        return modifyDate;
    }

    /**
     * 更新时间
     */
    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(Date cancelDate) {
        this.cancelDate = cancelDate;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getStorageId() {
        return storageId;
    }

    public void setStorageId(int storageId) {
        this.storageId = storageId;
    }

    public long getVirtualOrderId() {
        return virtualOrderId;
    }

    public void setVirtualOrderId(long virtualOrderId) {
        this.virtualOrderId = virtualOrderId;
    }

    public PayBank getPayBank() {
        return payBank;
    }

    public void setPayBank(PayBank payBank) {
        this.payBank = payBank;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    /**
     * 使用现金券或积分时记录其订单价格相关说明
     */
    public String getPriceMessage() {
        return priceMessage;
    }

    /**
     * 使用现金券或积分时记录其订单价格相关说明
     */
    public void setPriceMessage(String priceMessage) {
        this.priceMessage = priceMessage;
    }

    /**
     * 使用现金券或积分时记录其订单价格相关详细说明
     */
    public String getPriceMessageDetail() {
        return priceMessageDetail;
    }

    /**
     * 使用现金券或积分时记录其订单价格相关详细说明
     */
    public void setPriceMessageDetail(String priceMessageDetail) {
        this.priceMessageDetail = priceMessageDetail;
    }

    /**
     * 积分比例
     */
    public double getIntegralPercent() {
        return integralPercent;
    }

    /**
     * 积分比例
     */
    public void setIntegralPercent(double integralPercent) {
        this.integralPercent = integralPercent;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderItemList=" + orderItemList +
                ", logistics=" + logistics +
                ", payType=" + payType +
                ", payBank=" + payBank +
                ", invoiceInfo=" + invoiceInfo +
                ", valid=" + valid +
                ", totalPrice='" + totalPrice + '\'' +
                ", orderState=" + orderState +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", accountType=" + accountType +
                ", createDate=" + createDate +
                ", modifyDate=" + modifyDate +
                ", payDate=" + payDate +
                ", endDate=" + endDate +
                ", cancelDate=" + cancelDate +
                ", milliDate=" + milliDate +
                ", customerId=" + customerId +
                ", supplierName='" + supplierName + '\'' +
                ", storageId=" + storageId +
                ", storageName='" + storageName + '\'' +
                ", virtualOrderId=" + virtualOrderId +
                ", orderNo=" + orderNo +
                ", deliveryType=" + deliveryType +
                ", mustPreviousState=" + mustPreviousState +
                ", coupon=" + coupon +
                '}';
    }
}
