package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.tradecenter.repository.LinkageRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

/**
 * User: Asion
 * Date: 11-10-13
 * Time: 上午10:41
 */
@SpringApplicationContext({"classpath:tradeContext.xml"})
public class LinkageRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("linkageRepository")
    private LinkageRepository linkageRepository;


    @Test
    public void testLinkageRepository() {
        /*List<Area> areaList = linkageRepository.getAreaByCityCode();

        for(Area area: areaList){
            Area area2 = new Area();
            area2=linkageRepository.getAreaCountry(area);
            if(null != area2){
                area.setZipCode(area2.getZipCode());
                area.setAreaCode(area2.getAreaCode());
                linkageRepository.getUpdateArea(area);
            }
        }*/
    }
}
