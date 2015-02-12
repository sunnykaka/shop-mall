package com.kariqu.productmanager.helper;

import java.util.regex.Pattern;

/**
 * User: Asion
 * Date: 13-4-17
 * Time: 下午3:08
 */
public class ProductQuery {

    private static final Pattern pattern = Pattern.compile("\\d+");


    private int start;

    private int limit;

    private int categoryId;

    private int customerId;

    private int brandId;

    private String search;

    private int storeId;


    public int getPageNo() {
        return start / limit + 1;
    }


    public boolean isProductId() {
        return pattern.matcher(search).matches();
    }


    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }
}
