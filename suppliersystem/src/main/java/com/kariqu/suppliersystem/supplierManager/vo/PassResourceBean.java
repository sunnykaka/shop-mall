package com.kariqu.suppliersystem.supplierManager.vo;

import java.util.LinkedList;
import java.util.List;

/**
 * 后台放过的资源管理bean
 * 放过的资源路径可以通过在spring的配置文件中配置
 * User: Asion
 * Date: 11-12-6
 * Time: 上午11:21
 */
public class PassResourceBean {

    /**
     * 静态资源放过
     */
    private List<String> staticPath = new LinkedList<String>();

    /**
     * 放过的url
     */
    private List<String> excludedPath = new LinkedList<String>();


    /**
     * 判断是否放行
     *
     * @param requestURL
     * @return
     */
    public boolean letItGo(String requestURL) {
        if (excludedPath.contains(requestURL)) {
            return true;
        }
        boolean staticFound = false;
        for (String path : staticPath) {
            if (requestURL.startsWith(path)) {
                staticFound = true;
                break;
            }
        }
        if (staticFound) {
            return true;
        }
        return false;
    }

    public List<String> getStaticPath() {
        return staticPath;
    }

    public void setStaticPath(List<String> staticPath) {
        this.staticPath = staticPath;
    }

    public List<String> getExcludedPath() {
        return excludedPath;
    }

    public void setExcludedPath(List<String> excludedPath) {
        this.excludedPath = excludedPath;
    }
}
