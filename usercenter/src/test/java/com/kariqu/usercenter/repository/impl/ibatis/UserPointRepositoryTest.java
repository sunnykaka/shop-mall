package com.kariqu.usercenter.repository.impl.ibatis;

import com.kariqu.usercenter.domain.UserPoint;
import com.kariqu.usercenter.repository.UserPointRepository;
import com.kariqu.usercenter.service.UserPointQuery;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import static junit.framework.Assert.assertEquals;

/**
 * User: Asion
 * Date: 13-3-18
 * Time: 上午10:45
 */
@SpringApplicationContext({"classpath:userCenter.xml"})
public class UserPointRepositoryTest extends UnitilsJUnit4 {

    @SpringBean("userPointRepository")
    private UserPointRepository userPointRepository;

    @Test
    public void testUserPointRepository() {
        UserPoint userPoint = new UserPoint();
        userPoint.setUserId(1);
        userPoint.setPoint(2);
        userPoint.setInOutComingType(UserPoint.InOutComingType.Order);
        userPoint.setType(UserPoint.PointType.InComing);
        userPoint.setDescription("xxxxxx");
        userPointRepository.createUsePoint(userPoint);
        assertEquals(1, userPointRepository.queryUserPoint(UserPointQuery.where(1)).getTotalCount());
        assertEquals(1, userPointRepository.queryUserPoint(UserPointQuery.where(1)).getResult().size());
        assertEquals(1, userPointRepository.queryUserPoint(UserPointQuery.where(1).and(UserPoint.PointType.InComing)).getTotalCount());
        assertEquals(1, userPointRepository.queryUserPoint(UserPointQuery.where(1).and(UserPoint.PointType.InComing)).getResult().size());

    }
}
