package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Consultation;
import com.kariqu.productcenter.domain.ConsultationCategory;
import com.kariqu.productcenter.repository.ConsultationRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * @author: enoch
 * @since 1.0.0
 *        Date: 12-8-28
 *        Time: 下午4:18
 */
@SpringApplicationContext({"classpath:productCenter.xml"})
public class ConsultationRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("consultationRepository")
    private ConsultationRepository consultationRepository;

    @Test
    public void testConsultation(){

        Consultation consultation = new Consultation();
        consultation.setAskContent("这件商品到底怎么样？");
        consultation.setAskedUserName("老歪");
        consultation.setConsultationCategory(ConsultationCategory.pay);
        consultation.setProductId(1);
        consultation.setAskUserId(1);
        consultationRepository.createConsultation(consultation);
        assertEquals(new Integer(0),consultationRepository.queryConsultationById(consultation.getId()).getHasAnswer());

        consultation.setAnswerContent("当然很好");
        consultation.setAnswerTime(new Date());
        consultationRepository.answerConsultation(consultation.getId(),consultation.getAnswerContent(),new Date());
        assertEquals(new Integer(1) ,consultationRepository.queryConsultationById(consultation.getId()).getHasAnswer());
        assertEquals("当然很好",consultationRepository.queryConsultationById(consultation.getId()).getAnswerContent());

        assertEquals(1, consultationRepository.searchConsultation(new Page<Consultation>(), null, null,null).getTotalCount());
        assertEquals(1, consultationRepository.searchConsultation(new Page<Consultation>(), ConsultationCategory.pay, null,"1").getTotalCount());
        assertEquals(1, consultationRepository.queryConsultation(new Page<Consultation>(), null, null, null).getTotalCount());
        assertEquals(1, consultationRepository.queryConsultation(new Page<Consultation>(), null, null, 1).getTotalCount());
        assertEquals(1, consultationRepository.queryConsultation(new Page<Consultation>(), null, 1, 1).getTotalCount());
        assertEquals(1, consultationRepository.queryConsultation(new Page<Consultation>(), ConsultationCategory.pay, 1, 1).getTotalCount());
        assertEquals(1, consultationRepository.searchConsultation(new Page<Consultation>(), null, 1,null).getTotalCount());
        assertEquals(1, consultationRepository.searchConsultation(new Page<Consultation>(), null, 1,"1").getTotalCount());
        assertEquals(1, consultationRepository.searchConsultation(new Page<Consultation>(), ConsultationCategory.pay, 1,"1").getTotalCount());

    }
}
