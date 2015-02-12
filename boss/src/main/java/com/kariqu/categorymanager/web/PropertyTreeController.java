package com.kariqu.categorymanager.web;

import com.kariqu.categorycenter.domain.model.*;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryProperty;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryPropertyValue;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.service.*;
import com.kariqu.categorycenter.domain.util.PropertyValueUtil;
import com.kariqu.categorymanager.helper.*;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 属性树控制器
 * User: Asion
 * Date: 11-11-6
 * Time: 下午11:21
 */
@Controller
public class PropertyTreeController {

    private final Log logger = LogFactory.getLog(PropertyTreeController.class);

    @Autowired
    private CategoryPropertyService categoryPropertyService;

    @Autowired
    private NavigateCategoryService navigateCategoryService;

    @Autowired
    private NavigateCategoryPropertyService navigateCategoryPropertyService;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private CategoryAssociationService categoryAssociationService;

    /**
     * 通过某个后台类目构建属性和值选择树，默认选中
     * 用于在从某个父亲类目建立子类目的时候给子类目设置属性和值，父亲类目包含儿子的所有属性和值
     * 儿子只要选择继承就可以了，儿子可以自己删除属性，但是不能新增和修改
     * <p/>
     * 树上的checkbox是否选中是按照这样规则判断：
     * 用父亲类目建立树，全部用pid,vid判断，如果儿子有某个属性，或者在某个属性上有某个值则选中
     * <p/>
     * 所以，如果父亲类目变化了，儿子一旦某个属性不匹配，或者值不存在就会取消选中
     * <p/>
     *
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/category/property/tree/check/{categoryId}")
    public
    @ResponseBody
    List<PropertyCheckTreeJson> propertyCheckTree(@PathVariable("categoryId") int categoryId) {
        List<CategoryProperty> oldCP = categoryPropertyService.queryCategoryPropertyByCategoryId(categoryId);
        List<Integer> oldPropertyIds = new LinkedList<Integer>();//已有的属性
        for (CategoryProperty categoryProperty : oldCP) {
            oldPropertyIds.add(categoryProperty.getPropertyId());
        }
        ProductCategory category = productCategoryService.getProductCategoryById(categoryId);
        int parentId = category.getParent().getId();//取到父亲的类目ID
        List<CategoryProperty> categoryProperties = categoryPropertyService.queryCategoryPropertyByCategoryId(parentId);
        List<PropertyCheckTreeJson> nodeList = new LinkedList<PropertyCheckTreeJson>();

        for (CategoryProperty categoryProperty : categoryProperties) {
            List<Integer> oldValueIds = new LinkedList<Integer>();//已有的值
            List<CategoryPropertyValue> oldCPV = categoryPropertyService.getCategoryPropertyValues(categoryId, categoryProperty.getPropertyId());
            for (CategoryPropertyValue categoryPropertyValue : oldCPV) {
                oldValueIds.add(categoryPropertyValue.getValueId());
            }

            Property property = categoryPropertyService.getPropertyById(categoryProperty.getPropertyId());
            List<CategoryPropertyValue> categoryPropertyValues = categoryPropertyService.getCategoryPropertyValues(parentId, categoryProperty.getPropertyId());
            Set<Value> valueSet = new HashSet<Value>();
            for (CategoryPropertyValue categoryPropertyValue : categoryPropertyValues) {
                valueSet.add(categoryPropertyService.getValueById(categoryPropertyValue.getValueId()));
            }

            nodeList.add(ExtNodeBuilder.buildPropertyCheckTreeNode(oldPropertyIds, property, valueSet, oldValueIds));
        }
        return nodeList;
    }


    /**
     * 后台类目的属性树
     *
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/category/property/tree/{categoryId}")
    public
    @ResponseBody
    List<PropertyTreeJson> propertyTree(@PathVariable("categoryId") int categoryId) {
        List<CategoryProperty> categoryProperties = categoryPropertyService.queryCategoryPropertyByCategoryId(categoryId);
        List<PropertyTreeJson> nodeList = new LinkedList<PropertyTreeJson>();
        for (CategoryProperty categoryProperty : categoryProperties) {
            List<CategoryPropertyValue> categoryPropertyValues = categoryPropertyService.getCategoryPropertyValues(categoryId, categoryProperty.getPropertyId());
            Property property = categoryPropertyService.getPropertyById(categoryProperty.getPropertyId());
            Set<Value> valueSet = new HashSet<Value>();
            for (CategoryPropertyValue categoryPropertyValue : categoryPropertyValues) {
                valueSet.add(categoryPropertyService.getValueById(categoryPropertyValue.getValueId()));
            }
            nodeList.add(ExtNodeBuilder.buildPropertyTreeNode(property, valueSet));
        }
        return nodeList;
    }


    /**
     * 前台类目聚合属性树
     *
     * @param navId
     * @return
     */
    @RequestMapping(value = "/navigate/category/property/tree/aggregation/{navId}")
    public
    @ResponseBody
    List<PropertyTreeJson> aggregationPropertyTree(@PathVariable("navId") int navId) {
        NavigateCategory navigateCategory = navigateCategoryService.getNavigateCategory(navId);
        List<PropertyTreeJson> nodeList = new LinkedList<PropertyTreeJson>();
        Map<Integer, Set<Integer>> pvMap = parseConditions(navigateCategory.getConditions());
        for (Map.Entry<Integer, Set<Integer>> entry : pvMap.entrySet()) {
            Integer pid = entry.getKey();
            Set<Integer> valueIds = entry.getValue();
            Set<Value> valueSet = new LinkedHashSet<Value>();
            for (Integer valueId : valueIds) {
                valueSet.add(categoryPropertyService.getValueById(valueId));
            }
            Property property = categoryPropertyService.getPropertyById(pid);
            nodeList.add(ExtNodeBuilder.buildPropertyTreeNode(property, valueSet));
        }
        return nodeList;
    }

