package com.kariqu.accountcenter.repository;

import com.kariqu.accountcenter.domain.Level;
import com.kariqu.accountcenter.domain.Position;
import com.kariqu.common.pagenavigator.Page;

import java.util.List;

public interface PositionRepository {

    void createPosition(Position position);

    void updatePosition(Position position);

    Position queryPositionById(int id);

    List<Position> queryAllPosition();

    void deletePositionById(int id);

    Page<Position> queryPositionByPage(Page<Position> page);

    Level queryLevel(int employeeId);
}
