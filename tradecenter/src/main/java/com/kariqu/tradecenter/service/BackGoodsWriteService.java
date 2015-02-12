package com.kariqu.tradecenter.service;

import com.kariqu.tradecenter.domain.BackGoods;
import com.kariqu.tradecenter.domain.BackGoodsItem;
import com.kariqu.tradecenter.excepiton.BackGoodsBaseException;

import java.io.InputStream;

/**
 * 退货单单写入服务
 */
public interface BackGoodsWriteService {

    /**
     * 创建退货单(需要判断是否是此用户的订单在退货)、退货单项、退货单历史.<br/>
     * 前置条件 : backGoods 中需要带上 backGoodsItem 的信息, 需要带上创建者的信息
     *
     * @param backGoods 退货单及退货单项.
     * @exception BackGoodsBaseException
     */
    void createNewBackGoods(BackGoods backGoods) throws BackGoodsBaseException;
    
    /**
     * 用户取消退货单.
     *
     * @param backGoodsId 退货单id
     * @param userId 用户id
     * @throws BackGoodsBaseException
     */
    void cancelBackGoodsForUser(long backGoodsId, int userId) throws BackGoodsBaseException;

    /**
     * 客服取消(或拒绝)订单.
     *
     * @param backGoodsId 退货单号
     * @param userName 客服的用户名, 主要用来写入历史
     * @param remark 若拒绝, 则需要写入备注.
     * @throws BackGoodsBaseException
     */
    void cancelBackGoodsForCustomerServiceStaff(long backGoodsId, String userName, String remark) throws BackGoodsBaseException;

    /**
     * 客服审核通过 <span style="color:red;">未发货</span> 的退货单.
     *
     * @param backGoodsId 退货单号
     * @param userName 客服的用户名, 主要用来写入历史
     * @param remark 备注
     * @throws BackGoodsBaseException
     */
    void verifyNoSendBackGoods(long backGoodsId, String userName, String remark) throws BackGoodsBaseException;

    /**
     * 客服审核通过 <span style="color:red;">已发货</span> 的退货单.
     *
     * @param backGoodsId 退货单号
     * @param userName 客服的用户名, 主要用来写入历史
     * @param remark 备注
     * @throws BackGoodsBaseException
     */
    void verifyYetSendBackGoods(long backGoodsId, String userName, String remark) throws BackGoodsBaseException;

    /**
     * 客服确认收货
     *
     * @param backGoodsId 退货单号
     * @param userName 客服的用户名, 主要用来写入历史
     * @param expressNo 物流单号
     * @throws BackGoodsBaseException
     */
    void receiveGoods(long backGoodsId, String userName, String expressNo) throws BackGoodsBaseException;

    /**
     * 财务确认打款
     *
     * @param backGoodsId 退货单号
     * @param userName 财务的用户名, 主要用来写入历史
     * @throws BackGoodsBaseException
     */
    BackGoods refundsGoodsForFinance(long backGoodsId, String userName) throws BackGoodsBaseException;

    /**
     * 提交退货申请时生成10的缩略图
     * @param newFileName
     * @param savePosition
     * @return
     */
    String backGoodsImgCompress(String newFileName, String savePosition);

    /**
     *退货提交时保存退单凭证图片
     * @param inputStream
     * @param savePosition
     */
    void saveUploadPicture(InputStream inputStream, String savePosition);

    /**
     * 创建退货单项.
     *
     * @param backGoodsItem
     */
    void createBackGoodsItem(BackGoodsItem backGoodsItem);

}