    /**
     * 前台类目属性树
     *
     * @param navId
     * @return
     */
    @RequestMapping(value = "/navigate/category/property/tree/{navId}")
    public
    @ResponseBody
    List<PropertyTreeJson> associationPropertyTree(@PathVariable("navId") int navId) {
        List<NavCategoryProperty> navCategoryProperties = navigateCategoryPropertyService.queryNavCategoryProperties(navId);
        List<PropertyTreeJson> nodeList = new LinkedList<PropertyTreeJson>();
        for (NavCategoryProperty navCategoryProperty : navCategoryProperties) {
            List<NavCategoryPropertyValue> navCategoryPropertyValues = navigateCategoryPropertyService.queryNavCategoryPropertyValues(navId, navCategoryProperty.getPropertyId());
            Property property = categoryPropertyService.getPropertyById(navCategoryProperty.getPropertyId());
            Set<Value> valueSet = new LinkedHashSet<Value>();
            for (NavCategoryPropertyValue navCategoryPropertyValue : navCategoryPropertyValues) {
                valueSet.add(categoryPropertyService.getValueById(navCategoryPropertyValue.getValueId()));
            }
            nodeList.add(ExtNodeBuilder.buildPropertyTreeNode(property, valueSet));
        }
        return nodeList;
    }

    /**
     * 展示前台类目的属性
     *
     * @param navId
     * @return
     */
    @RequestMapping(value = "/navigate/category/property/tree/forSearch/{navId}")
    public
    @ResponseBody
    List<PropertyCheckJson> associationCheckedPropertyTree(@PathVariable("navId") int navId) {
        List<NavCategoryProperty> navCategoryProperties = navigateCategoryPropertyService.queryNavCategoryProperties(navId);
        List<PropertyCheckJson> nodeList = new LinkedList<PropertyCheckJson>();
        for (NavCategoryProperty navCategoryProperty : navCategoryProperties) {
            Property property = categoryPropertyService.getPropertyById(navCategoryProperty.getPropertyId());
            PropertyCheckJson node = new PropertyCheckJson();
            node.setId(property.getId());
            node.setLeaf(true);
            node.setText(property.getName());
            nodeList.add(node);
        }
        return nodeList;
    }

    /**
     * 前台类目已筛选属性
     *
     * @param navId
     * @return
     */
    @RequestMapping(value = "/navigate/category/property/tree/searchable/{navId}")
    public
    @ResponseBody
    List<PropertyTreeJson> associationPropertyTreeSearchable(@PathVariable("navId") int navId) {
        List<NavCategoryProperty> navCategoryProperties = navigateCategoryPropertyService.queryNavCategoryPropertiesSearchable(navId);
        List<PropertyTreeJson> nodeList = new LinkedList<PropertyTreeJson>();
        for (NavCategoryProperty navCategoryProperty : navCategoryProperties) {
            List<NavCategoryPropertyValue> navCategoryPropertyValues = navigateCategoryPropertyService.queryNavCategoryPropertyValues(navId, navCategoryProperty.getPropertyId());
            Property property = categoryPropertyService.getPropertyById(navCategoryProperty.getPropertyId());
            Set<Value> valueSet = new HashSet<Value>();
            for (NavCategoryPropertyValue navCategoryPropertyValue : navCategoryPropertyValues) {
                valueSet.add(categoryPropertyService.getValueById(navCategoryPropertyValue.getValueId()));
            }
            nodeList.add(ExtNodeBuilder.buildPropertyTreeNode(property, valueSet));
        }
        return nodeList;
    }

