package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.productcenter.domain.ProductPicture;
import com.kariqu.productcenter.repository.ProductPictureRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

/**
 * User: Asion
 * Date: 11-6-28
 * Time: 下午4:31
 */
@SpringApplicationContext({"classpath:productCenter.xml"})
public class ProductPictureRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean(("productPictureRepository"))
    private ProductPictureRepository productPictureRepository;

    @Test
    public void testProductPictureRepository() {
        ProductPicture productPicture = new ProductPicture();
        productPicture.setPictureUrl("http://www.jdon.com/index.jgg");
        productPicture.setPictureLocalUrl("http://wwwww.jpg");
        productPicture.setName("大图");
        productPicture.setProductId(3);
        productPicture.setOriginalName("xxx");
        productPicture.setMainPic(false);
        productPictureRepository.createProductPicture(productPicture);
        assertEquals(1, productPictureRepository.queryAllProductPictures().size());
        assertEquals(1, productPictureRepository.queryProductPicturesByProductId(3).size());
        assertEquals(1, productPictureRepository.queryProductPictureUrlByProductId(3, 0).size());

        assertNull(productPictureRepository.getMainProductPictureByProductId(3));
        productPictureRepository.makeProductPictureAsMain(productPicture);
        assertNotNull(productPictureRepository.getMainProductPictureByProductId(3));

        productPictureRepository.deleteProductPictureByProductId(3);
        assertEquals(0, productPictureRepository.queryAllProductPictures().size());

        productPictureRepository.createProductPicture(productPicture);
        assertEquals(3, productPictureRepository.getProductPictureById(productPicture.getId()).getProductId());

        productPictureRepository.deleteProductPictureById(productPicture.getId());
        assertEquals(0, productPictureRepository.queryAllProductPictures().size());

        productPicture.setMainPic(true);
        productPictureRepository.createProductPicture(productPicture);
        productPictureRepository.createProductPicture(productPicture);
        productPictureRepository.createProductPicture(productPicture);
        productPictureRepository.createProductPicture(productPicture);
        productPictureRepository.makeProductPictureAsMain(productPicture);
        assertNotNull(productPictureRepository.getMainProductPictureByProductId(3));
    }
}
