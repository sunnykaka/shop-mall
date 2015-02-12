package com.kariqu.productmanager.web;

import com.kariqu.common.JsonResult;
import com.kariqu.productcenter.domain.*;
import com.kariqu.productcenter.service.ProductPictureResolver;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productmanager.helper.RecommendProduct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 商品推荐管理
 * 目前有组合和配件两种推荐形式，他们在数据库中形成一个中ID关联
 * User: Asion
 * Date: 11-9-9
 * Time: 下午2:05
 */
@Controller
public class RecommendController {

    private static final Log LOGGER = LogFactory.getLog(RecommendController.class);

    @Autowired
    private ProductService productService;


    /**
     * 附件推荐列表
     *
     * @param productId
     * @return
     */
    @RequestMapping(value = "/product/accessory/{productId}")
    public void accessoryList(@PathVariable("productId") int productId, HttpServletResponse response) throws IOException {
        Recommend recommend = productService.getRecommend(productId);
        List<Product> accessoryProducts = new ArrayList<Product>();
        for (Integer accessoryId : recommend.getProductList(RecommendType.ACCESSORY)) {
            Product product = productService.getProductById(accessoryId);
            if (product != null) {
                accessoryProducts.add(product);
            } else {
                LOGGER.warn("发现被下架的商品");
            }
        }
        List<RecommendProduct> accessoryModelList = recommendList(productId, accessoryProducts, RecommendType.ACCESSORY);
        new JsonResult(true).addData("accessoryList", accessoryModelList).toJson(response);
    }

    /**
     * 组合推荐列表
     *
     * @param productId
     * @return
     */
    @RequestMapping(value = "/product/compose/{productId}")
    public void composeList(@PathVariable("productId") int productId, HttpServletResponse response) throws IOException {
        Recommend recommend = productService.getRecommend(productId);
        List<Product> composeProducts = new ArrayList<Product>();
        for (Integer composeId : recommend.getProductList(RecommendType.COMPOSE)) {
            Product product = productService.getProductById(composeId);
            if (product != null) {
                composeProducts.add(product);
            } else {
                LOGGER.warn("发现被下架的商品");
            }
        }
        List<RecommendProduct> composeModelList = recommendList(productId, composeProducts, RecommendType.COMPOSE);
        new JsonResult(true).addData("composeList", composeModelList).toJson(response);
    }

    /**
     * 同类推荐列表
     *
     * @param productId
     * @return
     */
    @RequestMapping(value = "/product/sameCategory/{productId}")
    public void categoryList(@PathVariable("productId") int productId, HttpServletResponse response) throws IOException {
        Recommend recommend = productService.getRecommend(productId);
        List<Product> sameCategoryProducts = new ArrayList<Product>();
        for (Integer id : recommend.getProductList(RecommendType.CATEGORY)) {
            Product product = productService.getProductById(id);
            if (product != null) {
                sameCategoryProducts.add(product);
            } else {
                LOGGER.warn("发现被下架的商品");
            }
        }
        List<RecommendProduct> categoryModelList = recommendList(productId, sameCategoryProducts, RecommendType.CATEGORY);
        new JsonResult(true).addData("sameCategoryList", categoryModelList).toJson(response);
    }

    /**
     * 创建推荐
     *
     * @param productId
     * @param recommendProductId
     * @param recommendType
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/recommend/new", method = RequestMethod.POST)
    public void createRecommend(@RequestParam("productId") Integer productId,
                                @RequestParam("recommendProductId") Integer recommendProductId,
                                @RequestParam("recommendType") RecommendType recommendType,
                                HttpServletResponse response) throws IOException {
        try {
            Product product = productService.getProductById(recommendProductId);
            if (product == null) {
                new JsonResult(false, "商品不存在").toJson(response);
                return;
            }

            Recommend recommend = productService.getRecommend(productId);
            List<Integer> recommendProducts = recommend.getProductList(recommendType);
            if (recommendProducts.contains(recommendProductId)) {
                new JsonResult(false, "该商品推荐已存在").toJson(response);
                return;
            }

            productService.createRecommendProduct(productId, recommendProductId, recommendType);

        } catch (Exception e) {
            LOGGER.error("商品管理的添加推荐异常：" + e);
            new JsonResult(false, "添加推荐出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 删除推荐
     *
     * @param productId
     * @param recommendProductIds
     * @param recommendTypes
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/recommend/delete", method = RequestMethod.POST)
    public void deleteRecommend(@RequestParam("productId") Integer productId,
                                @RequestParam("recommendProductIds") Integer[] recommendProductIds,
                                @RequestParam("recommendTypes") RecommendType[] recommendTypes,
                                HttpServletResponse response) throws IOException {
        try {
            for (int i = 0; i < recommendProductIds.length; i++) {
                productService.deleteRecommendProduct(productId, recommendProductIds[i], recommendTypes[i]);
            }
        } catch (Exception e) {
            LOGGER.error("商品管理的删除推荐异常：" + e);
            new JsonResult(false, "删除推荐出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 把推荐商品列表转为前端可视的列表
     *
     * @param productId
     * @param productList
     * @param recommendType
     * @return
     */
    private List<RecommendProduct> recommendList(int productId, List<Product> productList, RecommendType recommendType) {
        List<RecommendProduct> recommendProductList = new LinkedList<RecommendProduct>();
        for (Product product : productList) {
            RecommendProduct recommendProduct = new RecommendProduct();
            recommendProduct.setProductId(productId);
            recommendProduct.setRecommendProductId(product.getId());
            recommendProduct.setProductName(product.getName());
            PictureDesc pictureDesc = productService.getPictureDesc(product.getId());
            recommendProduct.setPictureUrl(ProductPictureResolver.getMinSizeImgUrl(pictureDesc.getMainPicture().getPictureUrl()));
            recommendProduct.setRecommendType(recommendType);
            recommendProductList.add(recommendProduct);
        }
        return recommendProductList;
    }
}
