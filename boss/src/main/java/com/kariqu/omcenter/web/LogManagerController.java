package com.kariqu.omcenter.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.om.domain.SystemLog;
import com.kariqu.om.service.OMService;
import com.kariqu.omcenter.helper.SystemLogVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-9-4
 * Time: 下午4:07
 */
@Controller
public class LogManagerController {

    private final Log logger = LogFactory.getLog(LogManagerController.class);

    @Autowired
    private OMService omService;

    /**
     * 查找日志
     *
     * @param systemLog
     * @param start
     * @param limit
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/log/search")
    public void searchSystemLog(SystemLog systemLog, int start, int limit, HttpServletResponse response) throws IOException {
        Page<SystemLog> page = omService.querySystemLogByPage(start / limit + 1, limit, systemLog.getTitle(), systemLog.getIp());
        List<SystemLogVo> list = new ArrayList<SystemLogVo>();
        for (SystemLog log : page.getResult()) {
            SystemLogVo systemLogVo = new SystemLogVo();
            systemLogVo.setContent(log.getContent());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            systemLogVo.setDate((format.format(log.getDate())).toString());
            systemLogVo.setId(log.getId());
            systemLogVo.setIp(log.getIp());
            systemLogVo.setOperator(log.getOperator());
            systemLogVo.setTitle(log.getTitle());
            systemLogVo.setRoleName(log.getRoleName());
            list.add(systemLogVo);
        }
        new JsonResult(true).addData("totalCount", page.getTotalCount()).addData("result", list).toJson(response);
    }

    /**
     * 删除日志信息
     *
     * @param ids
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/log/delete", method = RequestMethod.POST)
    @Permission("删除系统日志")
    public void deleteSystemLog(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids) {
                omService.deleteSystemLog(id);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("删除日志信息错误：" + e);
            new JsonResult(false, "删除日志失败").toJson(response);
        }
    }
}
