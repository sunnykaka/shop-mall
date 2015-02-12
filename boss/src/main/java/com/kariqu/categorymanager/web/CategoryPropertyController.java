package com.kariqu.categorymanager.web;

import com.kariqu.categorycenter.domain.model.*;
import com.kariqu.categorycenter.domain.service.CategoryPropertyService;
import com.kariqu.categorymanager.helper.CPNamed;
import com.kariqu.categorymanager.helper.CPVNamed;
import com.kariqu.categorymanager.helper.CpvRow;
import com.kariqu.categorymanager.helper.PropertyValueDetailNamed;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 管理类目属性相关数据
 * 比如属性值，属性细节
 * User: Asion
 * Date: 11-10-31
 * Time: 上午11:13
 */
@Controller
public class CategoryPropertyController {

    private final Log logger = LogFactory.getLog(CategoryPropertyController.class);

    @Autowired
    private CategoryPropertyService categoryPropertyService;


    /**
     * 创建类目属性，如果属性是销售属性则默认为多值，界面的选择不起作用
     * 同时可以设定属性的值
     *
     * @param cp
     * @param name
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/category/cp/new", method = RequestMethod.POST)
    @Permission("创建了类目属性")
    public void createCp(CategoryProperty cp, String name, String value, HttpServletResponse response) throws IOException {
        try {
            if (cp.getPropertyType() == PropertyType.SELL_PROPERTY) {
                cp.setMultiValue(true);
            }
            int pid = categoryPropertyService.createPropertyIfNotExist(new Property(name));
            cp.setPropertyId(pid);

            categoryPropertyService.createCategoryProperty(cp);

            if (StringUtils.isNotEmpty(value)) {
                String[] values = value.split(",|，"); //用逗号隔开的多值
                Set<String> valueSet = new HashSet<String>(); //去重
                for (String v : values) {
                    valueSet.add(v);
                }
                List<Integer> newValue = new LinkedList<Integer>();
                for (String valueName : valueSet) {
                    int vid = categoryPropertyService.createValueIfNotExist(new Value(valueName));
                    newValue.add(vid);
                }
                for (Integer vid : newValue) {
                    CategoryPropertyValue cpv = new CategoryPropertyValue();
                    cpv.setCategoryId(cp.getCategoryId());
                    cpv.setPropertyId(pid);
                    cpv.setValueId(vid);
                    categoryPropertyService.createCategoryPropertyValue(cpv);
                }
            }
            new JsonResult(true).toJson(response);

        } catch (Exception e) {
            logger.error("创建类目属性错误：" + e);
            new JsonResult(false, "创建类目属性失败，请确保正确输入和不重复").toJson(response);
        }

    }


    /**
     * 修改类目属性
     *
     * @param cp
     * @param name
     * @param response
     * @throws IOException
     */
    @Permission("修改后台类目属性")
    @RequestMapping(value = "/category/cp/update", method = RequestMethod.POST)
    public void updateCp(CategoryProperty cp, String name, HttpServletResponse response) throws IOException {
        try {
            if (cp.getPropertyType() == PropertyType.SELL_PROPERTY) {
                cp.setMultiValue(true);
            }
            CategoryProperty categoryPropertyById = categoryPropertyService.getCategoryPropertyById(cp.getId());

            if (!checkIfCanUpdate(categoryPropertyById.getPropertyId(), name)) {
                new JsonResult(false, "请保持类目上必须有品牌这个属性以及属性名不允许多个").toJson(response);
                return;
            }
            Property property = new Property();
            property.setName(name);
            //新属性ID
            int pid = categoryPropertyService.createPropertyIfNotExist(property);
            //原来属性值重新关联到新属性上
            List<CategoryPropertyValue> categoryPropertyValues = categoryPropertyService.getCategoryPropertyValues(categoryPropertyById.getCategoryId(), categoryPropertyById.getPropertyId());
            for (CategoryPropertyValue categoryPropertyValue : categoryPropertyValues) {
                categoryPropertyValue.setPropertyId(pid);
                categoryPropertyService.updateCategoryPropertyValue(categoryPropertyValue);
            }
            categoryPropertyById.setPropertyId(pid);
            categoryPropertyById.setMultiValue(cp.isMultiValue());
            categoryPropertyById.setPropertyType(cp.getPropertyType());
            categoryPropertyById.setCompareable(cp.isCompareable());
            categoryPropertyService.updateCategoryProperty(categoryPropertyById);
            new JsonResult(true).toJson(response);
        } catch (IOException e) {
            logger.error("修改类目属性发生错误：" + e);
            new JsonResult(false, "修改失败").toJson(response);
        }
    }

