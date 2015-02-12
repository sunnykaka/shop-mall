package com.kariqu.buyer.web.controller.shop;

import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.categorycenter.domain.model.CategoryProperty;
import com.kariqu.categorycenter.domain.model.Property;
import com.kariqu.categorycenter.domain.model.PropertyType;
import com.kariqu.categorycenter.domain.service.CategoryPropertyService;
import com.kariqu.designcenter.client.service.ProdRenderPageService;
import com.kariqu.designcenter.domain.open.module.Product;
import com.kariqu.productcenter.service.ProductService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 商品对比控制器
 * 目前是同类商品可对比，如果商品列表是通过聚合搜索
 * 则会大量的出现商品不可对比，虽然他们都在同一个类目下
 * <p/>
 * 所以TODO 取消类目限制，根据属性来比较
 * User: Alec
 * Date: 12-7-19
 * Time: 下午2:34
 */

@Controller
public class ContrastController {

    private static final Log logger = LogFactory.getLog(ContrastController.class);

    @Resource(name = "prodOpenApiContext")
    private HashMap map;


    @Autowired
    private CategoryPropertyService categoryPropertyService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProdRenderPageService prodRenderPageService;

    /** 最多商品比较数量, 写成全局配置, 还是 spring 注入, 或直接写在应用程序? */
    private static final int MAX_COMPARE_PRODUCT_NUM = 4;

    @RequestMapping(value = "/compare")
    @RenderHeaderFooter
    public String productCompare(String ids, Model model) {
        List<com.kariqu.productcenter.domain.Product> prodList = new ArrayList<com.kariqu.productcenter.domain.Product>();
        List<String> imageList = new ArrayList<String>();
        List<String> priceList = new ArrayList<String>();
        Map<String, List<String>> keyPairMap = new LinkedHashMap<String, List<String>>();
        Map<Integer, String> descMap = new LinkedHashMap<Integer, String>();
        Map<String, Object> context = new HashMap<String, Object>();
        model.addAttribute("category", prodRenderPageService.prodRenderGlobalCommonModule("category", context).getPageContent());//显示前台类目
        try {
            if (StringUtils.isNotBlank(ids)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("对比的商品ID为" + ids);
                }
                String[] productIds = ids.split(",");
                for (String productId : productIds) {
                    int id = Integer.parseInt(productId);
                    com.kariqu.productcenter.domain.Product product = productService.getProductById(id);
                    if (null == product) {
                        logger.warn("商品对比中Id为：" + id + " 的商品不存在被自动过滤掉");
                        continue;
                    }

                    imageList.add(productService.getPictureDesc(id).getMainPicture().getPictureUrl());

                    prodList.add(product);
                    String description = product.getDescription();
                    if (StringUtils.isNotEmpty(description)) {
                        descMap.put(product.getId(), description.replaceAll("\n", "<br/>"));
                    }

                    priceList.add(productService.getProductPrice(id));
                    List<Product.PropertyValuePair> propertyValuePairs = ((com.kariqu.designcenter.domain.open.api.ProductService) map.get("productService")).queryKeyPropertyPair(id, 20);
                    // 过滤需要对比的销售属性
                    propertyValuePairs = productKeyPropertyPair(id, propertyValuePairs);

                    for (Product.PropertyValuePair propertyValuePair : propertyValuePairs) {
                        String property = propertyValuePair.getProperty();
                        if (!"价格".equals(property)) {
                            List<String> list = keyPairMap.get(property);
                            if (list == null) {
                                list = new ArrayList<String>(MAX_COMPARE_PRODUCT_NUM);
                                keyPairMap.put(property, list);
                            }
                            list.add(propertyValuePair.getValue());
                        }
                    }
                }
                for (Map.Entry<String, List<String>> keyPair : keyPairMap.entrySet()) {
                    List<String> list = keyPair.getValue();
                    if (list.size() < MAX_COMPARE_PRODUCT_NUM) {
                        int size = list.size();
                        for (int i = 1; i <= MAX_COMPARE_PRODUCT_NUM - size; i++) {
                            list.add("");
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("商品对比出现异常", e);
            return "contrastProduct";
        }

        model.addAttribute("nullList", new String[MAX_COMPARE_PRODUCT_NUM - imageList.size()]);
        model.addAttribute("imageList", imageList);
        model.addAttribute("priceList", priceList);
        model.addAttribute("prodList", prodList);
        model.addAttribute("compareMap", keyPairMap);
        model.addAttribute("descMap", descMap);
        return "contrastProduct";
    }


    /**
     * 过滤商品对比的销售属性
     *
     * @param productId
     * @param pairList
     * @return
     */
    private List<Product.PropertyValuePair> productKeyPropertyPair(int productId, List<Product.PropertyValuePair> pairList) {
        int categoryId = productService.getProductById(productId).getCategoryId();

        List<Product.PropertyValuePair> propertyPairList = new ArrayList<Product.PropertyValuePair>();
        if (pairList != null) {
            /**
             * 获取后台类目属性
             */
            List<CategoryProperty> categoryPropertyList = categoryPropertyService.getCategoryProperties(categoryId, PropertyType.KEY_PROPERTY);
            /**
             * 获取需要加入对比的属性名称
             */
            List<String> propertyValueList = new ArrayList<String>();
            for (CategoryProperty categoryProperty : categoryPropertyList) {
                if (categoryProperty.isCompareable()) {
                    Property property = categoryPropertyService.getPropertyById(categoryProperty.getPropertyId());
                    if (null != property)
                        propertyValueList.add(property.getName());
                }
            }

            /**
             * 取出不需要对比的属性
             */
            for (Product.PropertyValuePair property : pairList) {
                for (String propertyValue : propertyValueList) {
                    if (property.getProperty().equals(propertyValue)) {
                        propertyPairList.add(property);
                    }
                }
            }
        }
        return propertyPairList;
    }

    /**
     * 获取类目属性数量
     *
     * @param cid
     * @return
     */
    @RequestMapping(value = "/propertyNumber/{cid}")
    public void accessoryList(@PathVariable("cid") int cid, HttpServletResponse response) throws IOException {
        int propertyNumber = categoryPropertyService.getCategoryProperties(cid, PropertyType.KEY_PROPERTY).size();
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(String.valueOf(propertyNumber));
    }


}

