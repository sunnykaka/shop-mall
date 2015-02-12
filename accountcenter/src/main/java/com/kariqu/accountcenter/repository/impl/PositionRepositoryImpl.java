package com.kariqu.accountcenter.repository.impl;

import com.kariqu.accountcenter.domain.Level;
import com.kariqu.accountcenter.domain.Position;
import com.kariqu.accountcenter.repository.PositionRepository;
import com.kariqu.common.pagenavigator.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PositionRepositoryImpl extends SqlMapClientDaoSupport implements PositionRepository {

    @Override
    public void createPosition(Position position) {
        getSqlMapClientTemplate().insert("insertPosition", position);
    }

    @Override
    public List<Position> queryAllPosition() {
        return getSqlMapClientTemplate().queryForList("selectAllPositions");
    }

    @Override
    public void updatePosition(Position position) {
        getSqlMapClientTemplate().update("updatePosition", position);
    }

    @Override
    public Position queryPositionById(int id) {
        return (Position) getSqlMapClientTemplate().queryForObject("selectPositionById", id);
    }

    @Override
    public void deletePositionById(int id) {
        getSqlMapClientTemplate().delete("deletePositionById", id);
    }

    @Override
    public Page<Position> queryPositionByPage(Page<Position> page) {
        Map<String, Integer> param = new HashMap<String, Integer>();
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<Position> queryPositionByPage = getSqlMapClientTemplate().queryForList("queryPositionByPage", param);
        page.setResult(queryPositionByPage);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("selectCountForPosition"));
        return page;
    }

    @Override
    public Level queryLevel(int employeeId) {
        String employeeLevel = (String) getSqlMapClientTemplate().queryForObject("queryemployeeLevel", employeeId);
        if(StringUtils.isNotEmpty(employeeLevel)){
            return Level.valueOf(employeeLevel);
        }else{
            return Level.FOUR;
        }
    }

}
