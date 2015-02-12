package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.productcenter.domain.SpacePicture;
import com.kariqu.productcenter.repository.SpacePictureRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

/**
 * Created with IntelliJ IDEA.
 * User: ennoch
 * Date: 12-7-13
 * Time: 下午2:02
 * T
 */
@SpringApplicationContext({"classpath:spacePictureCenter.xml"})
public class SpacePictureRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("spacePictureRepository")
    private SpacePictureRepository spacePictureRepository ;

    @Test
    public void testSpacePictureRepository(){

        SpacePicture picture = new SpacePicture();
        picture.setPictureUrl("wegwegwegwegwegwegwegwe.jpg");
        picture.setPictureLocalUrl("wegwegwegwegwegwegwegwe.jpg");
        picture.setOriginalName("testname");
        picture.setSpaceId(1);
        picture.setPictureName("haha");
        spacePictureRepository.createSpacePicture(picture);

    }
}
