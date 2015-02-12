package com.kariqu.suppliersystem.common;

import com.kariqu.common.uri.URLBrokerFactory;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.velocity.VelocityView;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

/**
 * 工程里的VM视图解析，可注入一些在页面上公共的类
 * User: Asion
 * Date: 12-8-16
 * Time: 下午12:52
 */
public class KariquVelocityViewResolver extends VelocityViewResolver {

    /**
     * 在vm页面引用UrlBroker的key
     */
    private static final String URL_BROKER_KEY = "urlBroker";

    /**
     * 在vm页面引用UrlBroker的key，可配置
     */
    private String brokerKey;

    private URLBrokerFactory urlBrokerBean;

    public URLBrokerFactory getUrlBrokerBean() {
        return urlBrokerBean;
    }

    public void setUrlBrokerBean(URLBrokerFactory urlBrokerBean) {
        this.urlBrokerBean = urlBrokerBean;
    }

    public String getBrokerKey() {
        return brokerKey;
    }

    public void setBrokerKey(String brokerKey) {
        this.brokerKey = brokerKey;
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        VelocityView view = (VelocityView) super.buildView(viewName);
        if (brokerKey == null) {
            view.addStaticAttribute(URL_BROKER_KEY, urlBrokerBean);
        } else {
            view.addStaticAttribute(brokerKey, urlBrokerBean);
        }
        return view;
    }


}
