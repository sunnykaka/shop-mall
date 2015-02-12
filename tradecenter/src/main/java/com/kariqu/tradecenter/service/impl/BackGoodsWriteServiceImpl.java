package com.kariqu.tradecenter.service.impl;

import com.kariqu.common.file.PictureToSmall;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.excepiton.BackGoodsBaseException;
import com.kariqu.tradecenter.excepiton.BackGoodsNoTransactionalException;
import com.kariqu.tradecenter.excepiton.BackGoodsTransactionalException;
import com.kariqu.tradecenter.repository.BackGoodsItemRepository;
import com.kariqu.tradecenter.repository.BackGoodsLogRepository;
import com.kariqu.tradecenter.repository.BackGoodsRepository;
import com.kariqu.tradecenter.service.BackGoodsQueryService;
import com.kariqu.tradecenter.service.BackGoodsWriteService;
import com.kariqu.tradecenter.service.OrderQueryService;
import magick.MagickImage;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 写入退货单的服务类
 */
public class BackGoodsWriteServiceImpl implements BackGoodsWriteService {

    private static final Logger LOGGER = Logger.getLogger(BackGoodsWriteServiceImpl.class);

    @Autowired
    private BackGoodsQueryService backGoodsQueryService;

    @Autowired
    private OrderQueryService orderQueryService;

    @Autowired
    private BackGoodsRepository backGoodsRepository;

    @Autowired
    private BackGoodsItemRepository backGoodsItemRepository;

    @Autowired
    private BackGoodsLogRepository backGoodsLogRepository;

