package com.kariqu.suppliersystem.supplierManager.vo;

import com.kariqu.securitysystem.domain.UrlPermission;

import java.util.List;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-4-27
 * Time: 下午6:36
 */
public class PermissionVo {

    private String category;
    private List<UrlPermission> urlPermissionList;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<UrlPermission> getUrlPermissionList() {
        return urlPermissionList;
    }

    public void setUrlPermissionList(List<UrlPermission> urlPermissionList) {
        this.urlPermissionList = urlPermissionList;
    }
}
