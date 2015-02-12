package com.kariqu.securitysystem.domain;

import java.io.Serializable;

/**
 * URL权限，如果系统只是通过URL到达功能接口，可以直接在这里拦截处理
 * 比如类目删除的URL是 category/delete，用正则匹配即可
 * User: Asion
 * Date: 11-11-16
 * Time: 上午10:44
 */
public class UrlPermission extends Permission implements Serializable {

    /**
     * 资源路径+操作
     */
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
