package com.kariqu.securitymanager.helper;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-6-5
 * Time: 上午9:41
 */
public class RoleVo {

    private String functionSet;

    private String functionSetName;

    private boolean hasExist;

    public RoleVo() {}

    public RoleVo(String functionSet, String functionSetName) {
        this.functionSet = functionSet;
        this.functionSetName = functionSetName;
    }

    public RoleVo(String functionSet, String functionSetName, boolean hasExist) {
        this.functionSet = functionSet;
        this.functionSetName = functionSetName;
        this.hasExist = hasExist;
    }

    public boolean isHasExist() {
        return hasExist;
    }

    public void setHasExist(boolean hasExist) {
        this.hasExist = hasExist;
    }

    public String getFunctionSetName() {
        return functionSetName;
    }

    public void setFunctionSetName(String functionSetName) {
        this.functionSetName = functionSetName;
    }

    public String getFunctionSet() {
        return functionSet;
    }

    public void setFunctionSet(String functionSet) {
        this.functionSet = functionSet;
    }

}
