package com.kariqu.categorymanager.web;

import com.kariqu.categorycenter.domain.model.CategoryProperty;
import com.kariqu.categorycenter.domain.model.ProductCategory;
import com.kariqu.categorycenter.domain.model.Property;
import com.kariqu.categorycenter.domain.model.PropertyType;
import com.kariqu.categorycenter.domain.service.CategoryPropertyService;
import com.kariqu.categorycenter.domain.service.ProductCategoryService;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.productcenter.service.ProductService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 后台类目控制器
 * User: Asion
 * Date: 11-7-10
 * Time: 下午2:26
 */

@Controller
public class CategoryController {

    private final Log logger = LogFactory.getLog(CategoryController.class);

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryPropertyService categoryPropertyService;

    /**
     * 创建类目
     * parentId从客户端传过来，如果是-1则是根类目
     * 可以用逗号隔断多个值
     * <p/>
     * 每个类目在创建完成之后默认都有一个品牌属性
     *
     * @param parentId
     * @param productCategory
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/category/new", method = RequestMethod.POST)
    @Permission("创建了类目")
    public void createCategory(@RequestParam("parentId") int parentId, ProductCategory productCategory, HttpServletResponse response) throws IOException {
        try {
            ProductCategory parent = new ProductCategory();
            parent.setId(parentId);
            String name = productCategory.getName();
            //一个新增ID，客户端拿到ID可以做一些事情，比如展开类目树
            int categoryId = 0;
            for (String theName : name.split(",|，")) {
                int totalSize = productCategoryService.queryProductCategoryByNameAndParentId(theName, parent.getId());
                if (totalSize >= 1) {
                    new JsonResult(false, "类目" + theName + "已存在").toJson(response);
                    return;
                } else {
                    ProductCategory pc = new ProductCategory();
                    pc.setName(theName);
                    pc.setDescription(productCategory.getDescription());
                    pc.setParent(parent);

                    productCategoryService.createProductCategory(pc);
                    CategoryProperty categoryProperty = new CategoryProperty();
                    categoryProperty.setCategoryId(pc.getId());
                    Property property = new Property();
                    property.setName("品牌");
                    int brandId = categoryPropertyService.createPropertyIfNotExist(property);
                    categoryProperty.setPropertyId(brandId);
                    categoryProperty.setPropertyType(PropertyType.KEY_PROPERTY);
                    categoryProperty.setMultiValue(false);
                    categoryPropertyService.createCategoryProperty(categoryProperty);
                    categoryId = pc.getId();
                }
            }
            new JsonResult(true).addData("categoryId", categoryId).toJson(response);
        } catch (Exception e) {
            logger.error("添加后台类目错误：" + e);
            new JsonResult(false, "添加失败").toJson(response);
        }

    }

    /**
     * 更新类目的值
     *
     * @param categoryId
     * @param name
     * @param response
     * @throws IOException
     */
    @Permission("修改后台类目")
    @RequestMapping(value = "/category/update", method = RequestMethod.POST)
    public void updateCategory(int categoryId, String name, HttpServletResponse response) throws IOException {
        try {
            ProductCategory category = productCategoryService.getProductCategoryById(categoryId);
            String categoryName = category.getName();
            // 修改了类目名称
            if (!categoryName.equals(name.trim())) {
                // 根据新类目名称和父类目ID，查询该新类目是否已存在
                int resultCount = productCategoryService.queryProductCategoryByNameAndParentId(name.trim(), category.getParent().getId());

                if (resultCount >= 1) {
                    new JsonResult(false, "类目" + name + "已存在").toJson(response);
                    return;
                } else {
                    category.setName(name);
                    productCategoryService.updateProductCategory(category);
                    new JsonResult(true).toJson(response);
                }
            } else {   // 没有修改类目名称
                category.setName(name);
                productCategoryService.updateProductCategory(category);
                new JsonResult(true).toJson(response);
            }
        } catch (Exception e) {
            logger.error("更新类目值错误：" + e);
            new JsonResult(false, "更新失败").toJson(response);
        }

    }


    /**
     * 获取这个类目的信息，
     * 根据是否在类目上发布商品来判断是否是叶子类目
     * 现在每个类目都可设置属性，子类目选择继承父类目的属性和值
     * <p/>
     * 根据parent id来判断是否是根类目
     *
     * @param categoryId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/category/info")
    public void checkLeaf(int categoryId, HttpServletResponse response) throws IOException {
        ProductCategory category = productCategoryService.getProductCategoryById(categoryId);
        JsonResult jsonResult = new JsonResult(true);
        if (category.getParent().getId() == -1) {
            jsonResult.addData("root", true);
        } else {
            jsonResult.addData("root", false);
        }

        if (productService.existProduct(categoryId)) {
            jsonResult.addData("leaf", true);
        } else {
            jsonResult.addData("leaf", false);
        }
        jsonResult.toJson(response);
    }


    /**
     * 删除后台类目
     * 如果类目上发布了商品不能删除
     *
     * @param categoryId
     * @param response
     * @throws IOException
     */
    @Permission("删除后台类目")
    @RequestMapping(value = "/category/delete", method = RequestMethod.POST)
    public void deleteCategory(int categoryId, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            if (productService.existProduct(categoryId)) {
                new JsonResult(true).addData("prodExist", true).toJson(response);
            } else {
                productCategoryService.deleteProductCategory(categoryId);
                new JsonResult(true).addData("prodExist", false).toJson(response);
            }
        } catch (Exception e) {
            logger.error("删除后台类目错误：" + e);
            new JsonResult(false, "删除失败").toJson(response);
        }
    }

}
