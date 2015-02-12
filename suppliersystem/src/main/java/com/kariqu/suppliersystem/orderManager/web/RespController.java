package com.kariqu.suppliersystem.orderManager.web;

import com.kariqu.suppliercenter.domain.ProductStorage;
import com.kariqu.suppliercenter.domain.SupplierAccount;
import com.kariqu.suppliersystem.common.JsonResult;
import com.kariqu.suppliersystem.supplierManager.vo.SessionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 仓库Controller
 * User: amos.zhou
 * Date: 13-9-27
 * Time: 上午11:10
 */
@Controller
@RequestMapping("/resp")
public class RespController extends BaseController {

    private final Log logger = LogFactory.getLog(RespController.class);

    /**
     * 查询当前登陆商家的所有仓库
     */
    @RequestMapping(method = RequestMethod.GET)
    public void respListForEach(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
        if (logger.isInfoEnabled()) {
            logger.info("当前商家ID：" + supplierAccount.getCustomerId() + ",商家名为：" + supplierAccount.getAccountName());
        }

        List<ProductStorage> productStorages = new ArrayList<ProductStorage>();

        //在前台多出一个option，所有仓库。方便前台Extjs获取
        ProductStorage allStorage = new ProductStorage();
        allStorage.setId(0);
        allStorage.setName("--所有仓库--");
        productStorages.add(allStorage);
        productStorages.addAll(supplierService.queryProductStorageByCustomerId(supplierAccount.getCustomerId()));
        new JsonResult(JsonResult.SUCCESS).addData(JsonResult.RESULT_TYPE_LIST, productStorages).toJson(response);
    }
}
