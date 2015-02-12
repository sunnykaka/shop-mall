package com.kariqu.securitysystem.domain;

import java.io.Serializable;

/**
 * 表示系统暴露的功能接口权限
 * User: Asion
 * Date: 11-11-16
 * Time: 上午10:42
 */
public class ServicePermission extends Permission implements Serializable {

    /**
     * 接口服务名
     */
    private String serviceName;

    /**
     * 操作
     */
    private String operation;


    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }


}
