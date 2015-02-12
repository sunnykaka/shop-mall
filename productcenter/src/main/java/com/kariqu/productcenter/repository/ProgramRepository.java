package com.kariqu.productcenter.repository;

import java.util.List;

import com.kariqu.productcenter.domain.Program;

public interface ProgramRepository {

    void createProgram(Program program);

    void updateProgram(Program program);
    
    public List<Program> queryProgram(String type);
}
