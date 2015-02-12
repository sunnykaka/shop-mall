package com.kariqu.tradecenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.BackGoods;
import com.kariqu.tradecenter.domain.BackGoodsState;

import java.util.List;

public interface BackGoodsRepository {

    /**
     * 创建退货单.
     *
     * @param backGoods
     */
    void insert(BackGoods backGoods);

    /**
     * 逻辑删除退货单
     *
     * @param backGoodsId
     * @return
     */
    int delete(Long backGoodsId);

    /**
     * 主键查询单条未删除的退货数据.
     *
     * @param backGoodsId
     * @return
     */
    BackGoods select(Long backGoodsId);

    /**
     * 查询指定用户的指定退货单.
     *
     * @param userId
     * @param backGoodsId
     * @return
     */
    BackGoods select(Integer userId, Long backGoodsId);

    /**
     * 查询用户下的所有未删除的退货数据.
     *
     * @param userId 用户编号
     * @return
     */
    List<BackGoods> selectByUserId(Integer userId);

    /**
     * 查询订单Id对应的所有未删除且未取消且已审核通过的退货单数据.
     *
     * @param orderId
     * @return
     */
    List<BackGoods> selectByOrderId(long orderId);

    /**
     * 查询订单编号对应的所有未删除且未取消的退货单数据.
     *
     * @param orderNo
     * @param userId
     * @return
     */
    List<BackGoods> selectByOrderNo(long orderNo, int userId);

    /**
     * 查询订单编号对应的所有未删除且未取消的退货单Id.
     *
     * @param orderNo
     * @param userId
     * @return
     */
    List<Long> selectIdByOrderNo(long orderNo, int userId);

    /**
     * 查询用户下所有未删除的分页退货数据
     *
     * @param userId
     * @param page
     * @return
     */
    Page<BackGoods> queryBackGoodsByUserIdPage(Integer userId, Page<BackGoods> page);

    /**
     * 查询某个状态下的所有未删除的退货数据.
     *
     * @param state 状态
     * @return
     */
    List<BackGoods> selectByState(BackGoodsState state);

    /**
     * 动态更新退货单数据.
     *
     * @param backGoods
     * @return 返回受影响的行数
     */
    int update(BackGoods backGoods);

    /**
     * 查询指定状态未删除的退货单数量.
     *
     * @param state
     * @return
     */
    int selectCountByState(BackGoodsState state);

    /**
     * 查询所有需要财务打款的退货单数量.
     *
     * @return
     */
    int selectCountByFinance();

    /**
     * 查询所有需要财务打款的退货单数据.
     *
     * @return
     */
    List<BackGoods> selectForFinance();

}