    /**
     * 生成某个前台类目的属性和值选择树，前台类目会关联多个后台类目节点，所以要查询出关联的后台ID
     * 然后合并属性和值
     *
     * @param navId
     * @return
     */
    @RequestMapping(value = "/navigate/category/property/tree/check/{navId}")
    public
    @ResponseBody
    List<PropertyCheckTreeJson> associationPropertyCheckTree(@PathVariable("navId") int navId) {
        NavigateCategory navigateCategory = navigateCategoryService.getNavigateCategory(navId);
        List<NavCategoryProperty> oldNcp = navigateCategoryPropertyService.queryNavCategoryProperties(navId);
        List<Integer> oldPropertyIds = new LinkedList<Integer>();//已有的属性
        for (NavCategoryProperty navCategoryProperty : oldNcp) {
            oldPropertyIds.add(navCategoryProperty.getPropertyId());
        }
        List<PropertyCheckTreeJson> nodeList = new LinkedList<PropertyCheckTreeJson>();
        Map<Property, Set<Value>> mergeMap = getMergePVMap(navId);

        Map<Integer, Set<Integer>> pvMap = parseConditions(navigateCategory.getConditions());

        for (Map.Entry<Property, Set<Value>> propertyListEntry : mergeMap.entrySet()) {
            Property property = propertyListEntry.getKey();
            //如果类目已经聚合了，则选择树中不能出现
            if (!pvMap.containsKey(property.getId())) {
                List<NavCategoryPropertyValue> navCategoryPropertyValues = navigateCategoryPropertyService.queryNavCategoryPropertyValues(navId, property.getId());//已有的值
                List<Integer> oldValueIds = new LinkedList<Integer>();
                for (NavCategoryPropertyValue navCategoryPropertyValue : navCategoryPropertyValues) {
                    oldValueIds.add(navCategoryPropertyValue.getValueId());
                }
                nodeList.add(ExtNodeBuilder.buildPropertyCheckTreeNode(oldPropertyIds, property, propertyListEntry.getValue(), oldValueIds));
            }

        }
        return nodeList;
    }


    /**
     * 用于前台类目聚合选择的树
     *
     * @param navId
     * @return
     */
    @RequestMapping(value = "/navigate/category/property/tree/forAggregation/{navId}")
    public
    @ResponseBody
    List<PropertyCheckTreeJson> aggregationPropertyCheckTree(@PathVariable("navId") int navId) {
        List<Integer> oldPropertyIds = new LinkedList<Integer>();//已有的属性
        List<Integer> oldValueIds = new LinkedList<Integer>(); //已有属性值

        NavigateCategory navigateCategory = navigateCategoryService.getNavigateCategory(navId);

        Map<Integer, Set<Integer>> pvMap = parseConditions(navigateCategory.getConditions());
        oldPropertyIds.addAll(pvMap.keySet());

        List<PropertyCheckTreeJson> nodeList = new LinkedList<PropertyCheckTreeJson>();
        Map<Property, Set<Value>> mergeMap = getMergePVMap(navId);
        for (Map.Entry<Property, Set<Value>> propertyListEntry : mergeMap.entrySet()) {
            Property property = propertyListEntry.getKey();
            Set<Integer> set = pvMap.get(property.getId());
            if (set != null) {
                oldValueIds.addAll(set);
            }
            nodeList.add(ExtNodeBuilder.buildPropertyCheckTreeNode(oldPropertyIds, property, propertyListEntry.getValue(), oldValueIds));
        }
        return nodeList;
    }

    private Map<Integer, Set<Integer>> parseConditions(String conditions) {
        Map<Integer, Set<Integer>> pvMap = Collections.emptyMap();
        if (StringUtils.isNotEmpty(conditions)) {
            String[] pvArray = conditions.split(",");
            Long[] longArray = new Long[pvArray.length];
            for (int i = 0; i < pvArray.length; i++) {
                longArray[i] = Long.valueOf(pvArray[i]);
            }
            pvMap = parsePVMap(longArray);
        }
        return pvMap;
    }

