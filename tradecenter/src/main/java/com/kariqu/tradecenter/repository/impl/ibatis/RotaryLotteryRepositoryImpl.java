package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.Lottery;
import com.kariqu.tradecenter.domain.Rotary;
import com.kariqu.tradecenter.domain.RotaryMeed;
import com.kariqu.tradecenter.repository.RotaryLotteryRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.List;
import java.util.Map;

/**
 * @author Athens(刘杰)
 */
public class RotaryLotteryRepositoryImpl extends SqlMapClientDaoSupport implements RotaryLotteryRepository {
    
    @Override
    public void insertRotary(Rotary rotary) {
        getSqlMapClientTemplate().insert("insertRotary", rotary);
    }

    @Override
    public void updateRotary(Rotary rotary) {
        getSqlMapClientTemplate().update("updateRotary", rotary);
    }

    @Override
    public void deleteRotary(int rotaryId) {
        getSqlMapClientTemplate().delete("deleteRotary", rotaryId);
    }

    @Override
    public List<Rotary> queryAllRotary() {
        return getSqlMapClientTemplate().queryForList("selectAllRotary");
    }

    @Override
    public Rotary queryRotaryById(int rotaryId) {
        return (Rotary) getSqlMapClientTemplate().queryForObject("selectRotaryById", rotaryId);
    }



    @Override
    public void insertMeed(RotaryMeed meed) {
        getSqlMapClientTemplate().insert("insertMeed", meed);
    }

    @Override
    public void updateMeed(RotaryMeed meed) {
        getSqlMapClientTemplate().update("updateMeed", meed);
    }

    @Override
    public void deleteMeed(int meedId) {
        getSqlMapClientTemplate().delete("deleteMeed", meedId);
    }

    @Override
    public List<RotaryMeed> queryAllMeedByRotaryIdOrderByIndex(int rotaryId) {
        return getSqlMapClientTemplate().queryForList("selectAllMeedByRotaryIdOrderByIndex", rotaryId);
    }

    @Override
    public List<RotaryMeed> queryAllMeedByRotaryIdOrderByProbability(int rotaryId) {
        return getSqlMapClientTemplate().queryForList("selectAllMeedByRotaryIdOrderByProbability", rotaryId);
    }

    @Override
    public RotaryMeed queryMeedById(int meedId) {
        return (RotaryMeed) getSqlMapClientTemplate().queryForObject("selectMeedById", meedId);
    }



    @Override
    public void insertLottery(Lottery lottery) {
        getSqlMapClientTemplate().insert("insertLottery", lottery);
    }

    @Override
    public void updateLottery(Lottery lottery) {
        getSqlMapClientTemplate().update("updateLottery", lottery);
    }

    @Override
    public void deleteLottery(int lotteryId) {
        getSqlMapClientTemplate().delete("deleteLottery", lotteryId);
    }

    @Override
    public void sendOutLottery(int lotteryId) {
        getSqlMapClientTemplate().delete("updateLotteryToSendOut", lotteryId);
    }

    @Override
    public List<Lottery> queryAllLotteryByRotaryIdWithOutNil(int rotaryId) {
        return getSqlMapClientTemplate().queryForList("selectAllLotteryByRotaryIdWithOutNil", rotaryId);
    }

    @Override
    public Page<Lottery> queryLotteryByQuery(Map<String, Object> map) {
        Page<Lottery> page = new Page<Lottery>();
        page.setResult(getSqlMapClientTemplate().queryForList("selectLotteryByQuery", map));
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("selectCountLotteryByQuery", map));
        return page;
    }

    @Override
    public Lottery queryLotteryById(int lotteryId) {
        return (Lottery) getSqlMapClientTemplate().queryForObject("selectLotteryById", lotteryId);
    }

}
