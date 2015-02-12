package com.kariqu.designcenter.domain.model;

/**
 * 模块实例参数细节，根据模块实例ID，我们可以解析出
 * pageId,shopId,prototypeId等信息，详情可以参考
 *
 * @author Tiger
 * @version 1.0
 * @see ModuleInstanceIdFactory
 * @since 12-12-26 下午3:02
 */
public class ModuleInstanceParamDetail {

    private Integer pageId;

    private Integer shopId;

    private Integer prototypeId;

    private boolean isHead;

    private boolean isFoot;

    private boolean isBody;

    private boolean isGlobal;

    public ModuleInstanceParamDetail(boolean head, boolean body, boolean foot, boolean global, Integer shopId, Integer pageId, Integer prototypeId) {
        isHead = head;
        isBody = body;
        isFoot = foot;
        isGlobal = global;
        this.shopId = shopId;
        this.pageId = pageId;
        this.prototypeId = prototypeId;
    }

    public Integer getPageId() {
        return pageId;
    }

    public Integer getShopId() {
        return shopId;
    }

    public Integer getPrototypeId() {
        return prototypeId;
    }

    public boolean isHead() {
        return isHead;
    }

    public boolean isFoot() {
        return isFoot;
    }

    public boolean isBody() {
        return isBody;
    }

    public boolean isGlobal() {
        return isGlobal;
    }
}
