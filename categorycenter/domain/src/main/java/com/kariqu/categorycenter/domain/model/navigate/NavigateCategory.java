package com.kariqu.categorycenter.domain.model.navigate;

import com.kariqu.categorycenter.domain.model.DescendantManager;
import com.kariqu.common.json.JsonUtil;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 导航类目 用于买家导航商品使用，导航类目可以关联后台后台类目
 * <p/>
 * 导航类目的存在基于这样一个事实：后台类目可能是行业标准类目，它对最终用户是不可理解的
 * 所以用导航类目来对用户可理解的分类进行建模，导航类目再和后台类目建立关联。
 * <p/>
 * 导航类目用来服务用户，后台类目用来发布商品
 *
 * @Author: Tiger
 * @Since: 11-7-4 下午10:22
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class NavigateCategory extends DescendantManager {

    private int id;

    private String name;

    private String description;

    /**
     * 这个字段保存类目的关键索引，比如炒锅的关键字可能是：炒，炒菜，做饭等
     * 关键字可以用中文或者英文的逗号隔开 @see com.kariqu.searchengine.KeyWordTransformer
     * 搜索引擎会dump这个关键字进行索引
     */
    private String keyWord;

    /**
     * 父类目
     */
    private NavigateCategory parent;

    /**
     * 表示一些条件
     */
    private String conditions;

    /**
     * 类目排序优先级，数字越小越靠前
     * 用于在前台显示的时候判断谁靠前，谁靠后，优先级设置在同一个父类目下有意义
     * 比如厨房用品下的所有子类目指定优先级，所有的根类目指定优先级
     */
    private int priority;

    /**
     * 关联的后台类目ID
     * 数据库中会有一个关联表保存它们的多对多关系
     */
    private List<Integer> categoryIds;

    private List<NavigateCategory> children = new LinkedList<NavigateCategory>();

    /** 一些设置，比如主图，推荐，用Json保存 */
    private String settings;

    /** 当前类目是否被选中 */
    private boolean open = false;
    /** 当前类目是否被选中 */
    public boolean isOpen() {
        return open;
    }
    /** 当前类目是否被选中 */
    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public NavigateCategory getParent() {
        return parent;
    }

    public void setParent(NavigateCategory parent) {
        this.parent = parent;
    }

    public List<NavigateCategory> getChildren() {
        return children;
    }

    public void setChildren(List<NavigateCategory> children) {
        this.children = children;
        for (NavigateCategory child : children) {
            this.addDescendantCategoryId(child.getId());
            child.setParent(this);
        }
        if (this.getParent() != null) {
            this.getParent().addDescendantCategoryId(this.getDescendantIds());
        }
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NavigateCategory that = (NavigateCategory) o;

        if (id != that.id) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (keyWord != null ? !keyWord.equals(that.keyWord) : that.keyWord != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (keyWord != null ? keyWord.hashCode() : 0);
        return result;
    }

    public NavigateCategorySettings settingsObject() {
        if (StringUtils.isEmpty(this.settings)) {
            NavigateCategorySettings navigateCategorySettings = new NavigateCategorySettings();
            navigateCategorySettings.setNavId(this.id);
            return navigateCategorySettings;
        }
        return JsonUtil.json2Object(this.settings, NavigateCategorySettings.class);
    }
}
