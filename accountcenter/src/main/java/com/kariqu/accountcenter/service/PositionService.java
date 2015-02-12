package com.kariqu.accountcenter.service;


import com.kariqu.accountcenter.domain.Level;
import com.kariqu.accountcenter.domain.Position;
import com.kariqu.common.pagenavigator.Page;

import java.util.List;

/**
 * User: Asion
 * Date: 12-3-12
 * Time: 下午3:39
 */
public interface PositionService {

    void createPosition(Position position);

    void updatePosition(Position position);

    Position queryPositionById(int id);

    List<Position> queryAllPosition();

    void deletePositionById(int id);

    Page<Position> queryPositionByPage(Page<Position> page);

    Level queryLevel(int employeeId);

}
