package com.kariqu.productmanager.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.ProductSuperConversion;
import com.kariqu.productcenter.service.ProductSuperConversionService;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * User: Json.zhu
 * Date: 14-1-3
 * Time: 下午1:36
 * 商品超值兑换
 */
@Controller
public class ProductSuperConversionController {
    private final Log logger = LogFactory.getLog(ProductSuperConversionController.class);

    @Autowired
    private ProductSuperConversionService productSuperConversionService;

    @RequestMapping("product/superconversion")
    public void productAuctionList(HttpServletResponse response, String productId, int start, int limit) throws IOException {
        int proId = NumberUtils.toInt(productId);
        Page<ProductSuperConversion> productSuperConversionPage = productSuperConversionService.queryProductSuperConversionByProductId(start,limit,proId);
        new JsonResult(true).addData("totalCount", productSuperConversionPage.getTotalCount()).addData("result", productSuperConversionPage.getResult()).toJson(response);

    }

    @RequestMapping(value = "product/superconversion/add")
    public void addProductAuction( ProductSuperConversion productSuperConversion, HttpServletResponse response) throws IOException {
        try {
            productSuperConversionService.createProductSuperConversion(productSuperConversion);
            new JsonResult(true).toJson(response);
        }catch (Exception e){
            logger.error(e);
            new JsonResult(false, e.getMessage()).toJson(response);
        }

    }

    @RequestMapping(value = "product/superconversion/update")
    public void updateProductAuction( ProductSuperConversion productSuperConversion, HttpServletResponse response) throws IOException {
        try {
            productSuperConversionService.updateProductSuperConversionById(productSuperConversion);
            new JsonResult(true).toJson(response);
        }catch (Exception e){
            logger.error(e);
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "product/superconversion/delete")
    public void deleteProductAuction(int ids[], HttpServletResponse response) throws IOException {
        try{
            for(int id : ids){
                productSuperConversionService.deleteProductSuperConversionById(id);
            }
            new JsonResult(true).toJson(response);
        }catch (Exception e){
            logger.error(e);
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }
}
