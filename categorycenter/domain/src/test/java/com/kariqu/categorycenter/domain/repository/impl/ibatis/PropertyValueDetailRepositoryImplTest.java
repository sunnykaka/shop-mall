package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.PropertyValueDetail;
import com.kariqu.categorycenter.domain.repository.PropertyValueDetailRepository;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

import static org.junit.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-6-25
 * Time: 下午7:10
 */
public class PropertyValueDetailRepositoryImplTest extends IbatisBaseTest {

    @SpringBean("categoryPropertyValueDetailRepository")
    private PropertyValueDetailRepository categoryPropertyValueDetailRepository;

    @Test
    public void testPropertyValueDetailRepository() throws InterruptedException {
        PropertyValueDetail propertyValueDetail = new PropertyValueDetail();
        propertyValueDetail.setPropertyId(1);
        propertyValueDetail.setValueId(2);
        propertyValueDetail.setDescription("描述");
        propertyValueDetail.setPictureUrl("http://sss.jgp");
        categoryPropertyValueDetailRepository.createPropertyValueDetail(propertyValueDetail);
        assertEquals(1, categoryPropertyValueDetailRepository.queryAllPropertyValueDetails().size());

        propertyValueDetail.setPropertyId(33);
        propertyValueDetail.setValueId(44);
        propertyValueDetail.setDescription("dddddd");
        propertyValueDetail.setPictureUrl("ggggggggggggg");
        categoryPropertyValueDetailRepository.updatePropertyValueDetail(propertyValueDetail);

        assertEquals(1, categoryPropertyValueDetailRepository.queryAllPropertyValueDetails().size());

        assertEquals(33, categoryPropertyValueDetailRepository.getPropertyValueDetail(33, 44).getPropertyId());

        categoryPropertyValueDetailRepository.deletePropertyValueDetailById(propertyValueDetail.getId());
        categoryPropertyValueDetailRepository.deleteAllPropertyValueDetail();
        assertEquals(0, categoryPropertyValueDetailRepository.queryAllPropertyValueDetails().size());

    }

}
