package com.kariqu.tradecenter.domain;

import com.kariqu.productcenter.domain.*;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.productcenter.service.SkuStorageService;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.domain.payment.SkuTradeResult;
import com.kariqu.tradecenter.excepiton.OrderNoTransactionalException;
import com.kariqu.tradecenter.service.OrderQueryService;
import com.kariqu.tradecenter.service.OrderWriteService;
import com.kariqu.usercenter.domain.User;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * User: Asion
 * Date: 11-10-11
 * Time: 下午8:52
 */
public class OrderItem extends TradeItem implements Comparable<OrderItem> {

    private long id;

    private long orderId;//订单ID

    /**
     * 活动Id
     */
    private long skuMarketingId;

    /**
     * 商品活动类型(意原购, 限时折扣等)
     */
    private ProductActivityType skuActivityType = ProductActivityType.Normal;

    /*此字段专用积分兑换、积分优惠购活动 活动id*/
    private int activityId;

    /**此字段专用积分兑换、积分优惠购活动  用户能购买的次数*/
    private int userBuyCount;

    /**此字段专用积分兑换、积分优惠购活动   积分*/
    private Long integral;

    /**
     * 下单时的商品单价(若有活动则使用活动价格)
     */
    private long unitPrice = 0;

    /**
     * 实际发货数量
     */
    private int shipmentNum;

    /**
     * 已退款数量
     */
    private int backNum;

    /**
     * 订单创建时使用的库存策略(0.普通策略, 仓库即扣减库存, 取消则回加库存; 1.付款策略, 付款成功后才会扣减)
     */
    private StoreStrategy storeStrategy;

    /**
     * 针对订单详情的更颗粒状态
     */
    private OrderState orderState;

    /**
     * 原先的订单状态, 只在更新 SQL 时有效, 不写入数据库
     */
    private OrderState mustPreviousState;


    /**
     * 订单项总额(理论上=单价*数量, 若有使用积分或现金券, 则分摊至单个订单项)
     */
    private long itemTotalPrice;

    private String skuExplain;//sku说明

    private String skuMainPicture; //主图

    private String skuName;//商品名字

    private String barCode;//条形码

    private String itemNo;//编号

    private int storageId;//库存位置

    private int productId;//冗余商品ID

    private int categoryId;//类目ID

    private int customerId;//商家ID

    private int brandId;//品牌ID

    /**
     * 是否已评价(0 false 表示未评价, 1 true 表示已评价), 默认是 0
     */
    private boolean appraise = false;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 现金券针对每个订单项分摊的金额(不存入db, 只做为计算的载体)
     */
    private long couponApportion;

    /**
     * 积分针对每个订单项分摊的金额(不存入db, 只做为计算的载体)
     */
    private long integralApportion;