    /**
     * 加载命名类目属性
     *
     * @param cid
     * @param pid
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/category/cp/{cid}/{pid}")
    public void getCP(@PathVariable("cid") int cid, @PathVariable("pid") int pid, HttpServletResponse response) throws IOException {
        CategoryProperty categoryProperty = categoryPropertyService.queryCategoryPropertyByCategoryIdAndPropertyId(cid, pid);
        CPNamed cpNamed = new CPNamed();
        cpNamed.setId(categoryProperty.getId());
        cpNamed.setCid(categoryProperty.getCategoryId());
        cpNamed.setPid(categoryProperty.getPropertyId());
        cpNamed.setPropertyType(categoryProperty.getPropertyType());
        cpNamed.setMultiValue(categoryProperty.isMultiValue());
        cpNamed.setCompareable(categoryProperty.isCompareable());
        cpNamed.setName(categoryPropertyService.getPropertyById(pid).getName());
        new JsonResult(true).addData("object", cpNamed).toJson(response);
    }

    /**
     * 加载命名类目属性值
     *
     * @param cid
     * @param pid
     * @param vid
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/category/cpv/{cid}/{pid}/{vid}")
    public void getCPV(@PathVariable("cid") int cid, @PathVariable("pid") int pid, @PathVariable("vid") int vid, HttpServletResponse response) throws IOException {
        CategoryPropertyValue categoryPropertyValue = categoryPropertyService.queryCategoryPropertyValueByCategoryIdAndPropertyIdAndValueId(cid, pid, vid);
        CPVNamed cpvNamed = new CPVNamed();
        cpvNamed.setId(categoryPropertyValue.getId());
        cpvNamed.setCid(cid);
        cpvNamed.setPid(pid);
        cpvNamed.setVid(vid);
        cpvNamed.setName(categoryPropertyService.getValueById(vid).getValueName());
        new JsonResult(true).addData("object", cpvNamed).toJson(response);
    }


    /**
     * 更新类目属性的优先级
     *
     * @param id
     * @param priority
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/category/cp/priority/update", method = RequestMethod.POST)
    @Permission("更改类目属性优先级")
    public void updateCpPriority(int id, int priority, HttpServletResponse response) throws IOException {
        try {
            CategoryProperty categoryProperty = categoryPropertyService.getCategoryPropertyById(id);
            categoryProperty.setPriority(priority);
            categoryPropertyService.updateCategoryProperty(categoryProperty);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("更新类目属性的优先级错误：" + e);
            new JsonResult(false, "更新失败").toJson(response);
        }

    }


    /**
     * 更新类目属性值优先级
     *
     * @param id
     * @param priority
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/category/cpv/priority/update", method = RequestMethod.POST)
    @Permission("更改类目属性值优先级")
    public void updateCpvPriority(int id, int priority, HttpServletResponse response) throws IOException {
        try {
            CategoryPropertyValue categoryPropertyValue = categoryPropertyService.getCategoryPropertyValueById(id);
            categoryPropertyValue.setPriority(priority);
            categoryPropertyService.updateCategoryPropertyValue(categoryPropertyValue);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("更新类目属性的优先级错误：" + e);
            new JsonResult(false, "更新失败").toJson(response);
        }

    }

    /**
     * 创建类目属性值
     * 可以用逗号隔开填多个值
     *
     * @param cpv
     * @param name
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/category/cpv/new", method = RequestMethod.POST)
    @Permission("创建属性值")
    public void createCpv(CategoryPropertyValue cpv, String name, HttpServletResponse response) throws IOException {
        try {
            String[] values = name.split(",|，"); //用逗号隔开的多值
            Set<String> valueSet = new HashSet<String>(); //去重
            for (String v : values) {
                valueSet.add(v);
            }
            List<Integer> newValue = new LinkedList<Integer>();
            for (String valueName : valueSet) {
                int vid = categoryPropertyService.createValueIfNotExist(new Value(valueName));
                newValue.add(vid);
                cpv.setValueId(vid);
                categoryPropertyService.createCategoryPropertyValue(cpv);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("创建类目属性值错误：" + e);
            new JsonResult(false, "添加失败，请检查是否重复或者数据非法").toJson(response);
        }

    }

    /**
     * 更新类目属性值
     *
     * @param cpv
     * @param name
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/category/cpv/update", method = RequestMethod.POST)
    @Permission("更新类目属性值")
    public void updateCpv(CategoryPropertyValue cpv, String name, HttpServletResponse response) throws IOException {
        try {
            Value value = new Value(name);

            int vid = categoryPropertyService.createValueIfNotExist(value);

            CategoryPropertyValue categoryPropertyValueById = categoryPropertyService.getCategoryPropertyValueById(cpv.getId());
            if (null == categoryPropertyValueById) {
                logger.error("类目属性值不存在：id:" + cpv.getId() + ",检查cpv表");
                new JsonResult(false, "该类目属性值不存在请联系管理员！").toJson(response);
                return;
            }

            categoryPropertyValueById.setValueId(vid);

            if (isCpvAlreadyExists(categoryPropertyValueById)) {
                new JsonResult(false, "类目已存在该属性值，请换一个！").toJson(response);
                return;
            }

            categoryPropertyService.updateCategoryPropertyValue(categoryPropertyValueById);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("更新类目属性值错误：" + e);
            new JsonResult(false, "更新失败").toJson(response);
        }
    }

    /**
     * 判断是否已经存在cpv
     *
     * @param cpv
     * @return
     */
    private boolean isCpvAlreadyExists(CategoryPropertyValue cpv) {
        return null != categoryPropertyService.queryCategoryPropertyValueByCategoryIdAndPropertyIdAndValueId(cpv.getCategoryId(), cpv.getPropertyId(), cpv.getValueId());
    }

