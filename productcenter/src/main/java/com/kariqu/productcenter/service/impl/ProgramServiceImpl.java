package com.kariqu.productcenter.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.kariqu.productcenter.domain.Program;
import com.kariqu.productcenter.repository.ProgramRepository;
import com.kariqu.productcenter.service.ProgramService;

public class ProgramServiceImpl implements ProgramService {

	@Autowired
	private ProgramRepository programRepository;
	
	@Override
	public void createProgram(Program program) {
		programRepository.createProgram(program);
	}

	@Override
	public void updateProgram(Program program) {
		programRepository.updateProgram(program);
	}

	@Override
	public List<Program> queryProgram(String type) {
		return programRepository.queryProgram(type);
	}

}