    /**
     * 检查商品及 sku 信息.
     *
     * @param productService
     * @param skuService
     * @throws OrderNoTransactionalException
     */
    public void checkAndInit(ProductService productService, SkuService skuService, SkuStorageService skuStorageService) throws OrderNoTransactionalException {
        // 所有需要 product 赋值的数据全在此处.
        StockKeepingUnit sku = skuService.getStockKeepingUnit(getSkuId());
        if (sku == null) {
            throw new OrderNoTransactionalException("商品已经不存在!");
        }

        if (sku.getSkuState() == StockKeepingUnit.SKUState.REMOVED) {
            throw new OrderNoTransactionalException("商品已经无效!");
        }

        // 商品Id
        productId = sku.getProductId();
        // 商品
        Product product = productService.getSimpleProductById(productId);
        if (product == null) {
            throw new OrderNoTransactionalException("商品已经不存在!");
        }
        // 商品名字
        skuName = product.getName();
        if (!product.isOnline()) {
            throw new OrderNoTransactionalException("商品(" + skuName + ")已下架!");
        }

        // sku 库存
        SkuStorage skuStorage = skuStorageService.getSkuStorage(getSkuId());
        if (skuStorage == null || skuStorage.getStockQuantity() <= 0) {
            throw new OrderNoTransactionalException("商品(" + skuName + ")无库存!");
        }
        if (skuStorage.getTradeMaxNumber() > 0 && number > skuStorage.getTradeMaxNumber()) {
            throw new OrderNoTransactionalException(String.format("商品(%s)一次只允许购买 %s 个!", skuName, skuStorage.getTradeMaxNumber()));
        }
        if (number > skuStorage.getStockQuantity()) {
            throw new OrderNoTransactionalException(String.format("商品(%s)的库存只有 %s 个!", skuName, skuStorage.getStockQuantity()));
        }

        // 库存位置, 由库存位置区分多个商品提交过来时的分单
        storageId = skuStorage.getProductStorageId();
        // 库存策略
        storeStrategy = product.getStoreStrategy();
        // 现在还没有计算积分和现金券, 单纯累积
        itemTotalPrice = totalPrice();
        // sku 说明
        skuExplain = skuService.getSkuPropertyToString(sku);

        // sku 图片
        ProductPicture productPicture = productService.getMainPictureBySKuId(getSkuId(), product.getId());
        // sku 主图的路径
        skuMainPicture = (productPicture == null) ? "" : productPicture.getPictureUrl();
        // 条形码
        barCode = StringUtils.isBlank(sku.getBarCode()) ? "未填" : sku.getBarCode();
        // 商品编号, 若未设置 sku 编码, 则使用商品编码
        String code = "未填";
        if (StringUtils.isNotBlank(sku.getSkuCode()))
            code = sku.getSkuCode();
        else if (StringUtils.isNotBlank(product.getProductCode()))
            code = product.getProductCode();
        itemNo = code;
        // 类目Id
        categoryId = product.getCategoryId();
        // 商家Id
        customerId = product.getCustomerId();
        // 品牌Id
        brandId = product.getBrandId();
    }

    /**
     * sku用户购买次数校验
     * @throws OrderNoTransactionalException
     */
    public void checkCurrencyActivity(int userHasBuyCount) throws OrderNoTransactionalException{
        if(userHasBuyCount >= userBuyCount){
            throw new OrderNoTransactionalException("商品正在参加积分商城超值兑换活动, 在此期间, 您只能购买" + userBuyCount + "次!");
        }
    }

    /**
     * 统计销售数.
     */
    public void recordSalesNumber(OrderWriteService orderWriteService, OrderQueryService orderQueryService) {
        if (orderState != OrderState.Success) return;

        SkuTradeResult tradeResult = orderQueryService.querySkuTradeResultBySkuId(getSkuId());
        if (tradeResult == null) {
            SkuTradeResult skuTradeResult = new SkuTradeResult(getSkuId(), productId);
            skuTradeResult.setNumber(number);
            orderWriteService.insertSkuTradeResult(skuTradeResult);
        } else {
            // 数量累加
            tradeResult.appendedNumber(number);
            orderWriteService.updateSkuTradeResult(tradeResult);
        }
    }

    /**
     * 统计付款成功数.
     */
    public void recordPayNumber(OrderWriteService orderWriteService, OrderQueryService orderQueryService) {
        if (orderState != OrderState.Pay) return;

        SkuTradeResult tradeResult = orderQueryService.querySkuTradeResultBySkuId(getSkuId());
        if (tradeResult == null) {
            SkuTradeResult skuTradeResult = new SkuTradeResult(getSkuId(), productId);
            skuTradeResult.setPayNumber(number);
            orderWriteService.insertSkuTradeResult(skuTradeResult);
        } else {
            // 数量累加
            tradeResult.appendedPayNumber(number);
            orderWriteService.updateSkuTradeResult(tradeResult);
        }
    }

