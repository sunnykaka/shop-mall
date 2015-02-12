package com.kariqu.productmanager.web;

import com.kariqu.categorycenter.domain.model.Property;
import com.kariqu.categorycenter.domain.service.CategoryPropertyService;
import com.kariqu.productmanager.helper.CustomerTreeJson;
import com.kariqu.common.JsonResult;
import com.kariqu.suppliercenter.domain.Brand;
import com.kariqu.suppliercenter.domain.Supplier;
import com.kariqu.suppliercenter.domain.ProductStorage;
import com.kariqu.suppliercenter.service.SupplierService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * 商品发布时的商家和品牌控制器
 * User: Asion
 * Date: 12-6-21
 * Time: 上午10:50
 */
@Controller
public class ProductCustomerController {

    private final Log logger = LogFactory.getLog(ProductCustomerController.class);

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private CategoryPropertyService categoryPropertyService;

    /**
     * 商家列表
     *
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/customer/list")
    public void customerList(HttpServletResponse response) throws IOException {
        List<Supplier> customers = supplierService.queryAllCustomer();
        new JsonResult(true).addData("customers", customers).addData("totalCount", customers.size()).toJson(response);
    }

    /**
     * 某个商家的品牌列表
     *
     * @param response
     * @param customerId
     * @throws IOException
     */
    @RequestMapping(value = "/product/customer/brand/list")
    public void brandList(HttpServletResponse response, @RequestParam("customerId") int customerId) throws IOException {
        List<Brand> brands = supplierService.queryBrandByCustomerId(customerId);
        new JsonResult(true).addData("brands", brands).toJson(response);
    }


    /**
     * 查询某个商家的所有库位
     *
     * @param response
     * @param customerId
     * @throws IOException
     */
    @RequestMapping(value = "/product/customer/store/list/{customerId}")
    public void productStoreList(HttpServletResponse response, @PathVariable("customerId") int customerId) throws IOException {
        List<ProductStorage> productStores = supplierService.queryProductStorageByCustomerId(customerId);
        new JsonResult(true).addData("stores", productStores).toJson(response);
    }

    /**
     * 查询某个商家的库位数量
     *
     * @param response
     * @param customerId
     * @throws IOException
     */
    @RequestMapping(value = "/product/customer/store/number/{customerId}")
    public void productStoreNumber(HttpServletResponse response, @PathVariable("customerId") int customerId) throws IOException {
        int productStoreNumber = supplierService.queryProductStorageNumber(customerId);
        new JsonResult(true).addData("storeNumber", productStoreNumber).toJson(response);
    }

    /**
     * 查询品牌这个属性的属性ID
     *
     * @param response
     */
    @RequestMapping(value = "/product/brand/property/id")
    public void brandPid(HttpServletResponse response) throws IOException {
        try {
            Property property = categoryPropertyService.getPropertyByName("品牌");
            new JsonResult(true).addData("brandPid", property.getId()).toJson(response);
        } catch (Exception e) {
            logger.error("商品管理的查询品牌属性Id异常：" + e);
            new JsonResult(false, "查询品牌出错").toJson(response);
        }

    }

    /**
     * 商品品牌树
     *
     * @return
     */
    @RequestMapping(value = "/product/supplier/brand/tree")
    public
    @ResponseBody
    List<CustomerTreeJson> getSupplierBrandTree() {
        List<Supplier> customers = supplierService.queryAllCustomer();
        List<CustomerTreeJson> nodeList = new LinkedList<CustomerTreeJson>();
        for (Supplier customer : customers) {
            CustomerTreeJson customerTreeJson = new CustomerTreeJson();
            List<Brand> brands = supplierService.queryBrandByCustomerId(customer.getId());
            if (brands.size() > 0) {
                customerTreeJson.setLeaf(false);
                for (Brand brand : brands) {
                    CustomerTreeJson treeJson = new CustomerTreeJson();
                    treeJson.setId(brand.getId());
                    treeJson.setText(brand.getName());
                    treeJson.setLeaf(true);
                    customerTreeJson.addNode(treeJson);
                }
            } else {
                customerTreeJson.setLeaf(true);
            }
            customerTreeJson.setText(customer.getName());
            customerTreeJson.setId(customer.getId());
            nodeList.add(customerTreeJson);
        }
        return nodeList;
    }


    /**
     * 商家仓库树
     *
     * @return
     */
    @RequestMapping(value = "/product/supplier/store/tree")
    public
    @ResponseBody
    List<CustomerTreeJson> getSupplierStoreTree() {
        List<Supplier> customers = supplierService.queryAllCustomer();
        List<CustomerTreeJson> nodeList = new LinkedList<CustomerTreeJson>();
        for (Supplier customer : customers) {
            CustomerTreeJson customerTreeJson = new CustomerTreeJson();
            List<ProductStorage> storageList = supplierService.queryProductStorageByCustomerId(customer.getId());
            if (storageList.size() > 0) {
                customerTreeJson.setLeaf(false);
                for (ProductStorage storage : storageList) {
                    CustomerTreeJson treeJson = new CustomerTreeJson();
                    treeJson.setId(storage.getId());
                    treeJson.setText(storage.getName());
                    treeJson.setLeaf(true);
                    customerTreeJson.addNode(treeJson);
                }
            } else {
                customerTreeJson.setLeaf(true);
            }
            customerTreeJson.setText(customer.getName());
            //防止界面上的ID冲突，仓库树商家ID不参与计算，所以扩大倍数没有问题
            customerTreeJson.setId(customer.getId() * 1000);
            nodeList.add(customerTreeJson);
        }
        return nodeList;
    }


}
