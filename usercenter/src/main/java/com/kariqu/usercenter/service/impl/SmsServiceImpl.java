package com.kariqu.usercenter.service.impl;

import com.kariqu.usercenter.domain.SmsCharacter;
import com.kariqu.usercenter.domain.SmsMould;
import com.kariqu.usercenter.repository.SmsRepository;
import com.kariqu.usercenter.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-8-8
 * Time: 上午11:24
 */
public class SmsServiceImpl implements SmsService {

    @Autowired
    private SmsRepository smsRepository;


    @Override
    public void createSmsMould(SmsMould smsMould) {
        smsRepository.createSmsMould(smsMould);
    }

    @Override
    public void updateSmsMould(SmsMould smsMould) {
        smsRepository.updateSmsMould(smsMould);
    }

    @Override
    public SmsMould getSmsMouldById(int id) {
        return smsRepository.getSmsMouldById(id);
    }

    @Override
    public List<SmsMould> querySmsMouIdList() {
        return smsRepository.querySmsMouIdList();
    }

    @Override
    public void deleteSmsMouldById(int id) {
       smsRepository.deleteSmsMouldById(id);
    }

    @Override
    public void createSmsCharacter(SmsCharacter smsCharacter) {
       smsRepository.createSmsCharacter(smsCharacter);
    }

    @Override
    public void updateSmsCharacter(SmsCharacter smsCharacter) {
        smsRepository.updateSmsCharacter(smsCharacter);
    }

    @Override
    public SmsCharacter getSmsCharacterById(int id) {
        return smsRepository.getSmsCharacterById(id);
    }

    @Override
    public List<SmsCharacter> querySmsCharacterList() {
        return smsRepository.querySmsCharacterList();
    }

    @Override
    public void deleteSmsCharacterById(int id) {
        smsRepository.deleteSmsCharacterById(id);
    }
}
