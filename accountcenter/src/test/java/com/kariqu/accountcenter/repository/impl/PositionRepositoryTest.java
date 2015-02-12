package com.kariqu.accountcenter.repository.impl;

import com.kariqu.accountcenter.domain.Level;
import com.kariqu.accountcenter.domain.Position;
import com.kariqu.accountcenter.repository.PositionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static junit.framework.Assert.assertEquals;


@ContextConfiguration(locations = {"/accountCenter.xml"})
public class PositionRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private PositionRepository positionRepository;


    @Test
    @Rollback(false)
    public void testPositionRepository() {
        Position position = new Position();
        position.setPositionName("总监");
        position.setLevel(Level.FOUR);
        positionRepository.createPosition(position);
        assertEquals("总监", positionRepository.queryPositionById(position.getId()).getPositionName());
        assertEquals(Level.FOUR, positionRepository.queryPositionById(position.getId()).getLevel());
        assertEquals(6, positionRepository.queryAllPosition().size());
    }


}
