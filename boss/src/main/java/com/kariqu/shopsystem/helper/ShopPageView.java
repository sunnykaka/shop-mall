package com.kariqu.shopsystem.helper;

import com.kariqu.designcenter.domain.model.PageType;
import com.kariqu.designcenter.domain.model.shop.PageStatus;

/**
 * 在界面上使用的店铺页面对象
 * User: Asion
 * Date: 12-5-18
 * Time: 上午9:16
 */
public class ShopPageView {

    private long id;

    private String name;

    private PageType pageType;

    private boolean release;

    private String title;

    private String keywords;

    private String description;

    private PageStatus pageStatus;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PageType getPageType() {
        return pageType;
    }

    public void setPageType(PageType pageType) {
        this.pageType = pageType;
    }

    public boolean isRelease() {
        return release;
    }

    public void setRelease(boolean release) {
        this.release = release;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PageStatus getPageStatus() {
        return pageStatus;
    }

    public void setPageStatus(PageStatus pageStatus) {
        this.pageStatus = pageStatus;
    }
}