    /**
     * 统计退货数量
     */
    public void recordBackNumber(int backNumber, OrderWriteService orderWriteService, OrderQueryService orderQueryService) {
        SkuTradeResult tradeResult = orderQueryService.querySkuTradeResultBySkuId(getSkuId());
        if (tradeResult == null) {
            SkuTradeResult skuTradeResult = new SkuTradeResult(getSkuId(), productId);
            skuTradeResult.setPayNumber(backNumber);
            orderWriteService.insertSkuTradeResult(skuTradeResult);
        } else {
            tradeResult.appendedBackNumber(backNumber);
            orderWriteService.updateSkuTradeResult(tradeResult);
        }
    }

    public String getSubtotalPrice() {
        return priceByMoney(totalPrice());
    }

    /**
     * 订单项单价rmb元的字符串表示
     * @return
     */
    public String getUnitPriceByMoney() {
        return priceByMoney(unitPrice);
    }

    private String priceByMoney(long price) {
        return Money.getMoneyString(price);
    }

    /**
     * 此订单项小计价格的rmb元的字符串表示
     * @return
     */
    public String getTotalPriceByMoney() {
        return priceByMoney(this.totalPrice());
    }

    public long totalPrice() {
        return number * unitPrice;
    }

    /**
     * 订单项退货时的单价
     */
    public long getBackUnitPrice() {
        // 若记录的 订单项总价 与 (单价 * 数量) 不符, 则使用订单项总价除以购买数量
        return (itemTotalPrice != totalPrice()) ? (itemTotalPrice / number) : unitPrice;
    }

    /**
     * 下单时的商品单价(若有活动则使用活动价格)
     */
    public long getUnitPrice() {
        return unitPrice;
    }

