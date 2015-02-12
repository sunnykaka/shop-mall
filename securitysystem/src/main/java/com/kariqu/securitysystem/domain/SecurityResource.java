package com.kariqu.securitysystem.domain;

/**
 * 资源安全对象需要实现的接口
 * 安全系统通过返回的名字来注册所有的安全资源
 * User: Asion
 * Date: 11-11-16
 * Time: 下午11:34
 */
public interface SecurityResource {

    String getResourceName();
}
