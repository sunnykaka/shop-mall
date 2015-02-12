package com.kariqu.tradecenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.Lottery;
import com.kariqu.tradecenter.domain.Rotary;
import com.kariqu.tradecenter.domain.RotaryMeed;

import java.util.List;
import java.util.Map;

public interface RotaryLotteryRepository {

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



    /** 新建中奖信息 */
    void insertLottery(Lottery lottery);

    /** 更新中奖信息 */
    void updateLottery(Lottery lottery);

    /** 删除中奖信息 */
    void deleteLottery(int lotteryId);

    /** 将中奖信息置为发货 */
    void sendOutLottery(int lotteryId);

    /** 查询某个轮盘里的所有中奖信息 */
    List<Lottery> queryAllLotteryByRotaryIdWithOutNil(int rotaryId);

    /**
     * 查询中奖信息
     *
     * @param map 抽奖Id(rotaryId), 是否真实(really); 是否需要发货(needSend); 是否已发货(sendOut).
     */
    Page<Lottery> queryLotteryByQuery(Map<String, Object> map);

    /** 查询单个中奖信息 */
    Lottery queryLotteryById(int lotteryId);

}