    /**
     * 删除属性CP,同时要删除CPV
     * 浏览器会传入一系列的cid-pid字符串，服务端要将每个字符串拆开成类目ID和属性ID
     * 更具类目ID和属性ID就以删除类目属性，同时删除对应的类目属性值
     *
     * @param cps      cid-pid字符串集合
     * @param response
     */
    @Permission("删除后台类目属性")
    @RequestMapping(value = "/category/cps/delete", method = RequestMethod.POST)
    public void deleteCPs(String[] cps, HttpServletResponse response) throws IOException {
        try {
            boolean flag = false;
            for (String cp : cps) {
                String[] cidPidForDelete = cp.split("-");
                int categoryId = Integer.parseInt(cidPidForDelete[0]);
                int propertyId = Integer.parseInt(cidPidForDelete[1]);
                if (is_Brand_Property(propertyId)) {
                    flag = true;
                } else {
                    categoryPropertyService.deleteCategoryPropertyByCPId(categoryId, propertyId);
                    categoryPropertyService.deleteCategoryPropertyValueByCPId(categoryId, propertyId);
                }
            }
            if (flag) {
                new JsonResult(false, "品牌属性不能删除").toJson(response);
            } else {
                new JsonResult(true).toJson(response);
            }

        } catch (Exception e) {
            logger.error("删除删除属性CP错误：" + e);
            new JsonResult(false, "删除失败").toJson(response);
        }
    }

    /**
     * 删除类目属性同时删除类目属性值
     *
     * @param cid
     * @param pid
     * @param response
     * @throws IOException
     */
    @Permission("删除类目属性和值")
    @RequestMapping(value = "/category/cp/delete", method = RequestMethod.POST)
    public void deleteCP(int cid, int pid, HttpServletResponse response) throws IOException {
        try {
            if (is_Brand_Property(pid)) {
                new JsonResult(false, "品牌属性不能删除").toJson(response);
            } else {
                categoryPropertyService.deleteCategoryPropertyByCPId(cid, pid);
                categoryPropertyService.deleteCategoryPropertyValueByCPId(cid, pid);
                new JsonResult(true).toJson(response);
            }
        } catch (Exception e) {
            logger.error("删除属性出错：" + e);
            new JsonResult(false, "删除属性失败").toJson(response);
        }

    }


    /**
     * 删除cpv
     *
     * @param cid
     * @param pid
     * @param vid
     * @param response
     * @throws IOException
     */
    @Permission("删除类目属性值")
    @RequestMapping(value = "/category/cpv/delete", method = RequestMethod.POST)
    public void deleteCPV(int cid, int pid, int vid, HttpServletResponse response) throws IOException {
        try {
            categoryPropertyService.deleteCategoryPropertyValueByCPVId(cid, pid, vid);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("删除cpv错误：" + e);
            new JsonResult(false, "删除失败").toJson(response);
        }
    }