    @Override
    @Transactional(rollbackFor = BackGoodsTransactionalException.class)
    public void createNewBackGoods(BackGoods backGoods) throws BackGoodsBaseException {
        // 检查退货单是否需要退货
        Order order = orderQueryService.getOrderByOrderNo(backGoods.getOrderNo());
        if (order == null || backGoods.getUserId() != order.getUserId()) {
            throw new BackGoodsNoTransactionalException("您没有此订单(" + backGoods.getOrderNo() + ")!");
        }
        if (order.getOrderState().checkCanNotBack()) {
            throw new BackGoodsNoTransactionalException("订单(" + backGoods.getOrderNo() + ")不需要退货!");
        }
        if (Money.YuanToCent(order.getTotalPrice()) == 0) {
            throw new BackGoodsNoTransactionalException("订单(" + backGoods.getOrderNo() + ")的付款金额为 0, 不需要退货!");
        }

        // 设置缺少的数据.
        backGoods.setOrderId(order.getId());
        backGoods.setOrderNo(order.getOrderNo());

        // 订单状态判断是否有发货. 流到商家的订单由客服人工审核
        if (order.getOrderState().checkNotSend()) {
            // 若未发货则由系统审核通过
            backGoods.setBackState(BackGoodsState.Verify);
            backGoods.setBackType(BackGoodsState.BackGoodsType.NoSend);

            checkItemAndCreateGoodsAndLog(backGoods);

            // 系统审核通过
            createBackGoodsLog(new BackGoodsLog(backGoods, "系统", "审核通过", StringUtils.EMPTY));
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("用户(" + backGoods.getUserName() + ") 订单(" + backGoods.getOrderNo() + ") 提交退货, 此时商品未发货, 由系统审核通过!");
            }
        } else {
            // 创建退货单
            backGoods.setBackState(BackGoodsState.Create);
            // 交易成功必须是已经发过货的. 其他情况在商家处由客服去线下操作.
            if (order.getOrderState() == OrderState.Success)
                backGoods.setBackType(BackGoodsState.BackGoodsType.YetSend);

            checkItemAndCreateGoodsAndLog(backGoods);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("用户(" + backGoods.getUserName() + ") 订单(" + backGoods.getOrderNo() + ") 提交退货.");
            }
        }
    }

    private void checkItemAndCreateGoodsAndLog(BackGoods backGoods) throws BackGoodsBaseException {
        // 检查退单项相关数据 并计算总价
        backGoods.checkBackItemAndCalculatePrice(orderQueryService, backGoodsQueryService);

        backGoodsRepository.insert(backGoods);
        // 检查退货单对应的退货单项是否有退过货, 数量是否合理, 而后创建退货单项.
        backGoods.createBackItems(this);
        // 记录历史操作
        createBackGoodsLog(new BackGoodsLog(backGoods, BackGoodsState.Create, backGoods.getUserName(), "创建退货单", StringUtils.EMPTY));
    }

    @Override
    @Transactional(rollbackFor = BackGoodsTransactionalException.class)
    public void cancelBackGoodsForUser(long backGoodsId, int userId) throws BackGoodsBaseException {
        BackGoods backGoods = backGoodsQueryService.queryBackGoodsById(backGoodsId);
        if (backGoods == null || backGoods.getUserId() != userId) {
            throw new BackGoodsNoTransactionalException("您没有此退货单!");
        }
        if (backGoods.getBackState().checkCanNotCancelForUser()) {
            throw new BackGoodsNoTransactionalException("此退货单不能取消! 如需操作, 请联系客服");
        }

        updateGoodsState(backGoods, BackGoodsState.Cancel, BackGoodsState.Create);
        createBackGoodsLog(new BackGoodsLog(backGoods, backGoods.getUserName(), "取消退货单", StringUtils.EMPTY));
    }

    @Override
    @Transactional(rollbackFor = BackGoodsTransactionalException.class)
    public void cancelBackGoodsForCustomerServiceStaff(long backGoodsId, String userName, String remark) throws BackGoodsBaseException {
        BackGoods backGoods = checkBackGoods(backGoodsId);

        if (backGoods.getBackState().checkCanNotCancelForCustomerService()) {
            throw new BackGoodsNoTransactionalException("退货单(" + backGoodsId + ")已是最终状态, 不能取消!");
        }

        // 客服取消退货单时没有限制.
        updateGoodsState(backGoods, BackGoodsState.Cancel, null);
        createBackGoodsLog(new BackGoodsLog(backGoods, "客服", "取消退货单", remark));
    }

    @Override
    @Transactional(rollbackFor = BackGoodsTransactionalException.class)
    public void verifyNoSendBackGoods(long backGoodsId, String userName, String remark) throws BackGoodsBaseException {
        verifyBackGoods(backGoodsId, userName, BackGoodsState.BackGoodsType.NoSend, remark);
    }

    private void verifyBackGoods(long backGoodsId, String userName,
                                 BackGoodsState.BackGoodsType backGoodsType, String remark) throws BackGoodsBaseException {
        BackGoods backGoods = checkBackGoods(backGoodsId);

        backGoods.setBackType(backGoodsType);

        updateGoodsState(backGoods, BackGoodsState.Verify, BackGoodsState.Create);
        createBackGoodsLog(new BackGoodsLog(backGoods, userName, "审核通过", remark));
    }

    @Override
    @Transactional(rollbackFor = BackGoodsTransactionalException.class)
    public void verifyYetSendBackGoods(long backGoodsId, String userName, String remark) throws BackGoodsBaseException {
        verifyBackGoods(backGoodsId, userName, BackGoodsState.BackGoodsType.YetSend, remark);
    }

    @Override
    @Transactional(rollbackFor = BackGoodsTransactionalException.class)
    public void receiveGoods(long backGoodsId, String userName, String expressNo) throws BackGoodsBaseException {
        BackGoods backGoods = checkBackGoods(backGoodsId);

        backGoods.setExpressNo(expressNo);

        updateGoodsState(backGoods, BackGoodsState.Receive, BackGoodsState.Verify);
        createBackGoodsLog(new BackGoodsLog(backGoods, userName, "确认收货", StringUtils.EMPTY));
    }

    @Override
    @Transactional(rollbackFor = BackGoodsTransactionalException.class)
    public BackGoods refundsGoodsForFinance(long backGoodsId, String userName) throws BackGoodsBaseException {
        BackGoods backGoods = checkBackGoods(backGoodsId);

        if (backGoods.checkCanNotRefunds()) {
            throw new BackGoodsNoTransactionalException("退货单(" + backGoodsId + ")不能操作退款.");
        }
        updateGoodsState(backGoods, BackGoodsState.Success, null);
        createBackGoodsLog(new BackGoodsLog(backGoods, userName, "确认打款", StringUtils.EMPTY));

        return backGoods;
    }

    /**
     * 更新退货单单状态(全量更新, 内置状态检查)
     *
     * @param backGoods         退货单
     * @param backGoodsState    要的退货单状态
     * @param mustPreviousState 必须的退货单前置状态, 若赋值为 null 则忽略检查.
     * @throws BackGoodsTransactionalException
     */
    private void updateGoodsState(BackGoods backGoods, BackGoodsState backGoodsState,
                                  BackGoodsState mustPreviousState) throws BackGoodsTransactionalException {
        BackGoodsState oldBackState = backGoods.getBackState();
        backGoods.setBackState(backGoodsState);
        backGoods.setMustPreviousState(mustPreviousState);
        if (backGoodsRepository.update(backGoods) != 1) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("退货单(" + backGoods.getId() + ")不能从当前状态(" + oldBackState.serviceDesc() + ")更新为("
                        + backGoodsState.serviceDesc() + ")(只能从(" + mustPreviousState.serviceDesc() + ")变更)");
            }
            throw new BackGoodsTransactionalException("退货单(" + backGoods.getId() + ")不能从当前状态(" + oldBackState.serviceDesc() + ")更新为("
                    + backGoodsState.serviceDesc() + ")(只能从(" + mustPreviousState.serviceDesc() + ")变更)");
        }
    }

    private BackGoods checkBackGoods(long backGoodsId) throws BackGoodsNoTransactionalException {
        BackGoods backGoods = backGoodsQueryService.queryBackGoodsById(backGoodsId);
        if (backGoods == null) {
            throw new BackGoodsNoTransactionalException("没有此退货单(" + backGoodsId + ")!");
        }
        return backGoods;
    }

    //退货提交时生成用户图片凭证缩略图并返回路径
    @Override
    public String backGoodsImgCompress(String newFileName, String savePosition) {
        String originalName = newFileName.substring(0, newFileName.lastIndexOf("."));
        String originalType = newFileName.substring(newFileName.lastIndexOf("."));
        String suffix = "_10";
        String url = savePosition + originalName + suffix + originalType;
        try{
            BufferedImage bufferedImage = ImageIO.read(new File(savePosition + newFileName));
            ImageIO.write(PictureToSmall.resizeImage(bufferedImage, 0.1), originalType.substring(1), new File(url));
        }catch (Exception e){
            if(LOGGER.isInfoEnabled()){
                LOGGER.info("退单提交生成图片异常" + url);
            }
        }
        return url;
    }


    //退货提交时保存退单凭证图片
    @Override
    public void saveUploadPicture(InputStream inputStream, String savePosition) {
        OutputStream outputStream = null;
        try {
            File pictureFile = new File(savePosition);
            File parentFile = new File(pictureFile.getParent());
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (!pictureFile.exists()) {
                pictureFile.createNewFile();
            }
            outputStream = new FileOutputStream(pictureFile);

            int readBytes;
            byte[] buffer = new byte[10000];
            while ((readBytes = inputStream.read(buffer, 0, 10000)) != -1) {
                outputStream.write(buffer, 0, readBytes);
            }
            if (LOGGER.isInfoEnabled())
                LOGGER.info("退单图片上传成功! path:" + pictureFile.getAbsoluteFile().getAbsolutePath());

        }catch (Exception e) {
            if(LOGGER.isInfoEnabled())
                LOGGER.info("退单图片上传失败");
        }finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ignore) {

            }
        }
    }


    @Override
    @Transactional
    public void createBackGoodsItem(BackGoodsItem backGoodsItem) {
        backGoodsItemRepository.insert(backGoodsItem);
    }

    private void createBackGoodsLog(BackGoodsLog backGoodsLog) {
        backGoodsLogRepository.insert(backGoodsLog);
    }
}
