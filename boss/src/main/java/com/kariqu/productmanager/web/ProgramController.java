package com.kariqu.productmanager.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kariqu.common.JsonResult;
import com.kariqu.productcenter.domain.Program;
import com.kariqu.productcenter.service.ProgramService;
import com.kariqu.productmanager.helper.ProgramVo;

@Controller
public class ProgramController {
    private final Log logger = LogFactory.getLog(ProgramController.class);

    @Autowired
    private ProgramService programService;

    @RequestMapping("program/add")
    public void createProgram(Program program,HttpServletResponse response) throws IOException {
    	  try {
          		programService.createProgram(program);
          		new JsonResult(true).toJson(response);
          }catch (Exception e){
              logger.error(e);
              new JsonResult(false, e.getMessage()).toJson(response);
          }
    }

    @RequestMapping(value = "program/update")
    public void updateProgram(ProgramVo programVo, HttpServletResponse response) throws IOException {
        try {
        	String[] ids = programVo.getIds();
        	String[] imgurl = programVo.getImgUrl();
        	String[] pro = programVo.getProductId();
        	for(int i = 0; i < ids.length; i++){
        		Program p = new Program();
        		p.setImgUrl(imgurl[i]);
        		p.setProductId(pro[i]);
        		p.setType(programVo.getType());
        		
        		if("newData".equals(ids[i])){
        			p.setTitle("热销产品");
        			programService.createProgram(p);
        		} else {
        			p.setId(new Integer(ids[i]));
            		programService.updateProgram(p);
        		}
        	}
            new JsonResult(true).toJson(response);
        }catch (Exception e){
            logger.error(e);
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "program/query")
    public void queryProgram(String type, HttpServletResponse response) throws IOException {
        List<Program> programs = programService.queryProgram(type);
        int size = programs.size();
        String[] urls = new String[size];
        String[] pros = new String[size];
        String[] ids =  new String[size];
        ProgramVo vo = new ProgramVo();
        for(int i = 0; i < size; i++) {
        	Program program = programs.get(i);
        	urls[i] = program.getImgUrl();
        	pros[i] = program.getProductId();
        	ids[i] = program.getId()+"";
        	vo.setImgUrl(urls);
        	vo.setProductId(pros);
        	vo.setIds(ids);
        }
        new JsonResult(true).addData("object", vo).toJson(response);
    }
}
