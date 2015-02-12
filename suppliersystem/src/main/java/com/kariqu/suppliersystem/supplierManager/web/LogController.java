package com.kariqu.suppliersystem.supplierManager.web;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.common.pagenavigator.PageProcessor;
import com.kariqu.suppliercenter.domain.SupplierAccount;
import com.kariqu.suppliercenter.domain.SupplierLog;
import com.kariqu.suppliercenter.service.SupplierLogQuery;
import com.kariqu.suppliersystem.common.JsonResult;
import com.kariqu.suppliersystem.common.PageInfo;
import com.kariqu.suppliersystem.orderManager.web.BaseController;
import com.kariqu.suppliersystem.supplierManager.vo.SessionUtils;
import com.kariqu.suppliersystem.supplierManager.vo.SupplierLogVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 商家操作日志控制器
 *
 * @author:Wendy
 * @since:1.0.0 Date: 13-4-23
 * Time: 上午10:15
 */
@Controller
@RequestMapping("log")
public class LogController extends BaseController {

    private final Log logger = LogFactory.getLog(LogController.class);

    @RequestMapping()
    public void queryOrder(SupplierLogQuery query, PageInfo pageInfo,  HttpServletResponse response, HttpServletRequest request) throws IOException {
        if (query.getLimit() == 0) {
            query.setLimit(15);
        }
        query = page(query, pageInfo);
        SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
        query.setSupplierId(supplierAccount.getCustomerId());
        Page<SupplierLog> supplierLogPage = supplierLogService.querySupplierLogPageBySupplierId(query, new Page<SupplierLog>(query.getStart(), query.getLimit()));
        new JsonResult(JsonResult.SUCCESS).addData(JsonResult.RESULT_TYPE_LIST, generateSupplierLogVo(supplierLogPage.getResult())).addData(JsonResult.RESULT_TYPE_SINGLE_OBJECT,supplierLogPage.getTotalCount()).toJson(response);
    }


    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public void deleteLogisticsPrintInfoById(@RequestParam("ids") long[] ids, HttpServletResponse response) throws IOException {
        try {
            for (long id : ids) {
                supplierLogService.deleteSupplierLogById(id);
            }
        } catch (Exception e) {
            logger.error("删除物流公司信息出错",e);
            new JsonResult(JsonResult.FAILURE, "删除物流公司失败").toJson(response);
            return;
        }
        new JsonResult(JsonResult.SUCCESS).toJson(response);
    }


    private List<SupplierLogVo> generateSupplierLogVo(List<SupplierLog> supplierLogList) {
        List<SupplierLogVo> supplierLogVoList = new ArrayList<SupplierLogVo>();
        for (SupplierLog supplierLog : supplierLogList) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SupplierLogVo supplierLogVo = new SupplierLogVo();
            supplierLogVo.setContent(supplierLog.getContent());
            supplierLogVo.setDate(format.format(supplierLog.getDate()));
            supplierLogVo.setId(supplierLog.getId());
            supplierLogVo.setIp(supplierLog.getIp());
            supplierLogVo.setOperator(supplierLog.getOperator());
            supplierLogVo.setTitle(supplierLog.getTitle());
            supplierLogVoList.add(supplierLogVo);
        }
        return supplierLogVoList;
    }

    private SupplierLogQuery page(SupplierLogQuery query, PageInfo pageInfo) {
        if (query.getLimit() == 0) {
            query.setLimit(15);
        }
        query.setStart(query.getStart() / query.getLimit() + 1);
        int pageNo = pageInfo.getPageNo();
        if (pageInfo.getPageNo() > 0) {
            query.setStart(pageInfo.getPageNo());
        }
        int totalPage = pageInfo.getTotalPage();
        if ("nextPage".equals(pageInfo.getForwardType())) {
            query.setStart((pageNo + 1) > totalPage ? totalPage : pageNo + 1);
        } else if ("prePage".equals(pageInfo.getForwardType())) {
            query.setStart((pageNo - 1) > 1 ? pageNo - 1 : 1);
        } else if ("firstPage".equals(pageInfo.getForwardType())) {
            query.setStart(1);
        } else if ("endPage".equals(pageInfo.getForwardType())) {
            query.setStart(totalPage);
        }
        return query;
    }

}
