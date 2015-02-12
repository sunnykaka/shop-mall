package com.kariqu.buyer.web.common;

import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.productcenter.service.ProductPictureResolver;
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
     * 商品图片地址解析类在vm中的key
     */
    private static final String IMG_RESOLVER_KEY = "imgResolver";

    /**
     * 在vm页面引用UrlBroker的key，可配置
     */
    private String brokerKey;

    private String imgResolverKey;

    private URLBrokerFactory urlBrokerBean;

    private ProductPictureResolver productPictureResolver;

    /**
     * 上线标志，通过这个标志，页面可以判断是否加入统计代码等
     */
    private boolean online;

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

    public String getImgResolverKey() {
        return imgResolverKey;
    }

    public void setImgResolverKey(String imgResolverKey) {
        this.imgResolverKey = imgResolverKey;
    }

    public ProductPictureResolver getProductPictureResolver() {
        return productPictureResolver;
    }

    public void setProductPictureResolver(ProductPictureResolver productPictureResolver) {
        this.productPictureResolver = productPictureResolver;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        VelocityView view = (VelocityView) super.buildView(viewName);
        view.addStaticAttribute("online", online);
        view.addStaticAttribute(brokerKey == null ? URL_BROKER_KEY : brokerKey, urlBrokerBean);
        view.addStaticAttribute(imgResolverKey == null ? IMG_RESOLVER_KEY : imgResolverKey, productPictureResolver);
        return view;
    }


}
