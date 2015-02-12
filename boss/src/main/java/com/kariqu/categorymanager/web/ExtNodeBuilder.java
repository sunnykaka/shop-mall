package com.kariqu.categorymanager.web;

import com.kariqu.categorycenter.domain.model.ProductCategory;
import com.kariqu.categorycenter.domain.model.Property;
import com.kariqu.categorycenter.domain.model.Value;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.util.PropertyValueUtil;
import com.kariqu.categorymanager.helper.*;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * User: Asion
 * Date: 11-8-13
 * Time: 上午9:32
 */
public class ExtNodeBuilder {

    /**
     * 构建可勾选的后台类目节点
     *
     * @param category
     * @param categoryIds
     * @return
     */
    public static CategoryCheckTreeJson buildCategoryCheckTreeJsonNode(boolean disable, ProductCategory category, List<Integer> categoryIds) {
        CategoryCheckTreeJson node = new CategoryCheckTreeJson();
        node.setDisabled(disable);
        node.setId(category.getId());
        node.setText(category.getName());
        if (category.getChildren().size() == 0) {
            node.setLeaf(true);
        }
        if (categoryIds.contains(node.getId())) {
            node.setChecked(true);
        }
        return node;
    }

    public static CheckedCategoryParentTreeJson buildCheckedCategoryParentTreeJsonNode(boolean disable, ProductCategory category) {
        CheckedCategoryParentTreeJson node = new CheckedCategoryParentTreeJson();
        node.setDisabled(disable);
        node.setId(category.getId());
        node.setText(category.getName());
        if (category.getChildren().size() == 0) {
            node.setLeaf(true);
        }
        return node;
    }

    //构建导航前台类目节点
    public static CategoryTreeJson buildCategoryTreeJsonNode(NavigateCategory navigateCategory) {
        CategoryTreeJson node = new CategoryTreeJson();
        node.setId(navigateCategory.getId());
        node.setText(navigateCategory.getName());
        if (navigateCategory.getChildren().size() == 0) {
            node.setLeaf(true);
        }
        if (StringUtils.isNotEmpty(navigateCategory.getSettings())) {
            boolean isHot = navigateCategory.settingsObject().isHot();
            node.setHot(isHot);
            if (isHot) {
                node.setText("<span style=\"color:red\">" + node.getText() + "</span>");
            }
        }
        return node;
    }

    //构建后台类目节点
    public static CategoryTreeJson buildCategoryTreeJsonNode(ProductCategory category) {
        CategoryTreeJson node = new CategoryTreeJson();
        node.setId(category.getId());
        node.setText(category.getName());
        if (category.getChildren().size() == 0) {
            node.setLeaf(true);
        }
        return node;
    }

    /**
     * 构建 筛选属性 选择树节点
     *
     * @param property
     * @param valueList
     * @return
     */
    public static PropertyCheckTreeJson buildPropertyCheckTreeNode(Property property, Set<Value> valueList) {
        PropertyCheckTreeJson propertyNode = new PropertyCheckTreeJson();
        propertyNode.setId(property.getId());
        propertyNode.setLeaf(false);
        propertyNode.setText(property.getName());
        List<PropertyCheckTreeJson> children = new LinkedList<PropertyCheckTreeJson>();
        for (Value value : valueList) {
            PropertyCheckTreeJson valueNode = new PropertyCheckTreeJson();
            valueNode.setId(value.getId());
            valueNode.setLeaf(true);
            valueNode.setText(value.getValueName());
            children.add(valueNode);
        }
        propertyNode.setChildren(children);
        return propertyNode;
    }

    /**
     * 构建属性树节点
     *
     * @param property
     * @param valueList
     * @return
     */
    public static PropertyTreeJson buildPropertyTreeNode(Property property, Set<Value> valueList) {
        PropertyTreeJson propertyNode = new PropertyTreeJson();
        propertyNode.setId(property.getId());
        propertyNode.setLeaf(false);
        propertyNode.setText(property.getName());
        List<PropertyTreeJson> children = new LinkedList<PropertyTreeJson>();
        for (Value value : valueList) {
            PropertyTreeJson valueNode = new PropertyTreeJson();
            valueNode.setId(value.getId());
            valueNode.setLeaf(true);
            valueNode.setText(value.getValueName());
            children.add(valueNode);
        }
        propertyNode.setChildren(children);
        return propertyNode;
    }

    /**
     * 构建属性选择树节点
     *
     * @param property  属性
     * @param valueList 值
     * @return
     */
    public static PropertyCheckTreeJson buildPropertyCheckTreeNode(List<Integer> oldPropertyIds, Property property, Set<Value> valueList, List<Integer> oldValueIds) {
        PropertyCheckTreeJson propertyNode = new PropertyCheckTreeJson();
        propertyNode.setId(property.getId());
        boolean hasSet = oldPropertyIds.size() > 0;//判断是否设置过
        boolean allChildrenNotCheck = false;
        if (hasSet && !oldPropertyIds.contains(property.getId())) {//设置过的，但是却不包含现在这个属性，则取消选中
            propertyNode.setChecked(false);
            allChildrenNotCheck = true; //这下面的所有值取消选中
        }
        propertyNode.setLeaf(false);
        propertyNode.setText(property.getName());
        List<PropertyCheckTreeJson> children = new LinkedList<PropertyCheckTreeJson>();
        for (Value value : valueList) {
            PropertyCheckTreeJson valueNode = new PropertyCheckTreeJson();
            //值的叶子节点包含了属性和值
            valueNode.setId(PropertyValueUtil.mergePidVidToLong(property.getId(), value.getId()));

            if (allChildrenNotCheck) {
                valueNode.setChecked(false);
            } else if (hasSet && oldPropertyIds.contains(property.getId()) && !oldValueIds.contains(value.getId())) {
                valueNode.setChecked(false); //设置过，也包含这个属性，但是值却不包含现在这个值，则取消选中
            }
            valueNode.setLeaf(true);
            valueNode.setText(value.getValueName());
            children.add(valueNode);
        }
        propertyNode.setChildren(children);
        return propertyNode;
    }
}
