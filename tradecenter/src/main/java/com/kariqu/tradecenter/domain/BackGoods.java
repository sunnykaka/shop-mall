package com.kariqu.tradecenter.domain;

import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.excepiton.BackGoodsBaseException;
import com.kariqu.tradecenter.service.BackGoodsQueryService;
import com.kariqu.tradecenter.service.BackGoodsWriteService;
import com.kariqu.tradecenter.service.OrderQueryService;
import com.kariqu.usercenter.domain.AccountType;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 退货单数据.
 */
public class BackGoods {

    /**
     * 退货单编号
     */
    private Long id;

    /**
     * 订单编号
     */
    private Long orderNo;

    /**
     * 冗余订单ID
     */
    private Long orderId;


    /**
     * 用户Id
     */
    private Integer userId;

    /**
     * 用户名(第三方账户可能会重复)
     */
    private String userName;

    /**
     * 用户账户类型(账户类型, QQ、weibo 等, KRQ 代表我们自己)
     */
    private AccountType accountType;

    /**
     * 退货单物流编号
     */
    private String expressNo;


    /**
     *退货的详细描述
     */
    private String backReason;


    /**
     * 退货原因  质量问题    非质量问题
     */
    private BackReason backReasonReal;

    /**
     *退货的处理方式  退货 退款 保修  目前数据库还没有此字段，以后可能会加
     */
    private ProcessMode processMode;

    /**
     *  退货地址
     */
    private String backAddress;

    /**
     * 联系人姓名
     */
    private String backShopperName;

    /**
     * 联系人号码
     */
    private String backPhone;

    /**
     * 退货金额
     */
    private Long backPrice = 0l;

    /**
     * 上传图片的地址
     */
    private String uploadFiles;

    /**
     * 退货状态
     */
    private BackGoodsState backState;

    /**
     * 退货类型, 已发货 或 未发货. 默认是已发货
     */
    private BackGoodsState.BackGoodsType backType = BackGoodsState.BackGoodsType.YetSend;

    /**
     * 必须的订单前置状态, 主要在 SQL 更新时, 不写入数据库.
     */
    private BackGoodsState mustPreviousState;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 退货单项
     */
    private List<BackGoodsItem> backGoodsItemList = new ArrayList<BackGoodsItem>();

    public void addBackGoodsItem(BackGoodsItem backGoodsItem) {
        backGoodsItemList.add(backGoodsItem);
    }

    /**
     * 检查退货单是否能退款.
     *
     * @return 若不能退款则返回 true.
     */
    public boolean checkCanNotRefunds() {
        return backType.checkCanNotRefunds(backState);
    }

    /**
     * 创建退货单项.
     */
    public void createBackItems(BackGoodsWriteService backGoodsWriteService) throws BackGoodsBaseException {
        for (BackGoodsItem backItem : backGoodsItemList) {
            // 创建退货单项
            backItem.setBackGoodsId(id);

            backGoodsWriteService.createBackGoodsItem(backItem);
        }
    }

    /** 检查退货项数据, 并统计退货价格 */
    public void checkBackItemAndCalculatePrice(OrderQueryService orderQueryService, BackGoodsQueryService backGoodsQueryService)
            throws BackGoodsBaseException {
        // 判断退货数量
        List<Long> backGoodsIdList = backGoodsQueryService.queryBackGoodsIdByOrderNoAndUserId(orderNo, userId);
        for (BackGoodsItem backGoodsItem : backGoodsItemList) {
            // 判断退货单项数量和是否有退过货
            backGoodsItem.checkSpecification(backGoodsIdList, orderQueryService, backGoodsQueryService);

            backPrice += backGoodsItem.totalPrice();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getUploadFiles() {
        return uploadFiles;
    }

    public void setUploadFiles(String uploadFiles) {
        this.uploadFiles = uploadFiles;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public BackReason getBackReasonReal() {
        return backReasonReal;
    }

    public void setBackReasonReal(BackReason backReasonReal) {
        this.backReasonReal = backReasonReal;
    }

    public String getBackAddress() {
        return backAddress;
    }

    public void setBackAddress(String backAddress) {
        this.backAddress = backAddress;
    }

    public String getBackReason() {
        return backReason;
    }

    public void setBackReason(String backReason) {
        this.backReason = backReason;
    }

    public ProcessMode getProcessMode() {
        return processMode;
    }

    public void setProcessMode(ProcessMode processMode) {
        this.processMode = processMode;
    }

    public String getBackShopperName() {
        return backShopperName;
    }

    public void setBackShopperName(String backShopperName) {
        this.backShopperName = backShopperName;
    }

    public String getBackPhone() {
        return backPhone;
    }

    public void setBackPhone(String backPhone) {
        this.backPhone = backPhone;
    }

    public Long getBackPrice() {
        return backPrice;
    }

    public void setBackPrice(Long backPrice) {
        this.backPrice = backPrice;
    }

    public String getBackPriceByMoney() {
        Money money = new Money();
        money.setCent(backPrice);
        return money.toString();
    }

    public BackGoodsState getBackState() {
        return backState;
    }

    public void setBackState(BackGoodsState backState) {
        this.backState = backState;
    }

    /**
     * 退货类型, 已发货 或 未发货. 默认是已发货
     */
    public BackGoodsState.BackGoodsType getBackType() {
        return backType;
    }

    /**
     * 退货类型, 已发货 或 未发货. 默认是已发货
     */
    public void setBackType(BackGoodsState.BackGoodsType backType) {
        this.backType = backType;
    }

    /**
     * 必须的订单前置状态, 主要在 SQL 更新时, 不写入数据库.
     */
    public BackGoodsState getMustPreviousState() {
        return mustPreviousState;
    }

    /**
     * 必须的订单前置状态, 主要在 SQL 更新时, 不写入数据库.
     */
    public void setMustPreviousState(BackGoodsState mustPreviousState) {
        this.mustPreviousState = mustPreviousState;
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

    public List<BackGoodsItem> getBackGoodsItemList() {
        return backGoodsItemList;
    }

    public void setBackGoodsItemList(List<BackGoodsItem> backGoodsItemList) {
        this.backGoodsItemList = backGoodsItemList;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public static enum BackReason {
        /**
         * 质量问题
         */
        QualityProblem,

        /**
         * 非质量问题
         */
        NoQualityProblem;

        private static Map<BackReason, String> mapping = new HashMap<BackReason, String>();

        static {
            mapping.put(QualityProblem, "质量问题");
            mapping.put(NoQualityProblem, "非质量问题");
        }

        public String toDesc() {
            return mapping.get(this);
        }

        }

    public static enum ProcessMode {
        /**
         * 退货
         */
        BackGood("退货"),
        /**
         * 退款
         */
        Reimburse("退款"),
        /**
         * 保修
         */
        repair("保修");

        ProcessMode(String value) {
            this.value = value;
        }

        private String value;

    }

    public static class UploadFiles {
        private String original;

        private String compress;

        public UploadFiles(){}

        public UploadFiles(String original, String compress) {
            this.original = original;
            this.compress = compress;
        }

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }

        public String getCompress() {
            return compress;
        }

        public void setCompress(String compress) {
            this.compress = compress;
        }
    }


}
