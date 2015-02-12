package com.kariqu.productmanager.web;

import com.kariqu.categorycenter.domain.service.ProductCategoryService;
import com.kariqu.common.JsonResult;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.designcenter.client.service.ModulePreviewService;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.productcenter.domain.Html;
import com.kariqu.productcenter.domain.HtmlDesc;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productmanager.helper.ProductModel;
import com.kariqu.productmanager.helper.ProductQuery;
import com.kariqu.suppliercenter.service.SupplierService;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 商品标题和描述管理控制器
 * User: Asion
 * Date: 11-9-8
 * Time: 下午1:26
 */
@Controller
public class ProductManageController {

    private final Log logger = LogFactory.getLog(ProductManageController.class);

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ModulePreviewService modulePreviewService;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private com.kariqu.designcenter.domain.open.api.ProductService openProductService;

    public static final String ProductDetailModuleName = "productSku";


    /**
     * 带状态的商品列表
     *
     * @param start
     * @param limit
     * @param online
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/list/status")
    public void productOnlineStatusList(@RequestParam int start, @RequestParam int limit, boolean online, HttpServletResponse response) throws IOException {
        Page<Product> productPage = productService.queryProductsByOnlineStatus(online, new Page<Product>(start / limit + 1, limit));
        List<ProductModel> productGrid = createProductGrid(productPage.getResult());
        new JsonResult(true).addData("totalCount", productPage.getTotalCount()).addData("result", productGrid).toJson(response);
    }

    /**
     * 通过商品编码查询
     *
     * @param code
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/list/code")
    public void productListByCode(String code, HttpServletResponse response) throws IOException {
        Product productByCode = productService.getProductByCode(code);
        List<Product> list = new LinkedList<Product>();
        if (productByCode != null) {
            list.add(productByCode);
        }
        List<ProductModel> productGrid = createProductGrid(list);
        new JsonResult(true).addData("totalCount", 1).addData("result", productGrid).toJson(response);
    }


    /**
     * 读取产品列表
     *
     * @param productQuery
     * @return
     */
    @RequestMapping(value = "/product/list")
    public void productList(ProductQuery productQuery, HttpServletResponse response) throws IOException {
        List<Product> productList = new ArrayList<Product>();
        int total = 0;
        if (productQuery.isProductId()) { //如果有ID筛选
            Product product = productService.getProductById(NumberUtils.toInt(productQuery.getSearch()));
            if (product != null) {
                productList.add(product);
                total = 1;
            }
        } else if (productQuery.getSearch().length() > 0) { //模糊查询
            Page<Product> page;
            page = productService.queryProductByFuzzyPage(productQuery.getSearch(), productQuery.getPageNo(), productQuery.getLimit());
            productList = page.getResult();
            total = page.getTotalCount();
        } else {
            Page<Product> page;
            if (productQuery.getCategoryId() > 0) {
                page = productService.queryProductsByCategoryIdByPage(productQuery.getCategoryId(), productQuery.getPageNo(), productQuery.getLimit());
            } else if (productQuery.getCustomerId() > 0) {
                page = productService.queryProductsByCustomerIdByPage(productQuery.getCustomerId(), productQuery.getPageNo(), productQuery.getLimit());
            } else if (productQuery.getBrandId() > 0) {
                page = productService.queryProductsByBrandIdByPage(productQuery.getBrandId(), productQuery.getPageNo(), productQuery.getLimit());
            } else if (productQuery.getStoreId() > 0) {
                page = productService.queryProductsByStoreIdByPage(productQuery.getStoreId(), productQuery.getPageNo(), productQuery.getLimit());
            } else {
                page = productService.queryAllProductsByPage(productQuery.getPageNo(), productQuery.getLimit());
            }
            productList = page.getResult();
            total = page.getTotalCount();
        }
        List<ProductModel> productGrid = createProductGrid(productList);
        new JsonResult(true).addData("totalCount", total).addData("result", productGrid).toJson(response);
    }


