package com.kariqu.tradesystem.web;

import com.kariqu.common.JsonResult;
import com.kariqu.suppliercenter.domain.Brand;
import com.kariqu.suppliercenter.domain.Supplier;
import com.kariqu.suppliercenter.domain.ProductStorage;
import com.kariqu.suppliercenter.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * User: wendy
 * Date: 12-6-28
 * Time: 下午5:05
 */

@Controller
public class SupplierInfoController {

    @Autowired
    private SupplierService supplierService;

    /**
     * 获取所有商家
     *
     * @return
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/order/list/customer")
    public void customerList(HttpServletResponse response) throws IOException {
        List<Supplier> customers = supplierService.queryAllCustomer();
        new JsonResult(true).addData("customersList", customers).toJson(response);
    }

    /**
     * 获取所有仓库
     *
     * @return
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/order/list/storage")
    public void storageList(@RequestParam("customerId") int customerId, HttpServletResponse response) throws IOException {
        List<ProductStorage> storage = supplierService.queryProductStorageByCustomerId(customerId);
        new JsonResult(true).addData("storageList", storage).toJson(response);
    }

    /**
     * 根据商家Id获取品牌
     *
     * @param customerId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/list/brand")
    public void brandList(@RequestParam("customerId") int customerId, HttpServletResponse response) throws IOException {
        List<Brand> brands = supplierService.queryBrandByCustomerId(customerId);
        new JsonResult(true).addData("brandList", brands).toJson(response);
    }
}
