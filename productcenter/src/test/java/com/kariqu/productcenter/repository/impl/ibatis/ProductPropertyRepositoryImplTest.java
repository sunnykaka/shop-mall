package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.PropertyType;
import com.kariqu.productcenter.domain.ProductProperty;
import com.kariqu.productcenter.repository.ProductPropertyRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import static junit.framework.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-11-8
 * Time: 上午11:36
 */
@SpringApplicationContext({"classpath:productCenter.xml"})
public class ProductPropertyRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("productPropertyRepository")
    private ProductPropertyRepository productPropertyRepository;

    @Test
    public void testProductPropertyRepository() {
        ProductProperty productProperty = new ProductProperty();
        productProperty.setJson("json");
        productProperty.setProductId(2);
        productProperty.setPropertyType(PropertyType.KEY_PROPERTY);
        productPropertyRepository.createProductProperty(productProperty);
        productProperty.setPropertyType(PropertyType.SELL_PROPERTY);
        productPropertyRepository.createProductProperty(productProperty);

        assertEquals(2, productPropertyRepository.queryAllProductProperty().size());
        assertEquals(2, productPropertyRepository.queryByProductId(2).size());
        assertEquals("json", productPropertyRepository.queryProductPropertyByPropertyType(2, PropertyType.SELL_PROPERTY).getJson());
        assertEquals("json", productPropertyRepository.queryProductPropertyByPropertyType(2, PropertyType.KEY_PROPERTY).getJson());

        productPropertyRepository.deleteProductPropertyByPropertyType(2, PropertyType.SELL_PROPERTY);
        assertEquals(1, productPropertyRepository.queryAllProductProperty().size());
        productPropertyRepository.deleteProductPropertyByPropertyType(2, PropertyType.KEY_PROPERTY);
        assertEquals(0, productPropertyRepository.queryAllProductProperty().size());

        productPropertyRepository.createProductProperty(productProperty);
        assertEquals(1, productPropertyRepository.queryAllProductProperty().size());

        productPropertyRepository.deleteProductPropertyByProductId(2);
        assertEquals(0, productPropertyRepository.queryAllProductProperty().size());
    }
}
