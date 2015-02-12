package com.kariqu.productcenter.repository.impl.ibatis;

import java.util.List;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.kariqu.productcenter.domain.Program;
import com.kariqu.productcenter.repository.ProgramRepository;

public class ProgramRepositoryImpl extends SqlMapClientDaoSupport implements ProgramRepository {

	@Override
	public void createProgram(Program program) {
		getSqlMapClientTemplate().insert("insertProgram", program);
	}

	@Override
	public void updateProgram(Program program) {
		getSqlMapClientTemplate().update("updateProgram", program);
	}

	@Override
	public List<Program> queryProgram(String type) {
		 return (List<Program>) getSqlMapClientTemplate().queryForList("selectProgram",type);
	}

}
