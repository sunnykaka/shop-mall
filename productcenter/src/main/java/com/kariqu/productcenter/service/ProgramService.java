package com.kariqu.productcenter.service;

import java.util.List;

import com.kariqu.productcenter.domain.Program;

public interface ProgramService {

    void createProgram(Program program);

    void updateProgram(Program program);
    
    public List<Program> queryProgram(String type);
}
