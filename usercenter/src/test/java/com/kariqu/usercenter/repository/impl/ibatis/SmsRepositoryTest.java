package com.kariqu.usercenter.repository.impl.ibatis;

import com.kariqu.usercenter.domain.SmsCharacter;
import com.kariqu.usercenter.domain.SmsMould;
import com.kariqu.usercenter.repository.SmsRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import static junit.framework.Assert.assertEquals;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-8-8
 * Time: 上午11:05
 */
@SpringApplicationContext({"classpath:userCenter.xml"})
public class SmsRepositoryTest extends UnitilsJUnit4 {

    @SpringBean("smsRepository")
    private SmsRepository smsRepository;

    @Test
    public void testSmsMould(){
        SmsMould smsMould=new SmsMould();
        smsMould.setContent("aaaaa");
        smsMould.setDescription("dfasdgasg");
        smsRepository.createSmsMould(smsMould);
        assertEquals(1, smsRepository.querySmsMouIdList().size());
        SmsMould smsMouldVo=smsRepository.getSmsMouldById(smsMould.getId());
        smsMouldVo.setContent("a");
        smsRepository.updateSmsMould(smsMouldVo);
        assertEquals("a", smsRepository.getSmsMouldById(smsMould.getId()).getContent()) ;
        smsRepository.deleteSmsMouldById(smsMould.getId());
        assertEquals(0, smsRepository.querySmsMouIdList().size());
    }

    @Test
    public void testSmsCharacter(){
        SmsCharacter smsCharacter=new SmsCharacter();
        smsCharacter.setName("姓名");
        smsCharacter.setValue("name");
        smsRepository.createSmsCharacter(smsCharacter);
        assertEquals(3, smsRepository.querySmsCharacterList().size()) ;
        SmsCharacter SmsCharacterVo=smsRepository.getSmsCharacterById(smsCharacter.getId());
        SmsCharacterVo.setValue("sex");
        smsRepository.updateSmsCharacter(SmsCharacterVo);
        assertEquals("sex", smsRepository.getSmsCharacterById(smsCharacter.getId()).getValue()) ;
        smsRepository.deleteSmsCharacterById(smsCharacter.getId());
        assertEquals(2, smsRepository.querySmsCharacterList().size()) ;
    }
}
