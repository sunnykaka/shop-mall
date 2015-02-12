package com.kariqu.productmanager.web;

import com.kariqu.categorycenter.domain.model.CategoryProperty;
import com.kariqu.categorycenter.domain.model.CategoryPropertyValue;
import com.kariqu.categorycenter.domain.model.PropertyType;
import com.kariqu.categorycenter.domain.service.CategoryPropertyService;
import com.kariqu.categorycenter.domain.util.PidVidJsonUtil;
import com.kariqu.categorycenter.domain.util.PropertyValueUtil;
import com.kariqu.categorymanager.helper.CPNamed;
import com.kariqu.categorymanager.helper.CPVNamed;
import com.kariqu.common.JsonResult;
import com.kariqu.productcenter.domain.ProductProperty;
import com.kariqu.productcenter.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 商品类目控制器
 * 在发布商品的时候从所选类目节点上调出类目属性和值从而构建商品发布表单
 * User: Asion
 * Date: 11-9-12
 * Time: 上午12:57
 */
@Controller
public class ProductCategoryController {

    @Autowired
    private CategoryPropertyService categoryPropertyService;

    @Autowired
    private ProductService productService;

    /**
     * 根据类目ID找到所有类目属性
     *
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/product/category/property/list")
    public void getCPList(@RequestParam("categoryId") int categoryId, String type, HttpServletResponse response) throws IOException {
        List<CategoryProperty> categoryProperties;
        if (type.equals("SELL_PROPERTY")) {
            categoryProperties = categoryPropertyService.getCategoryProperties(categoryId, PropertyType.SELL_PROPERTY);
        } else if (type.equals("KEY_PROPERTY")) {
            categoryProperties = categoryPropertyService.getCategoryProperties(categoryId, PropertyType.KEY_PROPERTY);
        } else {
            categoryProperties = categoryPropertyService.queryCategoryPropertyByCategoryId(categoryId);
        }
        List<CPNamed> cps = new LinkedList<CPNamed>();
        for (CategoryProperty categoryProperty : categoryProperties) {
            CPNamed cp = new CPNamed();
            cp.setCid(categoryId);
            cp.setPid(categoryProperty.getPropertyId());
            cp.setName(categoryPropertyService.getPropertyById(categoryProperty.getPropertyId()).getName());
            cp.setMultiValue(categoryProperty.isMultiValue());
            cps.add(cp);
        }
        new JsonResult(true).addData("cps", cps).toJson(response);
    }


    /**
     * 根据类目属性得到所有的类目属性值
     *
     * @param categoryId
     * @param propertyId
     * @return
     */
    @RequestMapping(value = "/product/category/property/value/list/{categoryId}/{propertyId}")
    public void getCPVList(@PathVariable("categoryId") int categoryId, @PathVariable("propertyId") int propertyId, HttpServletResponse response) throws IOException {
        List<CategoryPropertyValue> categoryPropertyValues = categoryPropertyService.getCategoryPropertyValues(categoryId, propertyId);
        List<CPVNamed> cpvs = new LinkedList<CPVNamed>();
        for (CategoryPropertyValue categoryPropertyValue : categoryPropertyValues) {
            CPVNamed cpv = new CPVNamed();
            cpv.setId(categoryPropertyValue.getId());
            cpv.setCid(categoryId);
            cpv.setPid(propertyId);
            cpv.setVid(categoryPropertyValue.getValueId());
            cpv.setName(categoryPropertyService.getValueById(categoryPropertyValue.getValueId()).getValueName());
            cpv.setPriority(categoryPropertyValue.getPriority());
            cpvs.add(cpv);
        }
        new JsonResult(true).addData("result", cpvs).addData("totalCount", cpvs.size()).toJson(response);
    }

    /**
     * 读取某个类目下的所有多值类目属性值
     *
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/product/category/property/value/multi/list")
    public
    @ResponseBody
    Map<String, List<CPVNamed>> getMultiCPVList(@RequestParam("categoryId") int categoryId, @RequestParam("type") String type) {
        Map<String, List<CPVNamed>> multiCPVMap = new HashMap<String, List<CPVNamed>>();
        List<CategoryProperty> categoryProperties;
        if (type.equals("KEY_PROPERTY")) {
            categoryProperties = categoryPropertyService.getCategoryProperties(categoryId, PropertyType.KEY_PROPERTY);
        } else if (type.equals("SELL_PROPERTY")) {
            categoryProperties = categoryPropertyService.getCategoryProperties(categoryId, PropertyType.SELL_PROPERTY);
        } else {
            categoryProperties = categoryPropertyService.queryCategoryPropertyByCategoryId(categoryId);
        }
        for (CategoryProperty categoryProperty : categoryProperties) {
            if (categoryProperty.isMultiValue()) {
                List<CategoryPropertyValue> categoryPropertyValues = categoryPropertyService.getCategoryPropertyValues(categoryId, categoryProperty.getPropertyId());
                List<CPVNamed> cpvs = new LinkedList<CPVNamed>();
                for (CategoryPropertyValue categoryPropertyValue : categoryPropertyValues) {
                    CPVNamed cpv = new CPVNamed();
                    cpv.setCid(categoryId);
                    cpv.setPid(categoryProperty.getPropertyId());
                    cpv.setVid(categoryPropertyValue.getValueId());
                    cpv.setName(categoryPropertyService.getValueById(categoryPropertyValue.getValueId()).getValueName());
                    cpvs.add(cpv);
                }
                multiCPVMap.put("" + categoryId + "-" + categoryProperty.getPropertyId(), cpvs);
            }
        }
        return multiCPVMap;
    }

    /**
     * 读取某个商品的所有关键属性和属性值映射，比如map.put('品牌','三星');
     * 注意map中的值是一个数组，在单值情况下这个数组长度是1
     *
     * @param productId
     * @return
     */
    @RequestMapping(value = "/product/category/product/cpv/map")
    public void getProductCPVMap(@RequestParam("productId") int productId, @RequestParam("type") PropertyType type, HttpServletResponse response) throws IOException {
        ProductProperty keyProperty = productService.getProductPropertyByPropertyType(productId, type);
        Map<Integer, List<Integer>> cpvMap = new HashMap<Integer, List<Integer>>();
        if (keyProperty != null) {
            List<Long> keyPidVids = PidVidJsonUtil.restore(keyProperty.getJson()).getPidvid();
            for (Long keyPidVid : keyPidVids) {
                PropertyValueUtil.PV pv = PropertyValueUtil.parseLongToPidVid(keyPidVid);
                if (cpvMap.get(pv.pid) == null) {
                    List<Integer> vidList = new LinkedList<Integer>();
                    vidList.add(pv.vid);
                    cpvMap.put(pv.pid, vidList);
                } else {
                    cpvMap.get(pv.pid).add(pv.vid);
                }
            }
        }
        new JsonResult(true).addData("map", cpvMap).toJson(response);
    }


}
