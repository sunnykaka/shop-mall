package com.kariqu.tradecenter.domain;

import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.excepiton.BackGoodsNoTransactionalException;
import com.kariqu.tradecenter.service.BackGoodsQueryService;
import com.kariqu.tradecenter.service.OrderQueryService;

import java.util.Date;
import java.util.List;

public class BackGoodsItem {

    private Long id;

    /**
     * 退货单编号
     */
    private Long backGoodsId;

    /**
     * 退货数量
     */
    private Integer number;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 订单详情编号
     */
    private Long orderItemId;

    /**
     * 订单项退货时的单价
     */
    private Long unitPrice;

    /**
     * 记录提交退款单时订单项对应的状态(方便取消退货单时状态还原), 目前已无用处! 因此以 Cancel 做为默认值.
     */
    private OrderState orderState = OrderState.Cancel;

    /**
     * 传递数据的载体, 不与数据库关联
     */
    private OrderItem orderItem;

    /**
     * 创建退货单时, 检查退货项与订单项的相关数据.<br/>
     * 1. 退单项是否有对应的订单项;<br/>
     * 2. 退货的数量是否有误;<br/>
     * 3. 订单项是否退过货.
     *
     * @param backGoodsIdList       此订单所有已经退过货未取消的退货单编号
     * @param orderQueryService     查询订单项
     * @param backGoodsQueryService 查询退货单对应的订单项是否有退过货
     * @throws BackGoodsNoTransactionalException
     *
     */
    public void checkSpecification(List<Long> backGoodsIdList, OrderQueryService orderQueryService,
                                   BackGoodsQueryService backGoodsQueryService) throws BackGoodsNoTransactionalException {
        orderItem = orderQueryService.queryOrderItemsById(orderItemId);
        if (orderItem == null) {
            throw new BackGoodsNoTransactionalException("没有对应的订单项信息!");
        }
        if (number <= 0 || orderItem.getNumber() < number) {
            throw new BackGoodsNoTransactionalException("商品[" + orderItem.getSkuName() + "]的退货数量有误!");
        }
        // 若允许一个 订单项 退多次, 则此处的检查需要更改.
        for (Long backGoodsId : backGoodsIdList) {
            if (backGoodsQueryService.queryByBackOrderItemId(backGoodsId, orderItemId) != 0) {
                throw new BackGoodsNoTransactionalException("商品[" + orderItem.getSkuName() + "]已经退过货!");
            }
        }

        unitPrice = orderItem.getBackUnitPrice();
        orderState = orderItem.getOrderState();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBackGoodsId() {
        return backGoodsId;
    }

    public void setBackGoodsId(Long backGoodsId) {
        this.backGoodsId = backGoodsId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Long unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * 字符串形式的单价
     */
    public String getUnitPriceByMoney() {
        return Money.getMoneyString(unitPrice);
    }

    /**
     * 字符串形式的小计(单价 乘 数量)
     */
    public String getSubtotalPrice() {
        return Money.getMoneyString(totalPrice());
    }

    public long totalPrice() {
        return number * unitPrice;
    }

    public Integer getNumber() {
        return number;
    }

    public String getNumberByString(){
        return Integer.toString(number);
    }
    public void setNumber(Integer number) {
        this.number = number;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }
}