    /**
     * 下单时的商品单价(若有活动则使用活动价格)
     */
    public void setUnitPrice(long unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * 订单项总额(理论上=单价*数量, 若有使用积分或现金券, 则分摊至单个订单项, 并记录总额)
     */
    public long getItemTotalPrice() {
        return itemTotalPrice;
    }

    /**
     * 订单项总额(理论上=单价*数量, 若有使用积分或现金券, 则分摊至单个订单项, 并记录总额)
     */
    public void setItemTotalPrice(long itemTotalPrice) {
        this.itemTotalPrice = itemTotalPrice;
    }

    public String getSkuExplain() {
        return skuExplain;
    }

    public void setSkuExplain(String skuExplain) {
        this.skuExplain = skuExplain;
    }

    public String getSkuMainPicture() {
        return skuMainPicture;
    }

    public void setSkuMainPicture(String skuMainPicture) {
        this.skuMainPicture = skuMainPicture;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    /**
     * 订单创建时使用的库存策略(0.普通策略, 仓库即扣减库存, 取消则回加库存; 1.付款策略, 付款成功后才会扣减)
     */
    public StoreStrategy getStoreStrategy() {
        return storeStrategy;
    }

    /**
     * 订单创建时使用的库存策略(0.普通策略, 仓库即扣减库存, 取消则回加库存; 1.付款策略, 付款成功后才会扣减)
     */
    public void setStoreStrategy(StoreStrategy storeStrategy) {
        this.storeStrategy = storeStrategy;
    }

    /**
     * 针对订单详情的更颗粒状态
     */
    public OrderState getOrderState() {
        return orderState;
    }

    /**
     * 针对订单详情的更颗粒状态
     */
    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    /**
     * 必须的订单前置状态, 只在更新 SQL 时有效, 不写入数据库
     */
    public OrderState getMustPreviousState() {
        return mustPreviousState;
    }

    /**
     * 必须的订单前置状态, 只在更新 SQL 时有效, 不写入数据库
     */
    public void setMustPreviousState(OrderState mustPreviousState) {
        this.mustPreviousState = mustPreviousState;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * 实际发货数量
     */
    public int getShipmentNum() {
        return shipmentNum;
    }

    /**
     * 实际发货数量
     */
    public void setShipmentNum(int shipmentNum) {
        this.shipmentNum = shipmentNum;
    }

    /**
     * 已退款数量
     */
    public int getBackNum() {
        return backNum;
    }

    /**
     * 已退款数量
     */
    public void setBackNum(int backNum) {
        this.backNum = backNum;
    }

    /**
     * 判断此订单详情是否可以退货
     *
     * @return 可以退则返回 true.
     */
    public boolean canBack() {
        return number > backNum;
    }

    /**
     * 可退数量
     *
     * @return
     */
    public int getReturnsNum() {
        return number - backNum;
    }

    public int getStorageId() {
        return storageId;
    }

    public void setStorageId(int storageId) {
        this.storageId = storageId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    /**
     * 活动Id
     */
    public long getSkuMarketingId() {
        return skuMarketingId;
    }

    /**
     * 活动Id
     */
    public void setSkuMarketingId(long skuMarketingId) {
        this.skuMarketingId = skuMarketingId;
    }

    /**
     * 商品活动类型(意原购, 限时折扣等)
     */
    public ProductActivityType getSkuActivityType() {
        return skuActivityType;
    }

    /**
     * 商品活动类型(意原购, 限时折扣等)
     */
    public void setSkuActivityType(ProductActivityType skuActivityType) {
        this.skuActivityType = skuActivityType;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getUserBuyCount() {
        return userBuyCount;
    }

    public void setUserBuyCount(int userBuyCount) {
        this.userBuyCount = userBuyCount;
    }

    public Long getIntegral() {
        return integral;
    }

    public void setIntegral(Long integral) {
        this.integral = integral;
    }

    /**
     * 是否已评价(0 false 表示未评价, 1 true 表示已评价), 默认是 0
     */
    public boolean isAppraise() {
        return appraise;
    }

    /**
     * 是否已评价(0 false 表示未评价, 1 true 表示已评价), 默认是 0
     */
    public void setAppraise(boolean appraise) {
        this.appraise = appraise;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * 现金券针对每个订单项分摊的金额(不存入db, 只做为计算的载体)
     */
    public long getCouponApportion() {
        return couponApportion;
    }

    /**
     * 现金券针对每个订单项分摊的金额(不存入db, 只做为计算的载体)
     */
    public void setCouponApportion(long couponApportion) {
        this.couponApportion = couponApportion;
    }

    /**
     * 积分针对每个订单项分摊的金额(不存入db, 只做为计算的载体)
     */
    public long getIntegralApportion() {
        return integralApportion;
    }

    /**
     * 积分针对每个订单项分摊的金额(不存入db, 只做为计算的载体)
     */
    public void setIntegralApportion(long integralApportion) {
        this.integralApportion = integralApportion;
    }

    @Override
    public int compareTo(OrderItem o) {
        return (int) (totalPrice() - o.totalPrice());
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", skuMarketingId=" + skuMarketingId +
                ", skuActivityType=" + skuActivityType +
                ", skuId=" + getSkuId() +
                ", unitPrice=" + unitPrice +
                ", number=" + number +
                ", shipmentNum=" + shipmentNum +
                ", backNum=" + backNum +
                ", storeStrategy=" + storeStrategy +
                ", orderState=" + orderState +
                ", mustPreviousState=" + mustPreviousState +
                ", itemTotalPrice=" + itemTotalPrice +
                ", skuExplain='" + skuExplain + '\'' +
                ", skuMainPicture='" + skuMainPicture + '\'' +
                ", skuName='" + skuName + '\'' +
                ", barCode='" + barCode + '\'' +
                ", itemNo='" + itemNo + '\'' +
                ", storageId=" + storageId +
                ", productId=" + productId +
                ", categoryId=" + categoryId +
                ", customerId=" + customerId +
                ", brandId=" + brandId +
                ", appraise=" + appraise +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                '}';
    }
}
