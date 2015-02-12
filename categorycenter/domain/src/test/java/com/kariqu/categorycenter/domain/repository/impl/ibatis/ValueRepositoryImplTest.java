package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.Value;
import com.kariqu.categorycenter.domain.repository.ValueRepository;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

import static org.junit.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-6-25
 * Time: 下午7:11
 */
public class ValueRepositoryImplTest extends IbatisBaseTest {

    @SpringBean("valueRepository")
    ValueRepository valueRepository;


    @Test
    public void testValueRepository() throws InterruptedException {
        Value value = new Value();
        value.setValueName("三星");
        valueRepository.createValue(value);
        assertEquals("三星", valueRepository.getValueById(value.getId()).getValueName());
        assertEquals(1, valueRepository.queryAllValues().size());
        value.setValueName("飞利浦");
        valueRepository.updateValue(value);
        assertEquals("飞利浦", valueRepository.getValueById(value.getId()).getValueName());
        assertEquals("飞利浦", valueRepository.getValueByName("飞利浦").getValueName());
        valueRepository.deleteAllValue();
        valueRepository.deleteValueById(value.getId());
        assertEquals(0, valueRepository.queryAllValues().size());
    }
}
