package com.kariqu.tradecenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.Lottery;
import com.kariqu.tradecenter.domain.Rotary;
import com.kariqu.tradecenter.domain.RotaryMeed;
import com.kariqu.usercenter.domain.User;

import java.util.Date;
import java.util.List;

public interface RotaryLotteryService {

    /** 新建轮盘 */
    void insertRotary(Rotary rotary);

    /** 更新轮盘 */
    void updateRotary(Rotary rotary);

    /** 删除轮盘 */
    void deleteRotary(int rotaryId);

    /** 查询所有的轮盘 */
    List<Rotary> queryAllRotary();

    /** 查询单个轮盘 */
    Rotary queryRotaryById(int rotaryId);



    /** 新建轮盘里的奖品项 */
    void insertMeed(RotaryMeed meed);

    /** 更新轮盘里的奖品项 */
    void updateMeed(RotaryMeed meed);

    /** 删除轮盘里的奖品项 */
    void deleteMeed(int meedId);

    /** 查询某个轮盘里的所有奖品项, 按索引排序 */
    List<RotaryMeed> queryAllMeedByRotaryIdOrderByIndex(int rotaryId);

    /** 查询某个轮盘里的所有奖品项, 按概率排序 */
    List<RotaryMeed> queryAllMeedByRotaryIdOrderByProbability(int rotaryId);

    /** 查询单个奖品项 */
    RotaryMeed queryMeedById(int meedId);

    /**
     * 新建中奖信息
     *
     * @param rotary 抽奖活动Id
     * @param user 用户名
     * @param meed 奖品项
     */
    Lottery insertLottery(Rotary rotary, User user, RotaryMeed meed);

    /**
     * 更新需要发货的中奖数据
     *
     * @param userName 用户名
     * @param lotteryId 中奖Id
     * @param consigneeName 收货人名字
     * @param consigneePhone 收货人电话
     * @param consigneeAddress 收货人地址
     */
    void updateSendLottery(String userName, int lotteryId, String consigneeName, String consigneePhone, String consigneeAddress);

    /**
     * 更新中奖信息
     *
     * @param lottery 中奖数据
     */
    void updateLottery(Lottery lottery);

    /**
     * 新建中奖信息
     *
     * @param rotaryId 抽奖活动Id
     * @param userName 用户名
     * @param meedId 抽奖项Id
     * @param createDate 创建时间
     */
    void insertMenLottery(int rotaryId, String userName, int meedId, Date createDate);

    /** 删除中奖信息 */
    void deleteLottery(int lotteryId);

    /** 将中奖信息置为已发货 */
    void sendOutLottery(int lotteryId);

    /**
     * 查询单个抽奖活动所有的中奖数据
     *
     * @param rotaryId 抽奖活动Id
     */
    List<Lottery> queryAllLotteryByRotaryIdWithOutNil(Integer rotaryId);

    /**
     * 查询中奖信息
     *
     * @param rotaryId 抽奖Id
     * @param userName 用户名
     * @param needSend 是否需要发货
     * @param sendOut 是否已发货
     * @param page
     */
    Page<Lottery> queryLotteryByQuery(Integer rotaryId, String userName, Boolean needSend, Boolean sendOut, Page<Lottery> page);

    /** 查询单个中奖信息 */
    Lottery queryLotteryById(int lotteryId);

}