package com.kariqu.accountcenter.service.impl;

import com.kariqu.accountcenter.domain.Level;
import com.kariqu.accountcenter.domain.Position;
import com.kariqu.accountcenter.repository.PositionRepository;
import com.kariqu.accountcenter.service.PositionService;
import com.kariqu.common.pagenavigator.Page;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: Asion
 * Date: 12-3-12
 * Time: 下午3:41
 */
public class PositionServiceImpl implements PositionService {

    @Autowired
    private PositionRepository positionRepository;


    @Override
    public void createPosition(Position position) {
        positionRepository.createPosition(position);
    }

    @Override
    public List<Position> queryAllPosition() {
        return positionRepository.queryAllPosition();
    }

    @Override
    public void updatePosition(Position position) {
        positionRepository.updatePosition(position);
    }

    @Override
    public Position queryPositionById(int id) {
        return positionRepository.queryPositionById(id);
    }

    @Override
    public void deletePositionById(int id) {
        positionRepository.deletePositionById(id);
    }

    @Override
    public Page<Position> queryPositionByPage(Page<Position> page) {
        return positionRepository.queryPositionByPage(page);
    }

    @Override
    public Level queryLevel(int employeeId) {
        return positionRepository.queryLevel(employeeId);
    }
}