    /**
     * 合并关联的类目的属性和值，得到一个map,key是属性，value是属性值的列表
     *
     * @param navId
     * @return
     */
    private Map<Property, Set<Value>> getMergePVMap(int navId) {
        List<Integer> categoryIds = categoryAssociationService.queryAssociationByNavCategoryId(navId);
        Map<Property, Set<Value>> mergeMap = new HashMap<Property, Set<Value>>(); //合并的属性值
        for (Integer categoryId : categoryIds) {
            List<CategoryProperty> categoryProperties = categoryPropertyService.queryCategoryPropertyByCategoryId(categoryId);
            for (CategoryProperty categoryProperty : categoryProperties) {
                Property property = categoryPropertyService.getPropertyById(categoryProperty.getPropertyId());
                List<CategoryPropertyValue> categoryPropertyValues = categoryPropertyService.getCategoryPropertyValues(categoryId, property.getId());
                Set<Value> valueList = new HashSet<Value>();//用Set保证不重复
                for (CategoryPropertyValue categoryPropertyValue : categoryPropertyValues) {
                    valueList.add(categoryPropertyService.getValueById(categoryPropertyValue.getValueId()));
                }
                if (mergeMap.get(property) == null) {
                    mergeMap.put(property, valueList);
                } else {
                    mergeMap.get(property).addAll(valueList);//如果存在了，就追加
                }
            }
        }
        return mergeMap;
    }

    /**
     * 保存后台类目选择继承了父亲的属性和值，注意要保留属性的类型和值类型
     * 通过调出父类目的属性和值去和客户端传过来的id比对，先判断属性是否被选，如果被选，就判断里面是否有值，这样就可确定客户端选择了什么属性和值
     *
     * @param categoryId
     * @param valueIds
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/category/property/inherited", method = RequestMethod.POST)
    @Permission("选择了属性和值")
    public void saveCategoryPropertyValue(int categoryId, Long[] valueIds, HttpServletResponse response) throws IOException {
        try {
            //如果前台多次选择继承保存，后台每次都清空即可避免重复
            //清空类目属性和属性值
            categoryPropertyService.deleteCategoryPropertyByCategoryId(categoryId);

            /**
             * 解析选中的PidVid
             * pid1:[vid1-1,vid1-2,vid1-3],pid2:[vid2-1,vid2-2,vid2-3]
             */
            Map<Integer, Set<Integer>> pvMap = parsePVMap(valueIds);

            ProductCategory category = productCategoryService.getProductCategoryById(categoryId);
            int parentId = category.getParent().getId();//取到父亲的类目ID
            //查询所有父亲的属性
            List<CategoryProperty> categoryProperties = categoryPropertyService.queryCategoryPropertyByCategoryId(parentId);

