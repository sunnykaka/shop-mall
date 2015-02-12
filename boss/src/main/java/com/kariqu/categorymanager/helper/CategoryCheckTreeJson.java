package com.kariqu.categorymanager.helper;

/**
 * 前台关联后台的选择树节点数据类
 * User: Asion
 * Date: 11-7-19
 * Time: 下午2:55
 */
public class CategoryCheckTreeJson extends CheckedCategoryParentTreeJson {

    private boolean checked;


    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
