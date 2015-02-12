package com.kariqu.designcenter.domain.open.module;

import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 类目频道数据
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-8-22
 *        Time: 下午5:04
 */
public class CategoryChannelData {

    /** 图片地址 */
    private List<String> pictureUrl;

    /** 图片的链接地址 */
    private List<String> linkUrl;

    /** 频道类目 */
    private String rootCategoryName = "";

    /** 频道类目ID */
    private int rootCategoryId;

    /** 频道类目的子类目 */
    private List<NavigateCategory> child = new ArrayList<NavigateCategory>(0);

    /** 各子类目的商品列表，key就是子类目的名字 */
    private Map<NavigateCategory, List<BasicProduct>> childProductMap = new LinkedHashMap<NavigateCategory, List<BasicProduct>>();

    public void putCategoryProduct(NavigateCategory category, BasicProduct product) {
        if (childProductMap.get(category) == null) {
            childProductMap.put(category, new ArrayList<BasicProduct>());
        }
        if (product != null) {
            childProductMap.get(category).add(product);
        }
    }

    public List<String> getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(List<String> pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public List<String> getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(List<String> linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getRootCategoryName() {
        return rootCategoryName;
    }

    public void setRootCategoryName(String rootCategoryName) {
        this.rootCategoryName = rootCategoryName;
    }

    public List<NavigateCategory> getChild() {
        return child;
    }

    public void setChild(List<NavigateCategory> child) {
        this.child = child;
    }

    public Map<NavigateCategory, List<BasicProduct>> getChildProductMap() {
        return childProductMap;
    }

    public void setChildProductMap(Map<NavigateCategory, List<BasicProduct>> childProductMap) {
        this.childProductMap = childProductMap;
    }

    public int getRootCategoryId() {
        return rootCategoryId;
    }

    public void setRootCategoryId(int rootCategoryId) {
        this.rootCategoryId = rootCategoryId;
    }

}
