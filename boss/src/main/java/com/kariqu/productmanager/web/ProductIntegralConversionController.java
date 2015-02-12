package com.kariqu.productmanager.web;

import com.kariqu.common.DateUtils;
import com.kariqu.common.JsonResult;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.ProductIntegralConversion;
import com.kariqu.productcenter.service.ProductIntegralConversionService;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * User: Json.zhu
 * Date: 14-1-2
 * Time: 下午4:23
 * 积分兑换
 */
@Controller
public class ProductIntegralConversionController {
    private final Log logger = LogFactory.getLog(ProductIntegralConversionController.class);

    @Autowired
    private ProductIntegralConversionService productIntegralConversionService;

    @RequestMapping("product/conversion")
    public void productAuctionList(HttpServletResponse response, String productId, int start, int limit) throws IOException {
        Integer proId = NumberUtils.toInt(productId);
        Page<ProductIntegralConversion> productAuctionPage = productIntegralConversionService.queryProductIntegralConversionByProductId(start, limit, proId);
        new JsonResult(true).addData("totalCount", productAuctionPage.getTotalCount()).addData("result",  productAuctionPage.getResult()).toJson(response);

    }

    @RequestMapping(value = "product/conversion/add")
    public void addProductAuction(ProductIntegralConversion productIntegralConversion, HttpServletResponse response) throws IOException{
        try {
            productIntegralConversionService.createProductIntegralConversion(productIntegralConversion);
            new JsonResult(true).toJson(response);
        }catch (Exception e){
            logger.error(e);
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "product/conversion/update")
    public void updateProductAuction(ProductIntegralConversion productIntegralConversion, HttpServletResponse response) throws IOException{
        try {
            productIntegralConversionService.updateProductIntegralConversionById(productIntegralConversion);
            new JsonResult(true).toJson(response);
        }catch (Exception e){
            logger.error(e);
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "product/conversion/delete")
    public void deleteProductAuction(int[] ids, HttpServletResponse response) throws IOException{
            try{
                for(int id : ids){
                    productIntegralConversionService.deleteProductIntegralConversionById(id);
                }
                new JsonResult(true).toJson(response);
            }catch (Exception e){
                logger.error(e);
                new JsonResult(false, e.getMessage()).toJson(response);
            }
    }
}
