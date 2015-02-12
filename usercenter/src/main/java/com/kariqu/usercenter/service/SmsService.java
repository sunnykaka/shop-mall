package com.kariqu.usercenter.service;

import com.kariqu.usercenter.domain.SmsCharacter;
import com.kariqu.usercenter.domain.SmsMould;

import java.util.List;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-8-8
 * Time: 上午11:21
 */
public interface SmsService {

    void createSmsMould(SmsMould smsMould);

    void updateSmsMould(SmsMould smsMould);

    public SmsMould getSmsMouldById(int id);

    public List<SmsMould> querySmsMouIdList();

    public void deleteSmsMouldById(int id);


    public void createSmsCharacter(SmsCharacter smsCharacter);

    public void updateSmsCharacter(SmsCharacter smsCharacter);

    public SmsCharacter getSmsCharacterById(int id);

    public List<SmsCharacter> querySmsCharacterList();

    public void deleteSmsCharacterById(int id);
}