    /**
     * 读取类目属性和值列表
     * 属性值通过中文逗号隔开，属性值的ID用英文逗号隔开
     *
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/category/cpv/list/{categoryId}")
    public void getCPVList(@PathVariable("categoryId") int categoryId, HttpServletResponse response) throws IOException {
        List<CpvRow> cpvList = new LinkedList<CpvRow>();
        List<CategoryProperty> categoryProperties = categoryPropertyService.queryCategoryPropertyByCategoryId(categoryId);
        for (CategoryProperty categoryProperty : categoryProperties) {
            List<CategoryPropertyValue> categoryPropertyValues = categoryPropertyService.getCategoryPropertyValues(categoryId, categoryProperty.getPropertyId());
            StringBuilder valueList = new StringBuilder();
            int i = 0;
            for (CategoryPropertyValue categoryPropertyValue : categoryPropertyValues) {
                if (i != 0) {
                    valueList.append("，"); //值用逗号分开给用户查看，如果这个属性没有值，则是一个空字符串
                }
                valueList.append(categoryPropertyService.getValueById(categoryPropertyValue.getValueId()).getValueName());
                i++;
            }
            CpvRow cpvListJson = new CpvRow(categoryProperty.getId(), categoryId, categoryProperty.getPropertyId(), categoryPropertyService.getPropertyById(categoryProperty.getPropertyId()).getName(), valueList.toString(), categoryProperty.getPropertyType(), categoryProperty.isMultiValue(), categoryProperty.getPriority(), categoryProperty.isCompareable());
            cpvList.add(cpvListJson);
        }
        new JsonResult(true).addData("totalCount", cpvList.size()).addData("result", cpvList).toJson(response);
    }


    /**
     * 读取某个类目下的属性值细节对象列表
     *
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/category/pvds/{categoryId}")
    public void getPVDetailList(@PathVariable("categoryId") int categoryId, HttpServletResponse response) throws IOException {
        List<PropertyValueDetailNamed> pvdList = new LinkedList<PropertyValueDetailNamed>();
        List<CategoryPropertyValue> categoryPropertyValues = categoryPropertyService.getCategoryPropertyValues(categoryId);
        for (CategoryPropertyValue categoryPropertyValue : categoryPropertyValues) {
            PropertyValueDetail propertyValueDetail = categoryPropertyService.getPropertyValueDetail(categoryPropertyValue.getPropertyId(), categoryPropertyValue.getValueId());
            PropertyValueDetailNamed propertyValueDetailNamed = new PropertyValueDetailNamed();
            propertyValueDetailNamed.setProperty(categoryPropertyService.getPropertyById(categoryPropertyValue.getPropertyId()).getName());
            propertyValueDetailNamed.setValue(categoryPropertyService.getValueById(categoryPropertyValue.getValueId()).getValueName());
            if (propertyValueDetail != null) {
                propertyValueDetailNamed.setPictureUrl(propertyValueDetail.getPictureUrl());
                propertyValueDetailNamed.setDescription(propertyValueDetail.getDescription());
            } else {
                propertyValueDetailNamed.setPictureUrl("");
                propertyValueDetailNamed.setDescription("");
            }
            pvdList.add(propertyValueDetailNamed);
        }
        new JsonResult(true).addData("totalCount", pvdList.size()).addData("result", pvdList).toJson(response);

    }

    /**
     * 更新属性属性值细节对象，先查询，如果不存在就直接添加，存在则更新
     *
     * @param pvd
     */
    @RequestMapping(value = "/category/pvd/update", method = RequestMethod.POST)
    @Permission("更新类目属性值细节")
    public void updatePV(PropertyValueDetailNamed pvd, HttpServletResponse response) throws IOException {
        try {
            Property property = categoryPropertyService.getPropertyByName(pvd.getProperty());
            Value value = categoryPropertyService.getValueByName(pvd.getValue());
            PropertyValueDetail propertyValueDetail = categoryPropertyService.getPropertyValueDetail(property.getId(), value.getId());
            if (propertyValueDetail == null) {
                propertyValueDetail = new PropertyValueDetail();
                propertyValueDetail.setPropertyId(property.getId());
                propertyValueDetail.setValueId(value.getId());
                propertyValueDetail.setDescription(pvd.getDescription());
                propertyValueDetail.setPictureUrl(pvd.getPictureUrl());
                categoryPropertyService.createCategoryPropertyValueDetail(propertyValueDetail);
            } else {
                propertyValueDetail.setDescription(pvd.getDescription());
                propertyValueDetail.setPictureUrl(pvd.getPictureUrl());
                categoryPropertyService.updateCategoryPropertyValueDetail(propertyValueDetail);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("设置属性细节错误：" + e);
            new JsonResult(false, "设置失败").toJson(response);
        }
    }


    private boolean is_Brand_Property(int propertyId) {
        Property property = categoryPropertyService.getPropertyById(propertyId);
        String property_name = property.getName();
        return property_name.equals("品牌") ? true : false;
    }

    /**
     * 检查品牌属性是否可修改
     *
     * @param propertyId
     * @param name
     * @return
     */
    private boolean checkIfCanUpdate(int propertyId, String name) {
        boolean update;
        boolean isBrand = is_Brand_Property(propertyId);
        boolean updateToBrand = name.equals("品牌");
        if (isBrand && updateToBrand) {
            update = true;
        } else if (isBrand && !updateToBrand) {
            update = false;
        } else if (!isBrand && updateToBrand) {
            update = false;
        } else {
            update = true;
        }
        return update;
    }

}
