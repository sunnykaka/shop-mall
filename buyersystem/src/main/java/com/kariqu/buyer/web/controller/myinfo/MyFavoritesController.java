package com.kariqu.buyer.web.controller.myinfo;

import com.google.common.collect.Maps;
import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.common.PageTitle;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.common.pagenavigator.PageProcessor;
import com.kariqu.productcenter.domain.PictureDesc;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.domain.ProductCollect;
import com.kariqu.productcenter.service.ProductPictureResolver;
import com.kariqu.productcenter.service.ProductService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户收藏夹
 * User: Alec
 * Date: 12-11-2
 * Time: 上午9:47
 */
@Controller
@PageTitle("我的收藏")
public class MyFavoritesController  {

    private final Log logger = LogFactory.getLog(MyFavoritesController.class);

    @Autowired
    private ProductService productService;

    /**
     * 收藏某个商品
     *
     */
    @RequestMapping(value = "/my/favorites/add", method = RequestMethod.POST)
    public void addProductCollect(@RequestParam("productId") int productId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);

            if (logger.isDebugEnabled()) {
                logger.debug("要关注的商品ID为:" + productId);
            }
            Product product = productService.getProductById(productId);
            if (null == product) {
                new JsonResult(false, "该商品已被删除！").toJson(response);
                return;
            }
            ProductCollect productCollect = new ProductCollect();
            productCollect.setProductId(productId);
            productCollect.setUserId(String.valueOf(sessionUserInfo.getId()));
            if (productService.hadProductCollect(productCollect)) {
                new JsonResult(false, "已经关注").addData("productCollectNum",
                        productService.queryProductCollectNum(sessionUserInfo.getId())).toJson(response);
                return;
            }

            productCollect.setProductName(product.getName());
            productCollect.setProductMainPicture(getMainPictureByList(productId));
            productCollect.setUnitPrice(productService.getProductPrice(productId));
            productService.createProductCollect(productCollect);

            new JsonResult(true).addData("productCollectNum",
                    productService.queryProductCollectNum(sessionUserInfo.getId())).toJson(response);
        } catch (Exception e) {
            logger.error("添加关注时出错", e);
            new JsonResult(false, "关注失败!").toJson(response);
        }
    }

    /**
     * 取列表主图
     */
    private String getMainPictureByList(Integer productId) {
        PictureDesc pictureDesc = productService.getPictureDesc(productId);

        return pictureDesc.getMainPicture().getPictureUrl();
    }

    @RenderHeaderFooter
    @RequestMapping(value = "/my/favorites")
    public String intoUserFavorites(@RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
                                    HttpServletRequest request, Model model) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);

        Page<ProductCollect> productCollectPage = productService.queryProductCollectByPage(pageNo, 10, sessionUserInfo.getId());

        if (productCollectPage.getResult().size() > 0) {
            model.addAttribute("productState", getProductCollectPage(productCollectPage.getResult()));
            model.addAttribute("consultationPageBar", PageProcessor.process(productCollectPage));
            model.addAttribute("productCollectPage", productCollectPage);

            model.addAttribute("productCollectDepreciate", getProductCollectDepreciate(productCollectPage.getResult()));//降价通知
        }

        model.addAttribute("contentVm", "myinfo/myFavorites.vm");
        return "myinfo/myInfoLayout";
    }

    /**
     * 判断商品是否下架
     *
     * @param ProductCollectList
     * @return
     */
    private Map<Integer, Boolean> getProductCollectPage(List<ProductCollect> ProductCollectList) {
        Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
        for (ProductCollect productCollect : ProductCollectList) {
            int productId = productCollect.getProductId();
            Product product = productService.getProductById(productId);
            //检查商品是否被删除
            if (null != product) {
                //检查是否被下架
                map.put(productId, product.isOnline());
            } else {
                map.put(productId, false);
            }
        }
        return map;
    }

    /**
     * 降价通知
     * @param productCollectList
     * @return
     */
    private Map<Integer, Double> getProductCollectDepreciate(List<ProductCollect> productCollectList){
        Map<Integer,Double> depreciateMap = Maps.newHashMap();
        for(ProductCollect productCollect : productCollectList){
            Double unitPrice = Double.parseDouble(productCollect.getUnitPrice());
            String realPriceStr = productService.getProductPrice(productCollect.getProductId());
            Double realPrice = Double.parseDouble(realPriceStr);
            if(realPrice < unitPrice){
                depreciateMap.put(productCollect.getProductId(),(unitPrice - realPrice));
            }else {
                depreciateMap.put(productCollect.getProductId(),0d);
            }
        }
        return depreciateMap;
    }


    /**
     * 取消收藏
     */
    @RequestMapping(value = "/my/favorites/delete", method = RequestMethod.POST)
    public void deleteProductCollect(@RequestParam("id") int id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
            productService.cancelProductCollect(id, sessionUserInfo.getId());
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("删除关注("+id+")时出错", e);
            new JsonResult(false, "服务器出错").toJson(response);
        }
    }

    /**
     * 批量取消上篇收藏
     */
    @RequestMapping(value = "/my/favorites/delete/batch", method = RequestMethod.POST)
    public void deleteProductCollect(int[] ids, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
            if (ids.length > 0) {
                for (int id : ids) {
                    if (id > 0)
                        productService.cancelProductCollect(id, sessionUserInfo.getId());
                }
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("删除关注时出错ID[" + ids + "]", e);
            new JsonResult(false, "服务器出错").toJson(response);
        }
    }


}
