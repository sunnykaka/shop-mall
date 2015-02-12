package com.kariqu.usercenter.repository;

import com.kariqu.usercenter.domain.SmsCharacter;
import com.kariqu.usercenter.domain.SmsMould;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.List;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-8-8
 * Time: 上午10:16
 */
public class SmsRepository extends SqlMapClientDaoSupport {

    public void createSmsMould(SmsMould smsMould) {
        getSqlMapClientTemplate().insert("insertSmsMould", smsMould);
    }

    public void updateSmsMould(SmsMould smsMould) {
        getSqlMapClientTemplate().update("updateSmsMould",smsMould);
    }

    public SmsMould getSmsMouldById(int id) {
        return (SmsMould) getSqlMapClientTemplate().queryForObject("selectSmsMouldById", id);
    }

    public List<SmsMould> querySmsMouIdList() {
        return getSqlMapClientTemplate().queryForList("selectAllSmsMould");
    }

    public void deleteSmsMouldById(int id) {
        getSqlMapClientTemplate().delete("deleteSmsMould", id);
    }


    public void createSmsCharacter(SmsCharacter smsCharacter) {
        getSqlMapClientTemplate().insert("insertSmsCharacter", smsCharacter);
    }

    public void updateSmsCharacter(SmsCharacter smsCharacter) {
       getSqlMapClientTemplate().update("updateSmsCharacter",smsCharacter);
    }

    public SmsCharacter getSmsCharacterById(int id) {
        return (SmsCharacter) getSqlMapClientTemplate().queryForObject("selectSmsCharacterById", id);
    }

    public List<SmsCharacter> querySmsCharacterList() {
        return getSqlMapClientTemplate().queryForList("selectAllSmsCharacter");
    }

    public void deleteSmsCharacterById(int id) {
        getSqlMapClientTemplate().delete("deleteSmsCharacter", id);
    }
}
