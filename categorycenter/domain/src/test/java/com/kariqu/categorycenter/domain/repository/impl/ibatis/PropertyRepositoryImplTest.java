package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.Property;
import com.kariqu.categorycenter.domain.repository.PropertyRepository;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

import static org.junit.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-6-25
 * Time: 下午7:10
 */
public class PropertyRepositoryImplTest extends IbatisBaseTest {

    @SpringBean("propertyRepository")
    private PropertyRepository propertyRepository;


    @Test
    public void testPropertyRepository() throws InterruptedException {
        Property property = new Property();
        property.setName("品牌");
        propertyRepository.createProperty(property);
        assertEquals("品牌", propertyRepository.getPropertyById(property.getId()).getName());
        assertEquals(1, propertyRepository.queryAllProperties().size());
        property.setName("尺寸");
        propertyRepository.updateProperty(property);
        assertEquals("尺寸", propertyRepository.getPropertyById(property.getId()).getName());
        propertyRepository.updateProperty(property);

        assertEquals("尺寸", propertyRepository.getPropertyByName("尺寸").getName());

        assertEquals(null, propertyRepository.getPropertyByName("ddddd"));

        propertyRepository.deleteAllProperty();
        assertEquals(0, propertyRepository.queryAllProperties().size());

    }
}