    private List<ProductModel> createProductGrid(List<Product> productList) {
        List<ProductModel> productModelList = new ArrayList<ProductModel>();
        for (Product product : productList) {
            ProductModel productModel = new ProductModel();
            productModel.setId(product.getId());
            productModel.setCategoryId(product.getCategoryId());
            productModel.setDescription(product.getDescription());
            productModel.setProductCode(product.getProductCode());
            productModel.setName(product.getName());
            productModel.setCategoryName(productCategoryService.getProductCategoryById(product.getCategoryId()).getName());
            productModel.setBrandId(product.getBrandId());
            productModel.setBrandName(supplierService.queryBrandById(product.getBrandId()).getName());
            productModel.setCustomerId(product.getCustomerId());
            productModel.setOnline(product.isOnline());
            productModel.setTagType(product.getTagType()==null ? "" :product.getTagType().name());
            productModelList.add(productModel);
        }
        return productModelList;
    }


    @RequestMapping(value = "/product/title/update", method = RequestMethod.POST)
    public void updateTitle(@RequestParam("productId") Integer productId, @RequestParam("productCode") String productCode, @RequestParam("name") String name, @RequestParam("description") String description, @RequestParam("tagType") String tagType, HttpServletResponse response) throws IOException {
        try {
            Product product = productService.getProductById(productId);
            product.setName(name);
            product.setDescription(description);
            product.setProductCode(productCode);
            product.setTagType(getTagType(tagType));
            productService.updateProduct(product);

            productService.notifyProductUpdate(productId);
        } catch (Exception e) {
            logger.error("商品管理的更新商品信息异常：" + e);
            new JsonResult(false, "更新商品信息出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    private Product.TagType getTagType(String tagType) {
        Product.TagType tagTypeEnum;
        try{
            tagTypeEnum = Enum.valueOf(Product.TagType.class, tagType);
        }   catch (IllegalArgumentException e){
            tagTypeEnum = Product.TagType.DEFAULT;
        }
        return tagTypeEnum;
    }


    @RequestMapping(value = "/product/preview/{id}")
    public String previewCommonModule(@PathVariable("id") int id, Model model) throws IOException {
        HashMap<String, Object> context = new HashMap<String, Object>();
        com.kariqu.designcenter.domain.open.module.Product product = openProductService.buildOpenProduct(id);
        context.put(RenderConstants.PRODUCT_CONTEXT_KEY, product);
        model.addAttribute("pageContent", modulePreviewService.previewCommonModule(ProductDetailModuleName, context));
        return "layout/moduleLayout";
    }


    @RequestMapping(value = "/product/html/list/{productId}")
    public void productHtmlDesc(@PathVariable("productId") int productId, HttpServletResponse response) throws IOException {
        HtmlDesc htmlDesc = productService.getHtmlDesc(productId);
        new JsonResult(true).addData("totalCount", htmlDesc.getHtmlList().size()).addData("result", htmlDesc.getHtmlList()).toJson(response);
    }

    @RequestMapping(value = "/product/html/new", method = RequestMethod.POST)
    public void crateHtmlDesc(Html html, HttpServletResponse response) throws IOException {
        try {
            HtmlDesc htmlDesc = productService.getHtmlDesc(html.getProductId());
            if (htmlDesc.hasHtml(html.getName())) {
                new JsonResult(false, "名字重复了").toJson(response);
                return;
            }
            productService.createHtml(html);
        } catch (Exception e) {
            logger.error("商品管理的添加描述信息异常：" + e);
            new JsonResult(false, "添加描述信息出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/product/html/update", method = RequestMethod.POST)
    public void updateHtmlDesc(Html html, HttpServletResponse response) throws IOException {
        try {
            productService.updateHtml(html);
        } catch (Exception e) {
            logger.error("商品管理的更新描述信息异常：" + e);
            new JsonResult(false, "更新描述信息出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/product/html/delete", method = RequestMethod.POST)
    public void deleteHtmlDesc(@RequestParam("productId") Integer productId, @RequestParam("names") String[] names, HttpServletResponse response) throws IOException {
        try {
            productService.deleteHtmlDesc(productId, names);
        } catch (Exception e) {
            logger.error("商品管理的删除描述信息异常：" + e);
            new JsonResult(false, "删除描述信息出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/product/deleteBrandNameForAllProduct", method = RequestMethod.POST)
    public void deleteBrandNameForAllProduct(HttpServletResponse response) throws IOException {
        try {
            int changeCount = productService.deleteBrandNameForAllProduct();
            new JsonResult(true, changeCount > 0 ? "商品名更新成功! 共更新" + changeCount + "个产品名称."
                    : "未匹配到需要更新的商品名!").toJson(response);
        } catch (Exception e) {
            logger.error("更新常数时异常:" + e.getMessage());
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

}