            //遍历父亲的属性，如果被选了就换个主人保存起来
            for (CategoryProperty categoryProperty : categoryProperties) {
                int pid = categoryProperty.getPropertyId();
                //如果选中了父亲的属性
                if (pvMap.containsKey(pid)) {
                    //添加属性
                    categoryProperty.setCategoryId(categoryId);
                    categoryPropertyService.createCategoryProperty(categoryProperty);

                    //遍历选中的父亲某个属性的值
                    List<CategoryPropertyValue> categoryPropertyValues = categoryPropertyService.getCategoryPropertyValues(parentId, pid);
                    for (CategoryPropertyValue categoryPropertyValue : categoryPropertyValues) {
                        //如果选中了父亲的某个属性值
                        if (pvMap.containsKey(pid) && pvMap.get(pid).contains(categoryPropertyValue.getValueId())) {
                            //添加属性值
                            categoryPropertyValue.setCategoryId(categoryId);
                            categoryPropertyService.createCategoryPropertyValue(categoryPropertyValue);
                        }
                    }
                }
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("后台类目继承属性和值错误：" + e);
            new JsonResult(false, "设定失败").toJson(response);
        }

    }

    /**
     * 保存前台类目聚合后台类目后设定的属性和值
     * <p/>
     * 新的关联会保留原来关联的设置，比如优先级，可筛选等
     *
     * @param navId
     * @param valueIds
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/navigate/category/property/association", method = RequestMethod.POST)
    @Permission("前台类目选择属性和值")
    public void saveNavCategoryPropertyValue(int navId, Long[] valueIds, HttpServletResponse response) throws IOException {
        try {

            //原来的类目属性和值，建立成map结构
            List<NavCategoryProperty> navCategoryProperties = navigateCategoryPropertyService.queryNavCategoryProperties(navId);
            Map<Integer, NavCategoryProperty> navPropertyMap = new HashMap<Integer, NavCategoryProperty>();
            Map<NavCategoryProperty, Map<Integer, NavCategoryPropertyValue>> navValueMap = new HashMap<NavCategoryProperty, Map<Integer, NavCategoryPropertyValue>>();
            for (NavCategoryProperty navCategoryProperty : navCategoryProperties) {
                navPropertyMap.put(navCategoryProperty.getPropertyId(), navCategoryProperty);
                List<NavCategoryPropertyValue> navCategoryPropertyValues = navigateCategoryPropertyService.queryNavCategoryPropertyValues(navId, navCategoryProperty.getPropertyId());
                Map<Integer, NavCategoryPropertyValue> map = new HashMap<Integer, NavCategoryPropertyValue>();
                for (NavCategoryPropertyValue navCategoryPropertyValue : navCategoryPropertyValues) {
                    map.put(navCategoryPropertyValue.getValueId(), navCategoryPropertyValue);
                }
                navValueMap.put(navCategoryProperty, map);
            }


            navigateCategoryPropertyService.deleteNavCategoryPropertyByNavCategoryId(navId);
            Map<Property, Set<Value>> mergePVMap = getMergePVMap(navId);

            Map<Integer, Set<Integer>> pvMap = parsePVMap(valueIds);


            for (Map.Entry<Property, Set<Value>> propertySetEntry : mergePVMap.entrySet()) {
                Property property = propertySetEntry.getKey();
                if (pvMap.containsKey(property.getId())) {
                    NavCategoryProperty np = new NavCategoryProperty();
                    np.setNavCategoryId(navId);
                    np.setPropertyId(property.getId());
                    NavCategoryProperty oldProperty = navPropertyMap.get(property.getId());
                    if (oldProperty != null) {
                        np.setPriority(oldProperty.getPriority());
                        np.setSearchable(oldProperty.isSearchable());
                    }
                    navigateCategoryPropertyService.createNavCategoryProperty(np);
                    Set<Value> valueSet = propertySetEntry.getValue();
                    for (Value value : valueSet) {
                        if (pvMap.containsKey(property.getId()) && pvMap.get(property.getId()).contains(value.getId())) {
                            NavCategoryPropertyValue npv = new NavCategoryPropertyValue();
                            npv.setNavCategoryId(navId);
                            npv.setPropertyId(property.getId());
                            npv.setValueId(value.getId());
                            if (oldProperty != null) {
                                Map<Integer, NavCategoryPropertyValue> map = navValueMap.get(oldProperty);
                                NavCategoryPropertyValue navCategoryPropertyValue = map.get(value.getId());
                                if (navCategoryPropertyValue != null) {
                                    npv.setPriority(navCategoryPropertyValue.getPriority());
                                }
                            }
                            navigateCategoryPropertyService.createNavCategoryPropertyValue(npv);
                        }
                    }
                }
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("为前台类目设定属性和值错误：" + e);
            new JsonResult(false, "设定失败").toJson(response);
        }

    }


    /**
     * 给前台类目设定聚合值，比如单柄炒锅这个类目需要聚合单柄这个属性值
     * 这些属性值以pidvid逗号隔开的字符串保存在前台类目的conditions字段中的
     *
     * @param navId
     * @param valueIds
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/navigate/category/property/aggregation", method = RequestMethod.POST)
    @Permission("前台类目聚合属性和值")
    public void saveNavCategoryAggregation(int navId, Long[] valueIds, HttpServletResponse response) throws IOException {
        try {
            NavigateCategory navigateCategory = navigateCategoryService.getNavigateCategory(navId);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < valueIds.length; i++) {
                if (i != 0) {
                    sb.append(",");
                }
                sb.append(valueIds[i]);
            }

            navigateCategory.setConditions(sb.toString());
            navigateCategoryService.updateNavigateCategory(navigateCategory);

            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("为前台类目设定属性和值错误：" + e);
            new JsonResult(false, "设定失败").toJson(response);
        }

    }

    /**
     * 编辑前台类目的属性，用于设置属性优先级
     *
     * @param navId
     * @return
     */
    @RequestMapping(value = "/navigate/category/cp/list/{navId}")
    public void navCPEditor(@PathVariable("navId") int navId, HttpServletResponse response) throws IOException {
        List<NavCPNamed> ncpRecord = new LinkedList<NavCPNamed>();
        List<NavCategoryProperty> navCategoryProperties = navigateCategoryPropertyService.queryNavCategoryProperties(navId);
        for (NavCategoryProperty navCategoryProperty : navCategoryProperties) {
            NavCPNamed record = new NavCPNamed();
            record.setId(navCategoryProperty.getId());
            record.setName(categoryPropertyService.getPropertyById(navCategoryProperty.getPropertyId()).getName());
            record.setPriority(navCategoryProperty.getPriority());
            ncpRecord.add(record);
        }
        new JsonResult(true).addData("totalCount", ncpRecord.size()).addData("result", ncpRecord).toJson(response);
    }


    /**
     * 编辑前台类目属性值的优先级
     *
     * @param navId
     * @param pid
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/navigate/category/cpv/list/{navId}/{pid}")
    public void navCPVEditor(@PathVariable("navId") int navId, @PathVariable("pid") int pid, HttpServletResponse response) throws IOException {
        List<NavCPVNamed> ncpvRecord = new LinkedList<NavCPVNamed>();
        List<NavCategoryPropertyValue> navCategoryPropertyValues = navigateCategoryPropertyService.queryNavCategoryPropertyValues(navId, pid);
        for (NavCategoryPropertyValue navCategoryPropertyValue : navCategoryPropertyValues) {
            NavCPVNamed record = new NavCPVNamed();
            record.setId(navCategoryPropertyValue.getId());
            record.setName(categoryPropertyService.getValueById(navCategoryPropertyValue.getValueId()).getValueName());
            record.setPriority(navCategoryPropertyValue.getPriority());
            ncpvRecord.add(record);
        }
        new JsonResult(true).addData("totalCount", ncpvRecord.size()).addData("result", ncpvRecord).toJson(response);
    }

    /**
     * 更新导航类目属性优先级
     *
     * @param priority
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/navigate/category/cp/priority/update", method = RequestMethod.POST)
    @Permission("修改前台类目属性优先级")
    public void updateNavCP(int id, int priority, HttpServletResponse response) throws IOException {
        try {
            NavCategoryProperty ncp = navigateCategoryPropertyService.getNavCategoryPropertyById(id);
            ncp.setPriority(priority);
            navigateCategoryPropertyService.updateNavCategoryProperty(ncp);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("设置属性优先级出错：" + e);
            new JsonResult(false, "更新失败，请确保你输入合法").toJson(response);
        }
    }

    /**
     * 更新前台类目属性值的优先级
     *
     * @param id
     * @param priority
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/navigate/category/cpv/priority/update", method = RequestMethod.POST)
    @Permission("更改前台类目属性值优先级")
    public void updateNavCPV(int id, int priority, HttpServletResponse response) throws IOException {
        try {
            NavCategoryPropertyValue ncpv = navigateCategoryPropertyService.getNavCategoryPropertyValueById(id);
            ncpv.setPriority(priority);
            navigateCategoryPropertyService.updateNavCategoryPropertyValue(ncpv);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("设置属性值优先级出错：" + e);
            new JsonResult(false, "更新失败，请确保你输入合法").toJson(response);
        }
    }


    /**
     * 设置前台类目的筛选属性
     *
     * @param navId
     * @param propertyIds
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/navigate/category/property/setterSearchable", method = RequestMethod.POST)
    @Permission("设置前台搜索属性")
    public void setNavCategoryPropertySearchable(int navId, int[] propertyIds, HttpServletResponse response) throws IOException {
        try {
            // 现将之前筛选的属性设置为没有筛选
            navigateCategoryPropertyService.makeAllNavigateCategoryPropertyUnSearchable(navId);

            for (int propertyId : propertyIds) {
                if (propertyId == -1) {
                    continue;
                }
                navigateCategoryPropertyService.makeNavigateCategoryPropertySearchable(navId, propertyId);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("为前台类目设定筛选属性错误：" + e);
            new JsonResult(false, "设定失败").toJson(response);
        }
    }


    /**
     * 解析一串pidvid成pid-set<vid>的形式,pid是key，value是一串值ID
     *
     * @param valueIds
     * @return
     */
    private Map<Integer, Set<Integer>> parsePVMap(Long[] valueIds) {
        Map<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
        for (Long id : valueIds) {
            PropertyValueUtil.PV pv = PropertyValueUtil.parseLongToPidVid(id);
            if (map.get(pv.pid) != null) {
                map.get(pv.pid).add(pv.vid);
            } else {
                Set<Integer> set = new HashSet<Integer>();
                set.add(pv.vid);
                map.put(pv.pid, set);
            }
        }
        return map;
    }

}
